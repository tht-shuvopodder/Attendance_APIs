package com.API_Testing.APIx.controller;

import com.API_Testing.APIx.impl.DeviceImpl;
import com.API_Testing.APIx.model.QRlog;
import com.API_Testing.APIx.model.request.QRRequestDTO;
import com.API_Testing.APIx.service.QRlogService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;


@RestController
@RequestMapping("/qr")
public class QRController {

    @Autowired
    QRlogService qrlogService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DeviceImpl deviceImpl;


    @PostMapping("/request-access")
    public ResponseEntity<?> generateAndDownloadQrCode(@RequestBody QRRequestDTO qrRequest, HttpServletResponse response) throws IOException, WriterException {
        String deviceMAC = qrRequest.getDeviceMAC();
        String email = qrRequest.getEmployeeEmail();
        String employeeId = qrRequest.getEmployeeId();

        // ✅ Step 1: Check if employee exists in the device-specific table
        String tableName = deviceImpl.formatMacToTableName(deviceMAC);
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE email = ? AND employee_id = ?", tableName);
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email, employeeId);

        if (count == null || count == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Employee not found for this device ❌");
        }

        // ✅ Step 2: Check if the employee already used a QR for this device
        if (qrlogService.existsByMacAndEmployeeEmailAndUsedIsTrue(deviceMAC, email)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .body("Access already verified. QR generation denied ⛔");
        }

        // ✅ Step 3: Generate UUID and QR
        String uuid = UUID.randomUUID().toString();
        BitMatrix matrix = new QRCodeWriter().encode(uuid, BarcodeFormat.QR_CODE, 300, 300);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(matrix);

        // ✅ Step 4: Log QR request
        QRlog qrlog = new QRlog();
        qrlog.setMac(deviceMAC);
        qrlog.setEmployeeEmail(email);
        qrlog.setEmployeeId(employeeId);
        qrlog.setGeneratedAt(Timestamp.from(Instant.now()));
        qrlog.setUuid(UUID.fromString(uuid));
        qrlog.setUsed(false);
        qrlogService.save(qrlog);

        // ✅ Step 5: Write QR to response directly
        response.setContentType("image/png");
        response.setHeader("Content-Disposition", "attachment; filename=access-qr.png");
        response.setHeader("X-QR-UUID", uuid); // Optional: UUID in header

        OutputStream outputStream = response.getOutputStream();
        ImageIO.write(qrImage, "PNG", outputStream);
        outputStream.flush();
        outputStream.close();

        return null; // Since response is already handled
    }



    @PostMapping("/verify-qr")
    public ResponseEntity<?> verifyQr(@RequestBody Map<String, String> payload) {
        String uuid = payload.get("uuid");

        if (uuid == null || uuid.isBlank()) {
            return ResponseEntity.badRequest().body("UUID is required");
        }

        try {
            UUID parsedUUID = UUID.fromString(uuid); // Validate UUID format

            Optional<QRlog> qrLogOpt = qrlogService.findByUUID(parsedUUID);

            if (qrLogOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid QR code");
            }

            QRlog qrLog = qrLogOpt.get();

            if (qrLog.isUsed()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("QR code already used");
            }

            // Mark as used
            qrLog.setUsed(true);
            qrlogService.save(qrLog);

            return ResponseEntity.ok("QR code verified successfully ✅");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Malformed UUID ❌");
        }
    }

}