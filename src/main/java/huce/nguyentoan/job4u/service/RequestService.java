package huce.nguyentoan.job4u.service;

import huce.nguyentoan.job4u.domain.Job;
import huce.nguyentoan.job4u.domain.Request.ReqCreateHR;
import huce.nguyentoan.job4u.domain.Resume;
import huce.nguyentoan.job4u.domain.User;
import huce.nguyentoan.job4u.domain.UserReq;
import huce.nguyentoan.job4u.repository.RequestRepository;
import huce.nguyentoan.job4u.util.constant.RequestStatus;
import huce.nguyentoan.job4u.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository repository;
    private final UserService userService;
    private final EmailService emailService;

    public boolean existsPendingRequest(Long userId) {
        return repository.existsByUserIdAndStatus(userId, RequestStatus.PENDING);
    }

    public UserReq createRequest(Long userId, ReqCreateHR dto) {
        UserReq req = new UserReq();
        req.setUserId(userId);
        req.setCompanyName(dto.getCompanyName());
        req.setCompanyAddress(dto.getCompanyAddress());
        req.setStatus(RequestStatus.PENDING);
        return repository.save(req);
    }

    public List<UserReq> findRequest(){
        return repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public UserReq findById(Long id){
        return repository.findById(id).orElse(null);
    }

    public void saveRequest(UserReq userReq) {
        this.repository.save(userReq);
    }

    public void sendEmailAfterRequest(String email) {
        User user = this.userService.handleGetUserByUsername(email);
        this.emailService.sendEmailRequest(
                email,
                "[JOB FOR YOU] THÔNG BÁO",
                "request",
                user.getName());

    }

    public void sendEmailAfterApprove(String email) {
        User user = this.userService.handleGetUserByUsername(email);
        this.emailService.sendEmailRequest(
                email,
                "[JOB FOR YOU] THÔNG BÁO",
                "approve",
                user.getName());

    }

    public void sendEmailAfterReject(String email) {
        User user = this.userService.handleGetUserByUsername(email);
        this.emailService.sendEmailRequest(
                email,
                "[JOB FOR YOU] THÔNG BÁO",
                "reject",
                user.getName());

    }
}
