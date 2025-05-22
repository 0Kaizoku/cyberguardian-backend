package com.cyberguardian.controller;

import com.cyberguardian.CyberGuardianApplication;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;

@SpringBootTest(classes = CyberGuardianApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MLModelIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private static MockWebServer mockWebServer;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(8000); // Start the mock server on the same port as the ML service
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void analyzeApp_shouldCallMLModelAndReturnPrediction() {
        // Mock ML service response
        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                    {
                        "risk_score": 0.9,
                        "risk_label": "HIGH"
                    }
                """)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        // Send request to backend
        webTestClient.post()
                .uri("/api/analyzeApp")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {
                        "package_name": "com.example.app",
                        "app_name": "ExampleApp",
                        "permissions": ["INTERNET", "CAMERA"],
                        "version_code": 2
                    }
                """)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.risk_score").isEqualTo(0.9)
                .jsonPath("$.risk_label").isEqualTo("HIGH");
    }
}