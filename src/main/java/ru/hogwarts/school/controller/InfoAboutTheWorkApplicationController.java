package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/info")
@RestController
public class InfoAboutTheWorkApplicationController {

    @GetMapping("/application")
    public ResponseEntity getInfoAboutTheWorkApplication() {
        return ResponseEntity.ok("This application is Good work!");
    }
}
