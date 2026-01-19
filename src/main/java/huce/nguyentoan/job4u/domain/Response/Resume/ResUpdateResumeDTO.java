package huce.nguyentoan.job4u.domain.Response.Resume;

import java.time.Instant;

import huce.nguyentoan.job4u.util.constant.ResumeStateEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResUpdateResumeDTO {
    private Instant updatedAt;
    private String updatedBy;
    private ResumeStateEnum status;
}
