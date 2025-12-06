package huce.nguyentoan.job4u.controller;

import huce.nguyentoan.job4u.domain.Company;
import huce.nguyentoan.job4u.domain.Request.ReqCreateHR;
import huce.nguyentoan.job4u.domain.Role;
import huce.nguyentoan.job4u.domain.User;
import huce.nguyentoan.job4u.domain.UserReq;
import huce.nguyentoan.job4u.service.CompanyService;
import huce.nguyentoan.job4u.service.RequestService;
import huce.nguyentoan.job4u.service.RoleService;
import huce.nguyentoan.job4u.service.UserService;
import huce.nguyentoan.job4u.util.SecurityUtil;
import huce.nguyentoan.job4u.util.annotation.ApiMessage;
import huce.nguyentoan.job4u.util.constant.RequestStatus;
import huce.nguyentoan.job4u.util.error.IdInvalidException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1")
public class RequestController {
    private final RequestService requestService;
    private final CompanyService companyService;
    private final UserService userService;
    private final RoleService roleService;

    public RequestController(RequestService requestService, CompanyService companyService,
                             UserService userService, RoleService roleService) {
        this.requestService = requestService;
        this.companyService = companyService;
        this.userService = userService;
        this.roleService = roleService;
    }

    // User gửi yêu cầu trở thành nhà tuyển dụng
    @PostMapping("/employer-request")
    @ApiMessage("Gửi yêu cầu trở thành nhà tuyển dụng")
    public ResponseEntity<?> createEmployerRequest(
            @RequestBody ReqCreateHR dto) {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User currentUser = this.userService.handleGetUserByUsername(email);
        long userId = currentUser.getId();

        if (requestService.existsPendingRequest(userId)) {
            return ResponseEntity.badRequest().body("Bạn đã gửi yêu cầu, vui lòng chờ duyệt.");
        }

        requestService.createRequest(userId, dto);
        this.requestService.sendEmailAfterRequest(email);

        return ResponseEntity.ok("Đã gửi yêu cầu, vui lòng chờ admin duyệt.");
    }

    //ADMIN xem yêu cầu
    @GetMapping("/admin/employer-request")
    @ApiMessage("Danh sách yêu cầu trở thành nhà tuyển dụng")
    public ResponseEntity<?> getAllRequests() {
        return ResponseEntity.ok(requestService.findRequest());
    }

    //ADMIN chấp nhận và tạo thông tin
    @PostMapping("/admin/employer-request/{id}/approve")
    @ApiMessage("Duyệt yêu cầu nhà tuyển dụng")
    public ResponseEntity<?> approve(@PathVariable Long id) throws IdInvalidException {

        UserReq req = requestService.findById(id);
        if (req == null) {
            throw new IdInvalidException("Không tìm thấy yêu cầu");
        }

        if (req.getStatus() != RequestStatus.PENDING) {
            return ResponseEntity.badRequest().body("Yêu cầu đã được xử lý");
        }

        Role r = this.roleService.fetchByName("HR");

        // 1. Tạo công ty
        Company c = new Company();
        c.setName(req.getCompanyName());
        c.setAddress(req.getCompanyAddress());
        this.companyService.handleCreateCompany(c);

        // 2. Gán companyId cho user
        User u = userService.fetchUserById(req.getUserId());
        Company company = this.companyService.findCompany(c.getId());
        u.setCompany(company);
        u.setRole(r);
        this.userService.saveUser(u);

        // 3. Cập nhật trạng thái request
        req.setStatus(RequestStatus.APPROVED);
        this.requestService.saveRequest(req);
        this.requestService.sendEmailAfterApprove(u.getEmail());

        return ResponseEntity.ok("Đã duyệt yêu cầu");
    }

    @PostMapping("/admin/employer-request/{id}/reject")
    @ApiMessage("Từ chối yêu cầu nhà tuyển dụng")
    public ResponseEntity<?> reject(@PathVariable Long id) throws IdInvalidException {
        UserReq req = this.requestService.findById(id);
        if (req == null) {
            throw new IdInvalidException("Không tìm thấy yêu cầu");
        }

        User u = userService.fetchUserById(req.getUserId());

        req.setStatus(RequestStatus.REJECTED);
        this.requestService.saveRequest(req);
        this.requestService.sendEmailAfterReject(u.getEmail());

        return ResponseEntity.ok("Đã từ chối yêu cầu");
    }


}
