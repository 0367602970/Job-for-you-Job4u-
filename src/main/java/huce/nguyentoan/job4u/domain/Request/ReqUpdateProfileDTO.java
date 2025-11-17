package huce.nguyentoan.job4u.domain.Request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqUpdateProfileDTO {
    private String name;
    private Integer age;
    private String address;
}
