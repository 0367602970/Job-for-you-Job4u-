package huce.nguyentoan.job4u.domain;

import java.time.Instant;

import huce.nguyentoan.job4u.util.constant.GenderEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String email;
    private String password;

    private int age;

    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    private String address;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;
    
    private Instant createdAt;
    private Instant updatedAt;
    private String createby;
    private String updateby;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company  company;


}
