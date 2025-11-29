package huce.nguyentoan.job4u.controller;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import huce.nguyentoan.job4u.service.SubscriberService;
import huce.nguyentoan.job4u.util.annotation.ApiMessage;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/v1")
public class EmailController {
    private final SubscriberService subscriberService;

    public EmailController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @GetMapping("/email")
    @ApiMessage("Gửi email")
//    @Scheduled(cron = "*/60 * * * * *")
//    @Transactional
    public String sendSimpleEmail() {
        System.out.println(">>> Gui Mail");
        this.subscriberService.sendSubscribersEmailJobs();
        return "Đã gửi mail";
    }
    
}
