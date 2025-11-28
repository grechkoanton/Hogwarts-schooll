package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/calculation")
public class CalculationController {

    @GetMapping("/sum-optimized")
    public ResponseEntity<Integer> getOptimizedSum() {
        int sum = IntStream.rangeClosed(1, 1_000_000)
                .parallel()
                .sum();
        return ResponseEntity.ok(sum);
    }
}
