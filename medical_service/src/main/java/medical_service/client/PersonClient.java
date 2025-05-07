package medical_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "person-client", url = "${person.service.url}")
public interface PersonClient {

    @GetMapping("/person/verify")
    ResponseEntity<Boolean> verifyPerson(@RequestParam("name") String fullName, @RequestParam("passport") String passport);
}
