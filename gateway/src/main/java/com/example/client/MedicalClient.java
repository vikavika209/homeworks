package com.example.client;

import com.example.config.FeignConfig;
import com.example.dto.VaccinationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(
        name = "medical-service",
        url = "${medical.service.url}",
        configuration = FeignConfig.class
)
public interface MedicalClient {

    @GetMapping("/vaccinations/vaccination")
    Page<VaccinationDTO> getAllVaccinationsByPassport(
            @RequestParam("document") String passport,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sort") String sort
    );

    @PostMapping(
            value = "/vaccinations/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    ResponseEntity<String> saveVaccination(@RequestPart("file") MultipartFile file);
}
