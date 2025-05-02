package madical_service.controller;

import madical_service.dto.VaccinationDTO;
import madical_service.entity.Vaccination;
import madical_service.service.FileReaderService;
import madical_service.service.VaccinationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    public ResponseEntity<String> saveVaccination(@RequestParam("file") MultipartFile file) {
        System.out.println("Получен файл: " + file.getOriginalFilename());
        fileReaderService.getVaccinationInfo(file);
        return ResponseEntity.ok("Файл успешно обработан и данные сохранены");
    }
}
