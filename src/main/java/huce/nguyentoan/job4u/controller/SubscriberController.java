package huce.nguyentoan.job4u.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import huce.nguyentoan.job4u.domain.Subscriber;
import huce.nguyentoan.job4u.service.SubscriberService;
import huce.nguyentoan.job4u.util.SecurityUtil;
import huce.nguyentoan.job4u.util.annotation.ApiMessage;
import huce.nguyentoan.job4u.util.error.IdInvalidException;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("Tạo mới một subscriber")
    public ResponseEntity<Subscriber> create(@Valid @RequestBody Subscriber sub) throws IdInvalidException{
        //check email
        boolean isExist = this.subscriberService.isExistsByEmail(sub.getEmail());
        if (isExist) {
            throw new IdInvalidException("Email đã tồn tại");
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.createSubscriber(sub));
    }
    
    @PutMapping("/subscribers")
    public ResponseEntity<Subscriber> update(@RequestBody Subscriber subsRequest) throws IdInvalidException{
        //check id
        Subscriber subDB = this.subscriberService.findById(subsRequest.getId());
        if (subDB == null) {
            throw new IdInvalidException("Id không tồn tại");
        }
        
        return ResponseEntity.ok().body(this.subscriberService.updateSubscriber(subDB, subsRequest));
    }

    @PostMapping("/subscribers/skills")
    @ApiMessage("Lấy kỹ năng đã đăng ký")
    public ResponseEntity<Subscriber> getSubscribersSkill(){
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        
        return ResponseEntity.ok().body(this.subscriberService.findByEmail(email));
    }
    
}
