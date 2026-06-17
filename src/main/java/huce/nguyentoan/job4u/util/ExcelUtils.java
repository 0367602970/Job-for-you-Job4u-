package huce.nguyentoan.job4u.util;

import huce.nguyentoan.job4u.dto.ExportColumn;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
public class ExcelUtils {

    public <T> byte[] exportXlsx(
            List<T> data,
            List<ExportColumn<T>> columns
    ) throws IOException {

        try (
                Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream output =
                        new ByteArrayOutputStream()
        ) {

            Sheet sheet =
                    workbook.createSheet("Export");

            // Header
            Row headerRow =
                    sheet.createRow(0);

            for (int i = 0; i < columns.size(); i++) {

                headerRow.createCell(i)
                        .setCellValue(
                                columns.get(i)
                                        .getHeader()
                        );
            }

            // Data
            int rowIndex = 1;

            for (T item : data) {

                Row row =
                        sheet.createRow(rowIndex++);

                for (int i = 0; i < columns.size(); i++) {

                    ExportColumn<T> column =
                            columns.get(i);

                    Object value =
                            column.getExtractor()
                                    .apply(item);

                    String formatted =
                            (column.getFormatter() != null)
                                    ? column.getFormatter()
                                    .apply(value)
                                    : Objects.toString(value, "");

                    row.createCell(i)
                            .setCellValue(formatted);
                }
            }

            // Auto size columns
            for (int i = 0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(output);

            return output.toByteArray();
        }
    }
}
