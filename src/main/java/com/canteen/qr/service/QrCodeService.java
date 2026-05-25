package com.canteen.qr.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class QrCodeService {

    public byte[] generateQrCode(String text, int width, int height, String ecl, String format, String onColorHex, String offColorHex) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.M;
        if (ecl != null) {
            switch (ecl.toUpperCase()) {
                case "L":
                    errorCorrectionLevel = ErrorCorrectionLevel.L;
                    break;
                case "Q":
                    errorCorrectionLevel = ErrorCorrectionLevel.Q;
                    break;
                case "H":
                    errorCorrectionLevel = ErrorCorrectionLevel.H;
                    break;
                case "M":
                default:
                    errorCorrectionLevel = ErrorCorrectionLevel.M;
                    break;
            }
        }
        hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        int onColor = parseColor(onColorHex, 0xFF000000); // Default: black
        int offColor = parseColor(offColorHex, 0xFFFFFFFF); // Default: white

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageConfig config = new MatrixToImageConfig(onColor, offColor);
        
        String imageFormat = "png";
        if (format != null && (format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg"))) {
            imageFormat = "jpeg";
        }
        
        MatrixToImageWriter.writeToStream(bitMatrix, imageFormat, outputStream, config);
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
                // Add full opacity prefix (FF)
                return (int) Long.parseLong("FF" + cleanHex, 16);
            } else if (cleanHex.length() == 8) {
                return (int) Long.parseLong(cleanHex, 16);
            }
        } catch (NumberFormatException e) {
            // Ignore and fall back to default
        }
        return defaultColor;
    }
}
