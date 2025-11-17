package huce.nguyentoan.job4u.domain.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IBackendRes<T> {
    private T data;
    private String message;
    private int statusCode;
}
