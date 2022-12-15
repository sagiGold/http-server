package calculators;

import JsonModels.IndependentJSON;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndependentCalc {
    @PostMapping(value= "/independent/calculate", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity IndependentCalculation(@RequestBody IndependentJSON json){
    return ResponseEntity.ok("hey");
    }
}
