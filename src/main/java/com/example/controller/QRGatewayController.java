package com.example.controller;

import com.example.client.QRCodeClient;
import com.example.dto.QRCodeDTO;
import com.example.exception.QRClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/qr")
@RequiredArgsConstructor
@Slf4j
public class QRGatewayController {

    private final QRCodeClient qrCodeClient;

    @GetMapping("/{passport}")
    public ResponseEntity<QRCodeDTO> getQRCodeByPassport(@PathVariable("passport") String passport) {

        log.info("получение Qr кода по паспорту: {} через QRClient.", passport);

        ResponseEntity<QRCodeDTO> qrCodeDTOResponseEntity = qrCodeClient.getQRCodeByPassport(passport);

        if(!qrCodeDTOResponseEntity.getStatusCode().is2xxSuccessful()) {
            throw new QRClientException("Не удалось получить Qr код по паспорту: " + passport);
        }

        return qrCodeDTOResponseEntity;
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> verifyQRCode(@RequestParam("code") String code) {
        ResponseEntity<Boolean> responseEntity = qrCodeClient.verifyQRCode(code);

        if(!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new QRClientException("Не удалось аутентифицировать Qr код: " + code);
        }

        return responseEntity;
    }
}
