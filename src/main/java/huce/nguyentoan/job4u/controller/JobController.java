package huce.nguyentoan.job4u.controller;

import huce.nguyentoan.job4u.domain.User;
import huce.nguyentoan.job4u.service.UserService;
import huce.nguyentoan.job4u.util.SecurityUtil;
import org.springframework.web.bind.annotation.*;

import com.turkraft.springfilter.boot.Filter;

import huce.nguyentoan.job4u.domain.Job;
import huce.nguyentoan.job4u.domain.Response.ResultPaginationDTO;
import huce.nguyentoan.job4u.domain.Response.Job.ResCreateJobDTO;
import huce.nguyentoan.job4u.domain.Response.Job.ResUpdateJobDTO;
import huce.nguyentoan.job4u.service.JobService;
import huce.nguyentoan.job4u.util.annotation.ApiMessage;
import huce.nguyentoan.job4u.util.error.IdInvalidException;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("api/v1")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Tạo mới một việc làm")
    public ResponseEntity<ResCreateJobDTO> create(@Valid @RequestBody Job job) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.createJob(job));
    }

    @PutMapping("/jobs")
    public ResponseEntity<ResUpdateJobDTO> update(@Valid @RequestBody Job job) throws IdInvalidException{
        Optional<Job> currentJob = this.jobService.handleFindById(job.getId());
        if (!currentJob.isPresent()) {
            throw new IdInvalidException("Việc làm không tồn tại");
        }
        return ResponseEntity.ok().body(this.jobService.updateJob(job));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Xoá một việc làm")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Job> currentJob = this.jobService.handleFindById(id);
        if (!currentJob.isPresent()) {
            throw new IdInvalidException("Việc làm không tồn tại");
        }
        this.jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("Lấy một việc làm")
    public ResponseEntity<Job> getJobById(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Job> currentJob = this.jobService.handleFindById(id);
        if (!currentJob.isPresent()) {
            throw new IdInvalidException("Việc làm không tồn tại");
        }
        return ResponseEntity.ok().body(currentJob.get());
    }
    
    @GetMapping("/jobs")
    @ApiMessage("Lấy danh sách việc làm")
    public ResponseEntity<ResultPaginationDTO> getAllJobs(@Filter Specification<Job> spec, Pageable pageable) {
        return ResponseEntity.ok().body(this.jobService.fecthAll(spec, pageable));

    }

    @GetMapping("/jobs/{id}/related")
    @ApiMessage("Lấy công việc tương tự")
    public ResponseEntity<List<Job>> fetchRelatedJobs(@PathVariable long id) {
        List<Job> relatedJobs = this.jobService.getRelatedJobs(id);
        return ResponseEntity.ok().body(relatedJobs);
    }

    @GetMapping("/jobs/by-company")
    @ApiMessage("Lấy công việc của 1 công ty")
    public ResponseEntity<List<Job>> fetchJobsByCompanyId() throws IdInvalidException {
        long companyId = this.jobService.findCompanyByUser();
        return ResponseEntity.ok().body(this.jobService.findJobByCompany(companyId));
    }

    @GetMapping("/jobs/count")
    @ApiMessage("Đếm số lượng việc làm")
    public ResponseEntity<Long> getJobsCount() {
        return ResponseEntity.ok(this.jobService.countJob());
    }
}
