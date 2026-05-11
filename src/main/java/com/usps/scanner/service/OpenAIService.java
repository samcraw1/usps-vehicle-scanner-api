package com.usps.scanner.service;

import com.usps.scanner.model.InspectionResponse;
import org.springframework.web.multipart.MultipartFile;

// Interface = a contract. Says "any class implementing this MUST provide an analyze() method."
// Why use an interface here?
// 1. Tests can mock the interface easily (no bytecode magic needed - works on any Java version).
// 2. We could swap implementations later (e.g., a ClaudeService or GeminiService) without
//    changing the controller code.
// 3. Demonstrates "dependency inversion" - controllers depend on abstractions, not concretions.
public interface OpenAIService {
    InspectionResponse analyze(MultipartFile image);
}
