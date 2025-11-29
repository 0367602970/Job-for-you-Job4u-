package huce.nguyentoan.job4u.controller;

import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import huce.nguyentoan.job4u.domain.Company;
import huce.nguyentoan.job4u.domain.Job;
import huce.nguyentoan.job4u.domain.User;
import huce.nguyentoan.job4u.service.UserService;
import huce.nguyentoan.job4u.util.SecurityUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import huce.nguyentoan.job4u.domain.Resume;
import huce.nguyentoan.job4u.domain.Response.ResultPaginationDTO;
import huce.nguyentoan.job4u.domain.Response.Resume.ResCreateResumeDTO;
import huce.nguyentoan.job4u.domain.Response.Resume.ResFetchResumeDTO;
import huce.nguyentoan.job4u.domain.Response.Resume.ResUpdateResumeDTO;
import huce.nguyentoan.job4u.service.ResumeService;
import huce.nguyentoan.job4u.util.annotation.ApiMessage;
import huce.nguyentoan.job4u.util.error.IdInvalidException;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;
    private final UserService userService;
    private final FilterSpecificationConverter filterSpecificationConverter;
    private final FilterBuilder  filterBuilder;
    
    public ResumeController(ResumeService resumeService, UserService userService, FilterSpecificationConverter filterSpecificationConverter, FilterBuilder filterBuilder) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.filterSpecificationConverter = filterSpecificationConverter;
        this.filterBuilder = filterBuilder;
    }

    @PostMapping("/resumes")
    @ApiMessage("Tạo mới hồ sơ")
    public ResponseEntity<ResCreateResumeDTO> create(@Valid @RequestBody Resume resume) throws IdInvalidException{
        boolean isIdExist = this.resumeService.checkResumeExistByUserAndJob(resume);
        if(!isIdExist){
            throw new IdInvalidException("User id/Job id không tồn tại");
        }
        ResCreateResumeDTO res = this.resumeService.createResume(resume);
        this.resumeService.sendEmailAfterApply(resume);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
    
    @PutMapping("/resumes")
    public ResponseEntity<ResUpdateResumeDTO> update(@RequestBody Resume resume) throws IdInvalidException {
        Optional<Resume> reqResumeOptional = this.resumeService.fetchById(resume.getId());
        if (reqResumeOptional.isEmpty()) {
            throw new IdInvalidException("Hồ sơ với id " + resume.getId() + " không tồn tại");
        }

        Resume reqResume = reqResumeOptional.get();
        reqResume.setStatus(resume.getStatus());
        
        return ResponseEntity.ok().body(this.resumeService.updateResume(reqResume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Xóa hồ sơ")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Resume> reqResumeOptional = this.resumeService.fetchById(id);
        if (reqResumeOptional.isEmpty()) {
            throw new IdInvalidException("Hồ sơ với id " + id + " không tồn tại");
        }

        this.resumeService.deleteResume(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/resumes/{id}")
    public ResponseEntity<ResFetchResumeDTO> fetchResumeById(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Resume> reqResumeOptional = this.resumeService.fetchById(id);
        if (reqResumeOptional.isEmpty()) {
            throw new IdInvalidException("Hồ sơ với id " + id + " không tồn tại");
        }
        return ResponseEntity.ok().body(this.resumeService.getResume(reqResumeOptional.get()));
    }
    
    @GetMapping("/resumes")
    public ResponseEntity<ResultPaginationDTO> fetchAllResume(@Filter Specification<Resume> spec, Pageable pageable) {
        List<Long> arrJobIds = null;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User currentUser = this.userService.handleGetUserByUsername(email);
        Specification<Resume> finalSpec = spec;

        if (currentUser != null) {

            // Nếu là ADMIN -> không filter job theo công ty
            if ("SUPER_ADMIN".equals(currentUser.getRole().getName())) {
            }

            // Nếu là HR -> filter theo company_id
            else if ("HR".equals(currentUser.getRole().getName())) {

                Company userCompany = currentUser.getCompany();
                if (userCompany != null) {
                    List<Job> companyJobs = userCompany.getJobs();

                    if (companyJobs != null && !companyJobs.isEmpty()) {
                        arrJobIds = companyJobs.stream()
                                .map(Job::getId)
                                .collect(Collectors.toList());

                        Specification<Resume> jobInspec = filterSpecificationConverter.convert(
                                filterBuilder.field("job").in(
                                        filterBuilder.input(arrJobIds)
                                ).get()
                        );

                        finalSpec = jobInspec.and(spec);
                    }
                }
            }
        }

        return ResponseEntity.ok().body(this.resumeService.getAllResume(finalSpec, pageable));
    }
    
    @PostMapping("/resumes/by-user")
    @ApiMessage("Lấy danh sách CV bởi người dùng")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(Pageable pageable) {
        
        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }
    
}
