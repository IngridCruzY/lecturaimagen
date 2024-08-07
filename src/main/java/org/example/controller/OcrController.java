package org.example.controller;

import net.sourceforge.tess4j.TesseractException;
import org.example.service.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/extract")
public class OcrController {
    @Autowired
    private OcrService ocrService;

    @PostMapping("/image-text")
    public  ResponseEntity<String> extractTextFromImage(@RequestParam("file") MultipartFile file) {
        try {
            String extractedText = ocrService.extractTextFromImage(file);
            return new ResponseEntity<>(extractedText, HttpStatus.OK);
        } catch (IOException | TesseractException | URISyntaxException e) {
            return new ResponseEntity<>("Error al procesar la imagen: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
