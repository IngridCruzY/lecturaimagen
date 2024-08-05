package org.example.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

@Service
public class OcrService {

    public String extractTextFromImage(MultipartFile file) throws IOException, TesseractException, URISyntaxException {

        File tempFile = File.createTempFile("uploaded_", file.getOriginalFilename());
        file.transferTo(tempFile);

        Tesseract tesseract = new Tesseract();
        //tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
        File tessDataFolder = Paths.get(getClass().getClassLoader().getResource("tessdata").toURI()).toFile();
        tesseract.setDatapath(tessDataFolder.getAbsolutePath());

        String result = tesseract.doOCR(tempFile);

        tempFile.delete();

        return result;
    }
}
