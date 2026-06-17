package huce.nguyentoan.job4u.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CustomerDto {

    private String code;

    private String name;

    private LocalDate createdDate;
}
