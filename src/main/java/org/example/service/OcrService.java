package org.example.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

    public String extractTextFromImage(MultipartFile file) throws IOException, TesseractException, URISyntaxException {

        File tempFile = File.createTempFile("uploaded_", file.getOriginalFilename());
        file.transferTo(tempFile);

        Mat src = Imgcodecs.imread(tempFile.getAbsolutePath());

        // Convertir a escala de grises
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

        // Aplicar un filtro bilateral para suavizar la imagen y mantener los bordes
        Mat denoised = new Mat();
        Imgproc.bilateralFilter(gray, denoised, 9, 75, 75);

        // Corregir la inclinación de la imagen
        Mat corrected = correctSkew(denoised);

        // Aplicar CLAHE para mejorar el contraste
        CLAHE clahe = Imgproc.createCLAHE();
        clahe.setClipLimit(4.0);
        Mat claheImage = new Mat();
        clahe.apply(corrected, claheImage);

        // Aplicar umbral adaptativo
        Mat thresh = new Mat();
        Imgproc.adaptiveThreshold(claheImage, thresh, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);

        //Imgcodecs.imwrite(tempFile.getAbsolutePath(), thresh);

        // Escalar la imagen para mejorar la lectura (opcional)
        Mat highRes = new Mat();
        Imgproc.resize(thresh, highRes, new Size(thresh.width() * 2, thresh.height() * 2));

        // Configuración de Tesseract
        Tesseract tesseract = new Tesseract();
        File tessDataFolder = Paths.get(getClass().getClassLoader().getResource("tessdata").toURI()).toFile();
        tesseract.setDatapath(tessDataFolder.getAbsolutePath());
        tesseract.setLanguage("spa");
        tesseract.setPageSegMode(3);  // Ajustar según la estructura del texto en la imagen

        String result = tesseract.doOCR(tempFile);
        tempFile.delete();

        return result;
    }

    // Métodos para corregir la inclinación (skew correction)
    private Mat correctSkew(Mat src) {
        Mat edges = new Mat();
        Imgproc.Canny(src, edges, 50, 150);
        Mat lines = new Mat();
        Imgproc.HoughLinesP(edges, lines, 1, Math.PI / 180, 100, 100, 10);

        double angle = calculateSkewAngle(lines);
        Point center = new Point(src.cols() / 2, src.rows() / 2);
        Mat rotMat = Imgproc.getRotationMatrix2D(center, angle, 1);
        Mat corrected = new Mat();
        Imgproc.warpAffine(src, corrected, rotMat, src.size(), Imgproc.INTER_CUBIC, Core.BORDER_REPLICATE);

        return corrected;
    }

    private double calculateSkewAngle(Mat lines) {
        double angle = 0.0;
        int count = 0;
        for (int i = 0; i < lines.rows(); i++) {
            double[] vec = lines.get(i, 0);
            double x1 = vec[0], y1 = vec[1], x2 = vec[2], y2 = vec[3];
            double dx = x2 - x1;
            double dy = y2 - y1;
            double currentAngle = Math.atan2(dy, dx) * 180 / Math.PI;
            if (Math.abs(currentAngle) < 45) {
                angle += currentAngle;
                count++;
            }
        }
        return count == 0 ? 0 : angle / count;
    }
}
