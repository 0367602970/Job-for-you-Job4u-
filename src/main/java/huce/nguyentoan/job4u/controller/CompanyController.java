package huce.nguyentoan.job4u.controller;

import huce.nguyentoan.job4u.domain.Job;
import huce.nguyentoan.job4u.domain.User;
import huce.nguyentoan.job4u.service.JobService;
import huce.nguyentoan.job4u.service.UserService;
import huce.nguyentoan.job4u.util.SecurityUtil;
import huce.nguyentoan.job4u.util.error.IdInvalidException;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import huce.nguyentoan.job4u.domain.Company;
import huce.nguyentoan.job4u.domain.Response.ResultPaginationDTO;
import huce.nguyentoan.job4u.service.CompanyService;
import huce.nguyentoan.job4u.util.annotation.ApiMessage;
import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;
    private final JobService jobService;
    private final UserService userService;

    public CompanyController(CompanyService companyService,  JobService jobService,  UserService userService) {
        this.companyService = companyService;
        this.jobService = jobService;
        this.userService = userService;
    }
    
    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company reqCompany) {
        Company company = this.companyService.handleCreateCompany(reqCompany);
        return ResponseEntity.status(HttpStatus.CREATED).body(company);
    }

    @GetMapping("/companies/{id}")
    @ApiMessage("Lấy công ty theo id")
    public ResponseEntity<Company> getCompany(@PathVariable("id") long id) throws IdInvalidException {
        if (this.companyService.findCompany(id) == null) {
            throw new IdInvalidException("Công ty không tồn tại");
        }
        return ResponseEntity.ok().body(this.companyService.findCompany(id));
    }

    @GetMapping("/companies")
    @ApiMessage("Lấy danh sách công ty thành công")
    public ResponseEntity<ResultPaginationDTO> getCompany(@Filter Specification<Company> spec, Pageable pageable) {
        
        return ResponseEntity.ok(this.companyService.handleGetCompany(spec, pageable));
    }
    
    @PutMapping("/companies")
    @ApiMessage("Cập nhật thông tin công ty thành công")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company reqCompany) {
        Company updateCompany = this.companyService.handleUpdateCompany(reqCompany);
        return ResponseEntity.ok(updateCompany);
    }
    
    @DeleteMapping("/companies/{id}")
    @ApiMessage("Xoá công ty thành công")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") long id) {
        this.companyService.handleDeleteCompany(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/companies/{id}/jobs")
    @ApiMessage("Lấy việc làm theo công ty")
    public ResponseEntity<List<Job>> getJobsOfCompany(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(this.jobService.getJobByCompany(id));
    }

    // --------------Chức năng của HR--------------
    @GetMapping("/companies/by-hr")
    @ApiMessage("Lấy thông tin công ty của HR")
    public ResponseEntity<Company> getCompanyByHR() throws IdInvalidException{

        long companyId = this.jobService.findCompanyByUser();
        if (companyId == 0) {
            throw new IdInvalidException("Bạn không thuộc công ty nào");
        }
        Company com = this.companyService.findCompany(companyId);
        return ResponseEntity.ok().body(com);
    }

    @PutMapping("/companies/by-hr")
    @ApiMessage("Cập nhật thông tin công ty thành công")
    public ResponseEntity<Company> updateCompanyByHR(@Valid @RequestBody Company reqCompany) throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        if (email.isEmpty()) {
            throw new IdInvalidException("Không tìm thấy người dùng");
        }

        User currentUser = this.userService.handleGetUserByUsername(email);
        if (currentUser == null) {
            throw new IdInvalidException("Không tìm thấy người dùng");
        }

        Company company = currentUser.getCompany();
        if (company == null) {
            throw new IdInvalidException("Bạn không thuộc công ty nào");
        }

        reqCompany.setId(company.getId());

        Company updated = this.companyService.handleUpdateCompany(reqCompany);
        if (updated == null) {
            throw new IdInvalidException("Cập nhật công ty thất bại");
        }

        return ResponseEntity.ok(updated);
    }

    @GetMapping("/companies/count")
    @ApiMessage("Đếm số lượng công ty")
    public ResponseEntity<Long> countCompany() {
        return ResponseEntity.ok(this.companyService.countCompany());
    }
}
