package com.chris.gestionpersonal.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

@Slf4j
@Service
public class QrCodeService {

    @Value("${qrcode.storage.path}")
    private String qrCodeBasePath;

    public File generateQRCode(String email, int width, int height) throws WriterException, IOException {
        File directory = new File(qrCodeBasePath);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Fallo al crear el directorio: " + qrCodeBasePath);
        }

        String qrCodePath = qrCodeBasePath + File.separator + email + "QR" + ".png";
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(email, BarcodeFormat.QR_CODE, width, height);
        Path path = FileSystems.getDefault().getPath(qrCodePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        File qrFile = path.toFile();
        log.info("QR code generated at: {}, exists: {}, size: {}", qrFile.getAbsolutePath(), qrFile.exists(), qrFile.length());
        return qrFile;
    }
}