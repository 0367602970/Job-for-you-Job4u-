package huce.nguyentoan.job4u.util;

import huce.nguyentoan.job4u.dto.ExportColumn;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;


@Component
public class CsvUtils {

    public <T> byte[] exportCsv(
            List<T> data,
            List<ExportColumn<T>> columns
    ) throws IOException {

        StringBuilder csv = new StringBuilder();

        // Header
        for (int i = 0; i < columns.size(); i++) {

            csv.append(
                    escapeCsv(
                            columns.get(i).getHeader()
                    )
            );

            if (i < columns.size() - 1) {
                csv.append(",");
            }
        }

        csv.append(System.lineSeparator());

        // Data
        for (T item : data) {

            for (int i = 0; i < columns.size(); i++) {

                ExportColumn<T> column =
                        columns.get(i);

                Object value =
                        column.getExtractor()
                                .apply(item);

                String formatted =
                        column.getFormatter() != null
                                ? column.getFormatter()
                                .apply(value)
                                : Objects.toString(value, "");

                csv.append(
                        escapeCsv(formatted)
                );

                if (i < columns.size() - 1) {
                    csv.append(",");
                }
            }

            csv.append(System.lineSeparator());
        }

        try (
                ByteArrayOutputStream output =
                        new ByteArrayOutputStream()
        ) {

            // UTF-8 BOM để Excel mở tiếng Việt đúng
            output.write(0xEF);
            output.write(0xBB);
            output.write(0xBF);

            output.write(
                    csv.toString()
                            .getBytes(StandardCharsets.UTF_8)
            );

            return output.toByteArray();
        }
    }

    private String escapeCsv(String value) {

        if (value == null) {
            return "";
        }

        if (
                value.contains(",")
                        || value.contains("\"")
                        || value.contains("\n")
                        || value.contains("\r")
        ) {

            return "\"" +
                    value.replace("\"", "\"\"")
                    + "\"";
        }

        return value;
    }
}
