package huce.nguyentoan.job4u.domain.Response.Job;

import java.time.Instant;
import java.util.List;

import huce.nguyentoan.job4u.util.constant.LevelEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResUpdateJobDTO {
    private long id;
    private String name;
    private String location;
    private double salary;
    private int quantity;
    private LevelEnum level;
    private Instant startDate;
    private Instant endDate;
    private boolean isActive;

    private List<String> skills;

    private Instant updatedAt;
    private String updatedBy;
}
