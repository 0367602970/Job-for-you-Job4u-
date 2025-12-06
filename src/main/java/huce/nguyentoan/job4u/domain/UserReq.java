package huce.nguyentoan.job4u.domain;

import huce.nguyentoan.job4u.util.constant.RequestStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "user_req")
@Getter
@Setter
public class UserReq {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String companyName;
    private String companyAddress;

    private long userId;

    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;

    private Instant createdAt = Instant.now();
}
