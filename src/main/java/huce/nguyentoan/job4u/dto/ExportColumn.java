package huce.nguyentoan.job4u.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Function;

@Getter
@Setter
@AllArgsConstructor
public class ExportColumn<T> {
    private String header;

    private Function<T, Object> extractor;

    private Function<Object, String> formatter;
}
