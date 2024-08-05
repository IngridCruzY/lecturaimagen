package org.example.controller;

import net.sourceforge.tess4j.TesseractException;
import org.example.service.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
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

    /*
    @PostMapping("/image-text")
    public String extractTextFromImage(@RequestParam("file") MultipartFile file) {
        try {
            return ocrService.extractTextFromImage(file);
        } catch (IOException | TesseractException | URISyntaxException e) {
            return "Error al procesar la imagen: " + e.getMessage();
        }
    }

     */

    @PostMapping("/image2-text")
    public String extractTextFromImage2(@RequestParam("file") MultipartFile file) {
        try {
            return ocrService.extractTextFromImage2(file);
        } catch (IOException | TesseractException | URISyntaxException e) {
            return "Error al procesar la imagen de Yape: " + e.getMessage();
        }
    }
}
