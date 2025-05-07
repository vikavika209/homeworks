package medical_service.controller;

import medical_service.dto.VaccinationDTO;
import medical_service.service.FileReaderService;
import medical_service.service.VaccinationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/vaccinations")
public class VaccinationController {
    private final VaccinationService vaccinationService;
    private final FileReaderService fileReaderService;

    public VaccinationController(VaccinationService vaccinationService, FileReaderService fileReaderService) {
        this.vaccinationService = vaccinationService;
        this.fileReaderService = fileReaderService;
    }

    @GetMapping("/vaccination")
    public ResponseEntity<Page<VaccinationDTO>> getAllVaccinationsByPassport(
            @RequestParam("document") String passport,
            Pageable pageable
    )
    {
        Page<VaccinationDTO> vaccinations = vaccinationService.getAllVaccinationsForPerson(passport, pageable);

        if (vaccinations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(vaccinations);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> saveVaccination(@RequestPart("file") MultipartFile file) {
        System.out.println("Получен файл: " + file.getOriginalFilename());
        fileReaderService.getVaccinationInfo(file);
        return ResponseEntity.ok("Файл успешно обработан и данные сохранены");
    }
}
