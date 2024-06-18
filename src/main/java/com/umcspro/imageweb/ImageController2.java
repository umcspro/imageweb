package com.umcspro.imageweb;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

@RestController
public class ImageController2 {
    @PostMapping("/restapi2/image/adjustbrightness")
    public ResponseEntity<String> adjustBrightness(@RequestBody ImageDTO imageDTO) throws IOException {
        String image64 = imageDTO.getBase64Image();
        String[] image = image64.split(",");
        String imageType = image[0].substring(image[0].indexOf("/") + 1,image[0].indexOf(";"));
        byte[] imageByte = Base64.getDecoder().decode(image[1]);
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageByte));

        changeBrightness(bufferedImage, imageDTO.getBrightness());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, imageType, byteArrayOutputStream);

        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();

        String encoded = Base64.getEncoder().encodeToString(imageBytes);
        String base64 = "data:image/" + imageType + ";base64," + encoded;

        return new ResponseEntity<>(base64, HttpStatus.OK);
    }

    private void changeBrightness(BufferedImage originalImage, int brightness) {
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

    private int clamp(int value) {
        return Math.max(0, Math.min(value, 255));
    }
}
