package com.chris.gestionpersonal.services;

import com.chris.gestionpersonal.exceptions.ExcelGenerationException;
import com.chris.gestionpersonal.models.dto.AssistDetailsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class AssistExcelServiceImpl {
    public byte[] createExcel(List<AssistDetailsDTO> assistDetailsDTOs) {
        log.info("Generando archivo Excel para las asistencias");
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            XSSFSheet sheet = workbook.createSheet("Asistencias");

            // 1. Crear estilos
            ExcelStyles styles = createExcelStyles(workbook);

            // 2. Crear título y cabeceras
            createTitleAndHeaders(sheet, styles);

            // 3. Llenar datos
            fillData(sheet, assistDetailsDTOs, workbook, styles);

            // 4. Ajustar columnas y congelar panel
            adjustColumnsAndFreezePane(sheet);

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Error al generar el archivo Excel", e);
            throw new ExcelGenerationException("Error al generar el archivo Excel", e);
        }
    }

    private record ExcelStyles(
        XSSFCellStyle titleStyle,
        XSSFCellStyle headerStyle,
        XSSFCellStyle evenRowStyle,
        XSSFCellStyle oddRowStyle,
        XSSFCellStyle dateStyle,
        XSSFCellStyle timeStyle
    ) {}

    private ExcelStyles createExcelStyles(XSSFWorkbook workbook) {
        // Estilo para el título
        XSSFCellStyle titleStyle = workbook.createCellStyle();
        XSSFFont titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);

        // Estilo para cabeceras
        XSSFCellStyle headerStyle = workbook.createCellStyle();
        XSSFFont headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        // Estilos para filas
        XSSFCellStyle evenRowStyle = workbook.createCellStyle();
        evenRowStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        evenRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        evenRowStyle.setBorderBottom(BorderStyle.THIN);
        evenRowStyle.setBorderTop(BorderStyle.THIN);
        evenRowStyle.setBorderLeft(BorderStyle.THIN);
        evenRowStyle.setBorderRight(BorderStyle.THIN);

        XSSFCellStyle oddRowStyle = workbook.createCellStyle();
        oddRowStyle.setBorderBottom(BorderStyle.THIN);
        oddRowStyle.setBorderTop(BorderStyle.THIN);
        oddRowStyle.setBorderLeft(BorderStyle.THIN);
        oddRowStyle.setBorderRight(BorderStyle.THIN);

        // Estilo para fechas y horas
        XSSFCellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(workbook.createDataFormat().getFormat("dd/MM/yyyy"));
        dateStyle.setBorderBottom(BorderStyle.THIN);
        dateStyle.setBorderTop(BorderStyle.THIN);
        dateStyle.setBorderLeft(BorderStyle.THIN);
        dateStyle.setBorderRight(BorderStyle.THIN);

        XSSFCellStyle timeStyle = workbook.createCellStyle();
        timeStyle.setDataFormat(workbook.createDataFormat().getFormat("HH:mm"));
        timeStyle.setBorderBottom(BorderStyle.THIN);
        timeStyle.setBorderTop(BorderStyle.THIN);
        timeStyle.setBorderLeft(BorderStyle.THIN);
        timeStyle.setBorderRight(BorderStyle.THIN);

        return new ExcelStyles(titleStyle, headerStyle, evenRowStyle, oddRowStyle, dateStyle, timeStyle);
    }

    private void createTitleAndHeaders(XSSFSheet sheet, ExcelStyles styles) {
        // Título del reporte
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("REPORTE DE ASISTENCIAS");
        titleCell.setCellStyle(styles.titleStyle());
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));

        // Crear cabeceras
        Row headerRow = sheet.createRow(2);
        headerRow.setHeightInPoints(25);
        String[] headers = {"ID", "Empleado", "Fecha", "Hora Entrada", "Hora Salida", "Horas Trabajadas", "Incidencias", "Motivo"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(styles.headerStyle());
        }
    }

    private void fillData(XSSFSheet sheet, List<AssistDetailsDTO> assistDetailsDTOs, XSSFWorkbook workbook, ExcelStyles styles) {
        int rowNum = 3;
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        for (AssistDetailsDTO assist : assistDetailsDTOs) {
            Row row = sheet.createRow(rowNum);
            XSSFCellStyle rowStyle = (rowNum % 2 == 0) ? styles.evenRowStyle() : styles.oddRowStyle();

            Cell idCell = row.createCell(0);
            idCell.setCellValue(assist.getId());
            idCell.setCellStyle(rowStyle);

            Cell employeeCell = row.createCell(1);
            employeeCell.setCellValue(assist.getFullName());
            employeeCell.setCellStyle(rowStyle);

            Cell dateCell = row.createCell(2);
            if (assist.getDate() != null) {
                dateCell.setCellValue(assist.getDate());
                XSSFCellStyle cellStyle = workbook.createCellStyle();
                cellStyle.cloneStyleFrom(styles.dateStyle());
                if (rowNum % 2 == 0) {
                    cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                }
                dateCell.setCellStyle(cellStyle);
            } else {
                dateCell.setCellStyle(rowStyle);
            }

            Cell entryTimeCell = row.createCell(3);
            if (assist.getEntryTime() != null) {
                entryTimeCell.setCellValue(assist.getEntryTime().format(timeFormatter));
            }
            entryTimeCell.setCellStyle(rowStyle);

            Cell exitTimeCell = row.createCell(4);
            if (assist.getExitTime() != null) {
                exitTimeCell.setCellValue(assist.getExitTime().format(timeFormatter));
            }
            exitTimeCell.setCellStyle(rowStyle);

            Cell hoursCell = row.createCell(5);
            hoursCell.setCellValue(assist.getWorkedHours());
            hoursCell.setCellStyle(rowStyle);

            Cell incidentsCell = row.createCell(6);
            incidentsCell.setCellValue(assist.getIncidents());

            incidenceColor(workbook, assist, rowStyle, incidentsCell);

            Cell reasonCell = row.createCell(7);
            reasonCell.setCellValue(assist.getReason() != null ? assist.getReason() : "");
            reasonCell.setCellStyle(rowStyle);
            rowNum++;
        }
    }

    private void incidenceColor(XSSFWorkbook workbook, AssistDetailsDTO assist, XSSFCellStyle rowStyle, Cell incidentsCell) {
        // Colorear las incidencias según su tipo
        XSSFCellStyle incidenceStyle = workbook.createCellStyle();
        incidenceStyle.cloneStyleFrom(rowStyle);
        incidenceStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        if ("FALTA".equals(assist.getIncidents())) {
            incidenceStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        } else if ("ASISTENCIA".equals(assist.getIncidents())) {
            incidenceStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        } else if ("RETARDO".equals(assist.getIncidents())) {
            incidenceStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        } else {
            incidenceStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        }
        incidentsCell.setCellStyle(incidenceStyle);
    }

    private void adjustColumnsAndFreezePane(XSSFSheet sheet) {
        // Ajustar ancho de columnas
        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
            // Añadir un poco más de espacio
            int columnWidth = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, columnWidth + 1000);
        }

        // Congelar panel para mantener las cabeceras visibles
        sheet.createFreezePane(1, 3);

    }
}
