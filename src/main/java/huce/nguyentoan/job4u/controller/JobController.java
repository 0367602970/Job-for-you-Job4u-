package huce.nguyentoan.job4u.controller;

import huce.nguyentoan.job4u.domain.Response.RestResponse;
import huce.nguyentoan.job4u.domain.User;
import huce.nguyentoan.job4u.dto.CustomerDto;
import huce.nguyentoan.job4u.dto.ExportColumn;
import huce.nguyentoan.job4u.service.UserService;
import huce.nguyentoan.job4u.util.CsvUtils;
import huce.nguyentoan.job4u.util.ExcelUtils;
import huce.nguyentoan.job4u.util.ExportFormater;
import huce.nguyentoan.job4u.util.SecurityUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

import java.io.IOException;
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
    private final ExcelUtils excelUtils;
    private final CsvUtils csvUtils;

    public JobController(JobService jobService, ExcelUtils excelUtils, CsvUtils csvUtils) {
        this.jobService = jobService;
        this.excelUtils = excelUtils;
        this.csvUtils = csvUtils;
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
    @ApiMessage("Xoá việc làm thành công")
    public ResponseEntity<RestResponse<String>> delete(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Job> currentJob = this.jobService.handleFindById(id);
        if (currentJob.isEmpty()) {
            throw new IdInvalidException("Việc làm không tồn tại");
        }
        this.jobService.deleteJob(id);

        RestResponse<String> response = new RestResponse<>();
        response.setStatusCode(200);
        response.setMessage("Xóa việc làm thành công");
        response.setData(null);
        return ResponseEntity.ok(response);
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

    @GetMapping("/jobs/export")
    public void exportJob(
            @Filter Specification<Job> spec,
            Pageable pageable,
            HttpServletResponse response
    ) throws IOException {


        ResultPaginationDTO result =
                jobService.fecthAll(
                        spec,
                        pageable
                );


        List<Job> jobs =
                (List<Job>) result.getResult();


        List<ExportColumn<Job>> columns = List.of(

                new ExportColumn<>(
                        "Tên Job",
                        Job::getName,
                        ExportFormater.STRING
                ),

                new ExportColumn<>(
                        "Địa điểm",
                        Job::getLocation,
                        ExportFormater.STRING
                ),

                new ExportColumn<>(
                        "Mức lương",
                        Job::getSalary,
                        ExportFormater.NUMBER
                ),

                new ExportColumn<>(
                        "Level",
                        Job::getLevel,
                        ExportFormater.STRING
                )
        );


        byte[] file =
                excelUtils.exportXlsx(
                        jobs,
                        columns
                );


        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );


        response.setHeader(
                "Content-Disposition",
                "attachment; filename=jobs.xlsx"
        );


        response.getOutputStream()
                .write(file);


        response.getOutputStream()
                .flush();
    }

    @GetMapping("/jobs/export/csv")
    public void exportCsv(
            HttpServletResponse response
    ) throws IOException {
        List<ExportColumn<Job>> columns = List.of(

                new ExportColumn<>(
                        "Tên Job",
                        Job::getName,
                        ExportFormater.STRING
                ),

                new ExportColumn<>(
                        "Địa điểm",
                        Job::getLocation,
                        ExportFormater.STRING
                ),

                new ExportColumn<>(
                        "Mức lương",
                        Job::getSalary,
                        ExportFormater.NUMBER
                ),

                new ExportColumn<>(
                        "Level",
                        Job::getLevel,
                        ExportFormater.STRING
                )
        );


        byte[] file =
                csvUtils.exportCsv(
                        jobService.findAll(),
                        columns
                );


        response.setContentType(
                "text/csv; charset=UTF-8"
        );


        response.setHeader(
                "Content-Disposition",
                "attachment; filename=jobs.csv"
        );


        response.getOutputStream()
                .write(file);
    }
}
