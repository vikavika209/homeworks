package com.example.client;

import com.example.dto.QRCodeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "qr-service", url = "${qr.service.url}")
public interface QRCodeClient {

    @GetMapping("/qr/{passport}")
    ResponseEntity<QRCodeDTO> getQRCodeByPassport(@PathVariable("passport") String passport);

    @GetMapping("/qr/check")
    ResponseEntity<Boolean> verifyQRCode(@RequestParam("code") String code);
}
