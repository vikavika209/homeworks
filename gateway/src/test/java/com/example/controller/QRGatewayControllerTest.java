package com.example.controller;

import com.example.client.QRCodeClient;
import com.example.dto.QRCodeDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QRGatewayController.class)
class QRGatewayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QRCodeClient qrCodeClient;

    @Test
    void getQRCodeByPassport_returnsOk() throws Exception {
        QRCodeDTO dto = new QRCodeDTO();
        dto.setVaccinationId(1L);
        dto.setHash("abc123");

        when(qrCodeClient.getQRCodeByPassport("1234567890"))
                .thenReturn(ResponseEntity.ok(dto));

        mockMvc.perform(get("/api/v1/qr/1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hash").value("abc123"))
                .andExpect(jsonPath("$.vaccinationId").value(1));
    }

    @Test
    void getQRCodeByPassport_returnsError_whenServiceFails() throws Exception {
        when(qrCodeClient.getQRCodeByPassport("0000000000"))
                .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        mockMvc.perform(get("/api/v1/qr/0000000000"))
                .andExpect(status().isBadGateway())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Не удалось получить Qr код")));
    }

    @Test
    void verifyQRCode_returnsTrue() throws Exception {
        when(qrCodeClient.verifyQRCode("abc123")).thenReturn(ResponseEntity.ok(true));

        mockMvc.perform(get("/api/v1/qr/check")
                        .param("code", "abc123"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void verifyQRCode_returnsError_whenServiceFails() throws Exception {
        when(qrCodeClient.verifyQRCode("badcode"))
                .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

        mockMvc.perform(get("/api/v1/qr/check")
                        .param("code", "badcode"))
                .andExpect(status().isBadGateway())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Не удалось аутентифицировать Qr код")));
    }
}
