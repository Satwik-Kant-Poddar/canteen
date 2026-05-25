package com.canteen.qr.controller;

import com.canteen.qr.service.QrCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QrCodeController {

    private final QrCodeService qrCodeService;

    @Autowired
    public QrCodeController(QrCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @GetMapping("/api/qr/generate")
    public ResponseEntity<byte[]> generateQrCode(
            @RequestParam String text,
            @RequestParam(defaultValue = "300") int width,
            @RequestParam(defaultValue = "300") int height,
            @RequestParam(defaultValue = "M") String ecl,
            @RequestParam(defaultValue = "png") String format,
            @RequestParam(defaultValue = "#000000") String onColor,
            @RequestParam(defaultValue = "#ffffff") String offColor
    ) {
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Text parameter is required".getBytes());
        }

        // Keep dimensions reasonable
        int finalWidth = Math.max(10, Math.min(width, 2000));
        int finalHeight = Math.max(10, Math.min(height, 2000));

        try {
            byte[] imageBytes = qrCodeService.generateQrCode(text, finalWidth, finalHeight, ecl, format, onColor, offColor);
            
            MediaType mediaType = MediaType.IMAGE_PNG;
            if (format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg")) {
                mediaType = MediaType.IMAGE_JPEG;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.setCacheControl("max-age=31536000, must-revalidate");

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating QR Code: " + e.getMessage()).getBytes());
        }
    }
}
