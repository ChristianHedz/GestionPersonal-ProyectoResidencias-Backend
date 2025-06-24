package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.exceptions.FileStorageException;
import com.chris.gestionpersonal.exceptions.FileUploadException;
import com.chris.gestionpersonal.exceptions.InvalidFileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoStorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/jpg", "image/png", "image/gif");
    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.url-expiration-hours}")
    private int urlExpirationHours;

    public String uploadEmployeePhoto(MultipartFile file, String employeeEmail) {
        validateFile(file);
        
        String fileName = generateFileName(employeeEmail, file.getOriginalFilename());
        
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        try {
            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            log.info("Foto subida: {}", fileName);
            return getFileUrl(fileName);
        } catch (IOException e) {
            throw new FileUploadException("Error al procesar el archivo", e);
        } catch (Exception e) {
            throw new FileStorageException("Error al subir archivo a S3", e);
        }
    }

    public String getFileUrl(String fileName) {
        try (S3Presigner presigner = S3Presigner.create()) {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofHours(urlExpirationHours))
                    .getObjectRequest(getRequest)
                    .build();

            return presigner.presignGetObject(presignRequest).url().toString();
        } catch (Exception e) {
            throw new FileStorageException("Error al generar URL", e);
        }
    }

    public void deleteEmployeePhoto(String fileName) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            
            s3Client.deleteObject(request);
            log.info("Foto eliminada: {}", fileName);
        } catch (Exception e) {
            throw new FileStorageException("Error al eliminar archivo", e);
        }
    }

    public boolean fileExists(String fileName) {
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            throw new FileStorageException("Error al verificar archivo", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("El archivo no puede estar vacío");
        }
        
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new InvalidFileException("Tipo de archivo no válido. Solo JPG, PNG, GIF");
        }
        
        if (file.getSize() > MAX_SIZE) {
            throw new InvalidFileException("Archivo muy grande. Máximo 5MB");
        }
    }

    private String generateFileName(String email, String originalName) {
        String extension = originalName != null && originalName.contains(".") 
            ? originalName.substring(originalName.lastIndexOf(".")) 
            : ".jpg";
        
        String cleanEmail = email.replaceAll("[^a-zA-Z0-9]", "_");
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        
        return String.format("employee-photos/%s_%s%s", cleanEmail, uuid, extension);
    }
} 