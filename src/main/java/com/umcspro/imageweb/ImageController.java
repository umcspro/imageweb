package com.umcspro.imageweb;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
@RestController
@RequestMapping("/api/image")
public class ImageController {

    @GetMapping("/adjust-brightness")
    public String adjustBrightness(@RequestBody ImageDTO request) throws IOException {
        String base64Image = request.getBase64Image().replaceAll("\\s+", "");
        String[] parts = base64Image.split(",");
        String imageType = parts[0].substring(parts[0].indexOf("/") + 1, parts[0].indexOf(";"));
        base64Image = parts[1];

        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        BufferedImage image = ImageIO.read(inputStream);
        inputStream.close();

        BufferedImage brightenedImage = changeBrightness(image, request.getBrightness());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(brightenedImage, imageType, outputStream);
        byte[] brightenedImageBytes = outputStream.toByteArray();
        outputStream.close();
        return "data:image/" + imageType + ";base64," + Base64.getEncoder().encodeToString(brightenedImageBytes);
    }

    @PostMapping("/adjust-brightness-raw")
    public ResponseEntity<byte[]> adjustBrightnessRaw(@RequestBody ImageDTO request) throws IOException {

        String base64Image = request.getBase64Image().replaceAll("\\s+", "");
        String[] parts = base64Image.split(",");
        String imageType = parts[0].substring(parts[0].indexOf("/") + 1, parts[0].indexOf(";"));
        base64Image = parts[1];

        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        BufferedImage image = ImageIO.read(inputStream);

        changeBrightness2(image, request.getBrightness());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, imageType, outputStream);
        byte[] brightenedImageBytes = outputStream.toByteArray();
        outputStream.close();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "image/" + imageType);
        headers.add("Content-Length", String.valueOf(brightenedImageBytes.length));

        return new ResponseEntity<>(brightenedImageBytes, headers, HttpStatus.OK);

    }

    private BufferedImage changeBrightness(BufferedImage originalImage, int brightness) {
        BufferedImage result = new BufferedImage(
                originalImage.getWidth(),
                originalImage.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < originalImage.getHeight(); y++) {
            for (int x = 0; x < originalImage.getWidth(); x++) {
                Color color = new Color(originalImage.getRGB(x, y));
                int r = clamp(color.getRed() + brightness);
                int g = clamp(color.getGreen() + brightness);
                int b = clamp(color.getBlue() + brightness);
                Color newColor = new Color(r, g, b);
                result.setRGB(x, y, newColor.getRGB());
            }
        }
        return result;
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(value, 255));
    }

    private void changeBrightness2(BufferedImage originalImage, int brightness) {
        for (int y = 0; y < originalImage.getHeight(); y++) {
            for (int x = 0; x < originalImage.getWidth(); x++) {
                Color color = new Color(originalImage.getRGB(x, y));
                int r =  clamp(color.getRed() + brightness);
                int g =  clamp(color.getGreen() + brightness);
                int b =  clamp(color.getBlue() + brightness);
                Color newColor = new Color(r, g, b);
                originalImage.setRGB(x, y, newColor.getRGB());
            }
        }
    }
}