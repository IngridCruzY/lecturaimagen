package org.example.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

@Service
public class OcrService {

    static {
        try {
            // Cargar la biblioteca nativa desde el JAR (dentro del archivo .jar)
            String path = OcrService.class.getResource("/native/opencv_java460.dll").getPath();
            System.load(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/*
    public String extractTextFromImage(MultipartFile file) throws IOException, TesseractException, URISyntaxException {

        File tempFile = File.createTempFile("uploaded_", file.getOriginalFilename());
        file.transferTo(tempFile);

        Tesseract tesseract = new Tesseract();
        File tessDataFolder = Paths.get(getClass().getClassLoader().getResource("tessdata").toURI()).toFile();
        tesseract.setDatapath(tessDataFolder.getAbsolutePath());

        String result = tesseract.doOCR(tempFile);
        tempFile.delete();

        return result;
    }

 */

    public String extractTextFromImage2(MultipartFile file) throws IOException, TesseractException, URISyntaxException {

        File tempFile = File.createTempFile("uploaded_", file.getOriginalFilename());
        file.transferTo(tempFile);

        Mat src = Imgcodecs.imread(tempFile.getAbsolutePath());
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.adaptiveThreshold(gray, gray, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 5);

        Imgcodecs.imwrite(tempFile.getAbsolutePath(), gray);

        Tesseract tesseract = new Tesseract();
        File tessDataFolder = Paths.get(getClass().getClassLoader().getResource("tessdata").toURI()).toFile();
        tesseract.setDatapath(tessDataFolder.getAbsolutePath());
        tesseract.setLanguage("spa");
        tesseract.setPageSegMode(3);

        String result = tesseract.doOCR(tempFile);
        tempFile.delete();

        return result;

    }

}
