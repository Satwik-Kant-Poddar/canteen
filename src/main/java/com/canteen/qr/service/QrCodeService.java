package com.canteen.qr.service;

import com.canteen.qr.core.QrCode;
import com.canteen.qr.core.QrCode.Ecc;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Service
public class QrCodeService {

    public byte[] generateQrCode(String text, int targetWidth, int targetHeight, String eclStr, String formatStr, String onColorHex, String offColorHex) throws Exception {
        
        Ecc errorCorrectionLevel = Ecc.MEDIUM;
        if (eclStr != null) {
            switch (eclStr.toUpperCase()) {
                case "L": errorCorrectionLevel = Ecc.LOW; break;
                case "Q": errorCorrectionLevel = Ecc.QUARTILE; break;
                case "H": errorCorrectionLevel = Ecc.HIGH; break;
                case "M":
                default: errorCorrectionLevel = Ecc.MEDIUM; break;
            }
        }

        // Generate the barebones mathematical QR matrix
        QrCode qr = QrCode.encodeText(text, errorCorrectionLevel);

        int onColorArgb = parseColor(onColorHex, 0xFF000000);
        int offColorArgb = parseColor(offColorHex, 0xFFFFFFFF);

        // Standard margin in modules
        int borderModules = 4;
        int qrSize = qr.size;
        int totalModules = qrSize + (borderModules * 2);

        // Calculate scaling factor to hit the target width/height
        // We use Math.max to maintain a perfect square aspect ratio
        int scale = Math.max(1, Math.min(targetWidth, targetHeight) / totalModules);
        
        // Final image size might be slightly smaller/larger due to integer division,
        // so we base the canvas on the exact scaled dimensions.
        int finalSize = totalModules * scale;

        BufferedImage image = new BufferedImage(finalSize, finalSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Fill background
        g2d.setColor(new Color(offColorArgb, true));
        g2d.fillRect(0, 0, finalSize, finalSize);

        // Draw foreground modules
        g2d.setColor(new Color(onColorArgb, true));
        for (int y = 0; y < qrSize; y++) {
            for (int x = 0; x < qrSize; x++) {
                if (qr.getModule(x, y)) {
                    int rectX = (x + borderModules) * scale;
                    int rectY = (y + borderModules) * scale;
                    g2d.fillRect(rectX, rectY, scale, scale);
                }
            }
        }
        g2d.dispose();

        String imageFormat = "png";
        if (formatStr != null && (formatStr.equalsIgnoreCase("jpg") || formatStr.equalsIgnoreCase("jpeg"))) {
            imageFormat = "jpeg";
            // For JPEG, alpha channel is not supported. Create a solid RGB background.
            BufferedImage rgbImage = new BufferedImage(finalSize, finalSize, BufferedImage.TYPE_INT_RGB);
            Graphics2D rgbG2d = rgbImage.createGraphics();
            rgbG2d.setColor(new Color(offColorArgb, false));
            rgbG2d.fillRect(0, 0, finalSize, finalSize);
            rgbG2d.drawImage(image, 0, 0, null);
            rgbG2d.dispose();
            image = rgbImage;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, imageFormat, outputStream);
        return outputStream.toByteArray();
    }

    private int parseColor(String hex, int defaultColor) {
        if (hex == null || hex.trim().isEmpty()) {
            return defaultColor;
        }
        try {
            String cleanHex = hex.trim();
            if (cleanHex.startsWith("#")) {
                cleanHex = cleanHex.substring(1);
            }
            if (cleanHex.length() == 6) {
                return (int) Long.parseLong("FF" + cleanHex, 16);
            } else if (cleanHex.length() == 8) {
                return (int) Long.parseLong(cleanHex, 16);
            }
        } catch (NumberFormatException e) {
            // fallback
        }
        return defaultColor;
    }
}
