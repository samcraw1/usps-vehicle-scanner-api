package com.usps.scanner.leaderboard;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LeaderboardController.class)
class LeaderboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeaderboardService service;

    @Test
    void createUserReturnsEntry() throws Exception {
        when(service.getOrCreateUser("Sam"))
                .thenReturn(new LeaderboardEntry("Sam", 0L));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"Sam\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Sam"))
                .andExpect(jsonPath("$.bestTimeMs").value(0));
    }

    @Test
    void getUserReturnsEntry() throws Exception {
        when(service.getOrCreateUser("Sam"))
                .thenReturn(new LeaderboardEntry("Sam", 45000L));

        mockMvc.perform(get("/api/users/Sam"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Sam"))
                .andExpect(jsonPath("$.bestTimeMs").value(45000));
    }

    @Test
    void submitScoreReturnsCreated() throws Exception {
        when(service.submitScore(anyString(), anyLong()))
                .thenReturn(new Score(new User("Sam"), 45230L));

        mockMvc.perform(post("/api/scores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"Sam\",\"totalTimeMs\":45230}"))
                .andExpect(status().isCreated());

        verify(service, times(1)).submitScore("Sam", 45230L);
    }

    @Test
    void getLeaderboardReturnsList() throws Exception {
        when(service.getLeaderboard()).thenReturn(List.of(
                new LeaderboardEntry("SpeedDemon", 35000L),
                new LeaderboardEntry("MailMaster", 42000L)
        ));

        mockMvc.perform(get("/api/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("SpeedDemon"))
                .andExpect(jsonPath("$[0].bestTimeMs").value(35000))
                .andExpect(jsonPath("$[1].username").value("MailMaster"))
                .andExpect(jsonPath("$[1].bestTimeMs").value(42000));
    }
}
