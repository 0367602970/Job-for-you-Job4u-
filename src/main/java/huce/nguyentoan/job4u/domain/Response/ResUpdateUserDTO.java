package huce.nguyentoan.job4u.domain.Response;

import java.time.Instant;

import huce.nguyentoan.job4u.util.constant.GenderEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResUpdateUserDTO {
    private long id;
    private String name;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant updatedAt;
}
