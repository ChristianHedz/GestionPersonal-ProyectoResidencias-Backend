package com.chris.gestionpersonal.services;

import com.google.zxing.WriterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class QrCodeServiceTest {

    private static final String TEST_EMAIL = "test@example.com";
    private static final int QR_WIDTH = 250;
    private static final int QR_HEIGHT = 250;

    @InjectMocks
    private QrCodeService qrCodeService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(qrCodeService, "qrCodeBasePath", tempDir.toString());
    }

    @Test
    @DisplayName("Debe generar un archivo QR y crear el directorio si este no existe")
    void cuandoDirectorioNoExiste_entoncesLoCreaYGeneraQr() throws IOException, WriterException {
        // Arrange
        Path subDirPath = tempDir.resolve("new_qr_codes");
        ReflectionTestUtils.setField(qrCodeService, "qrCodeBasePath", subDirPath.toString());
        assertThat(subDirPath).doesNotExist();

        // Act
        File qrFile = qrCodeService.generateQRCode(TEST_EMAIL, QR_WIDTH, QR_HEIGHT);

        // Assert
        assertThat(subDirPath).exists();
        assertThat(qrFile)
                .isNotNull()
                .hasName(TEST_EMAIL + "QR.png")
                .satisfies(file -> assertThat(file.length()).isGreaterThan(0L));
        assertThat(qrFile.getParentFile().toPath()).isEqualTo(subDirPath);
    }

    @Test
    @DisplayName("Debe generar un archivo QR si el directorio ya existe")
    void cuandoDirectorioYaExiste_entoncesGeneraQrCorrectamente() throws IOException, WriterException {
        // Arrange: El @BeforeEach ya establece tempDir, que @TempDir se asegura de que exista.

        // Act
        File qrFile = qrCodeService.generateQRCode(TEST_EMAIL, QR_WIDTH, QR_HEIGHT);

        // Assert
        assertThat(qrFile)
                .exists()
                .hasName(TEST_EMAIL + "QR.png");
    }

    @Test
    @DisplayName("Debe lanzar IOException si la ruta del directorio es inválida (un archivo)")
    void cuandoRutaDeDirectorioEsInvalida_entoncesLanzaExcepcion() throws IOException {
        // Arrange
        File blockerFile = tempDir.resolve("es_un_archivo.txt").toFile();
        assertThat(blockerFile.createNewFile()).isTrue();

        // La ruta inválida es un subdirectorio DENTRO de un archivo, lo cual es imposible de crear.
        Path invalidPath = blockerFile.toPath().resolve("subfolder");
        ReflectionTestUtils.setField(qrCodeService, "qrCodeBasePath", invalidPath.toString());

        // Act & Assert
        IOException exception = assertThrows(IOException.class, () ->
            qrCodeService.generateQRCode(TEST_EMAIL, QR_WIDTH, QR_HEIGHT)
        );
        assertThat(exception.getMessage()).startsWith("Fallo al crear el directorio");
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException si el contenido del QR está vacío")
    void cuandoElContenidoEsVacio_entoncesLanzaIllegalArgumentException() {
        // Arrange
        String emailVacio = "";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            qrCodeService.generateQRCode(emailVacio, QR_WIDTH, QR_HEIGHT)
        );
    }
}