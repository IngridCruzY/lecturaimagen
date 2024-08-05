package org.example.controller;

import org.example.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @PostMapping("/pdf-text")
    public String extractTextFromPdf(@RequestParam("file") MultipartFile file) {
        try {
            return pdfService.extractTextFromPdf(file);
        } catch (IOException e) {
            return "Error al leer el archivo PDF: " + e.getMessage();
        }
    }
}
