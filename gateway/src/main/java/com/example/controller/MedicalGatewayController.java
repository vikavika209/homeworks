package com.example.controller;

import com.example.client.MedicalClient;
import com.example.dto.VaccinationDTO;
import com.example.exception.MedicalClientException;
import com.example.util.MultipartInputStreamFileResource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/medical")
@RequiredArgsConstructor
public class MedicalGatewayController {

    private final MedicalClient medicalClient;

    @GetMapping("/vaccination")
    public ResponseEntity<Page<VaccinationDTO>> getAll(@RequestParam("document") String passport,
                                                       @RequestParam("page") int page,
                                                       @RequestParam("size") int size
    ) {
        String sort = "vaccinationDate,desc";
        Page<VaccinationDTO> vaccinationDTOPage = medicalClient.getAllVaccinationsByPassport(
                passport, page, size, sort
        );

        if(vaccinationDTOPage.getTotalElements() > 0) {
            return ResponseEntity.ok(vaccinationDTOPage);
        }
        throw  new MedicalClientException("Не удалось получить вакцинации по паспорту: " + passport);
    }

    @PostMapping(
            value = "/vaccinations/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> upload(@RequestPart("file") MultipartFile file) throws IOException {
        ResponseEntity<String> response = medicalClient.saveVaccination(file);
        if(!response.getStatusCode().is2xxSuccessful()) {
            throw new MedicalClientException("Не удалось обработать файл.");
        }
        else return response;
    }
}

