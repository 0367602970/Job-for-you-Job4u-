package huce.nguyentoan.job4u.domain.Response;

import java.time.Instant;

import huce.nguyentoan.job4u.util.constant.GenderEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCreateUserDTO {
    private long id;
    private String name;
    private String email;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant createdAt;
    private CompanyUser company;

    @Setter
    @Getter
    public static class CompanyUser {
        private long id;
        private String name;
    }
}
