package madical_service.controller;

import madical_service.entity.Vaccination;
import madical_service.service.FileReaderService;
import madical_service.service.VaccinationService;
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
    public ResponseEntity<List<Vaccination>> getAllVaccinationsByPassport(@RequestParam("document") String passport) {
        List<Vaccination> vaccinations = vaccinationService.getAllVaccinationsForPerson(passport);

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
