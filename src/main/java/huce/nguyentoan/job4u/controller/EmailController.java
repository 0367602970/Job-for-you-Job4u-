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
//  @Scheduled(cron = "*/86400 * * * * *")
//  @Transactional
    public String sendSimpleEmail() {
        // this.emailService.sendSimpleEmail();
        // this.emailService.sendEmailSync("toandeptrai3108@gmail.com", "send email from Job4U", "<h1> <b> hello </b> </h1>", false, true);
        // this.emailService.sendEmailFromTemplateSync("toandeptrai3108@gmail.com", "Send email from Job4U", "job");
        System.out.println(">>> Gui Mail");
        this.subscriberService.sendSubscribersEmailJobs();
        return "Đã gửi mail";
    }
    
}
