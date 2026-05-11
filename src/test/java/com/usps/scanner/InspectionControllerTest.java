package com.usps.scanner;

import com.usps.scanner.controller.InspectionController;
import com.usps.scanner.model.HistoryEntry;
import com.usps.scanner.model.InspectionResponse;
import com.usps.scanner.model.Issue;
import com.usps.scanner.service.InspectionHistoryService;
import com.usps.scanner.service.OpenAIService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @WebMvcTest only loads the controller layer (not the full app).
// Faster than booting the whole Spring context. Perfect for unit-testing controllers.
@WebMvcTest(InspectionController.class)
class InspectionControllerTest {

    // MockMvc lets us simulate HTTP requests without a real server running.
    @Autowired
    private MockMvc mockMvc;

    // @MockBean replaces the real services with fake ones we control.
    // This is HUGE - means tests don't actually call OpenAI (no cost, no internet needed).
    @MockBean
    private OpenAIService openAIService;

    @MockBean
    private InspectionHistoryService historyService;

    @Test
    void healthEndpointReturnsOkStatus() throws Exception {
        when(historyService.size()).thenReturn(0);

        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.service").value("USPS Vehicle Scanner API"))
                .andExpect(jsonPath("$.historyCount").value(0));
    }

    @Test
    void healthEndpointReflectsHistoryCount() throws Exception {
        when(historyService.size()).thenReturn(7);

        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.historyCount").value(7));
    }

    @Test
    void inspectEndpointReturnsAnalysisAndRecordsHistory() throws Exception {
        // Build a fake response the mock OpenAI service will return.
        InspectionResponse fakeResponse = new InspectionResponse(
                "UNSAFE",
                List.of(new Issue("Cracked windshield", "Front", "HIGH")),
                "Vehicle has a cracked windshield."
        );
        when(openAIService.analyze(any())).thenReturn(fakeResponse);

        // Build a fake image upload.
        MockMultipartFile imageFile = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "fake-image-bytes".getBytes()
        );

        mockMvc.perform(multipart("/api/inspect").file(imageFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UNSAFE"))
                .andExpect(jsonPath("$.summary").value("Vehicle has a cracked windshield."))
                .andExpect(jsonPath("$.issues[0].issue").value("Cracked windshield"))
                .andExpect(jsonPath("$.issues[0].severity").value("HIGH"));

        // Verify the history service was called exactly once with our file.
        verify(historyService, times(1)).record(anyString(), anyLong(), any(InspectionResponse.class));
    }

    @Test
    void historyEndpointReturnsListOfEntries() throws Exception {
        InspectionResponse r1 = new InspectionResponse("PASS", List.of(), "All clear");
        InspectionResponse r2 = new InspectionResponse("ATTENTION", List.of(), "Minor issue");

        when(historyService.getAll()).thenReturn(List.of(
                new HistoryEntry("photo1.jpg", 100L, r1),
                new HistoryEntry("photo2.jpg", 200L, r2)
        ));

        mockMvc.perform(get("/api/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].filename").value("photo1.jpg"))
                .andExpect(jsonPath("$[0].result.status").value("PASS"))
                .andExpect(jsonPath("$[1].result.status").value("ATTENTION"));
    }

    @Test
    void deleteHistoryEndpointClearsAndReportsCount() throws Exception {
        when(historyService.size()).thenReturn(5);

        mockMvc.perform(delete("/api/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cleared").value(true))
                .andExpect(jsonPath("$.removed").value(5));

        // Verify clear() was actually called.
        verify(historyService, times(1)).clear();
    }
}
