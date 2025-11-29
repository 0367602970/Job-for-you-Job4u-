package huce.nguyentoan.job4u.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import huce.nguyentoan.job4u.domain.Job;
import huce.nguyentoan.job4u.domain.Skill;
import huce.nguyentoan.job4u.domain.Subscriber;
import huce.nguyentoan.job4u.domain.Response.Email.ResEmailJob;
import huce.nguyentoan.job4u.repository.JobRepository;
import huce.nguyentoan.job4u.repository.SkillRepository;
import huce.nguyentoan.job4u.repository.SubscriberRepository;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository, JobRepository jobRepository, EmailService emailService) {
        this.skillRepository = skillRepository;
        this.subscriberRepository = subscriberRepository;
        this.emailService = emailService;
        this.jobRepository = jobRepository;
    }
    
    public boolean isExistsByEmail(String email) {
        return this.subscriberRepository.existsByEmail(email);
    }

    public Subscriber createSubscriber(Subscriber subs) {
        //check skills
        if (subs.getSkills() != null) {
            List<Long> reqSkills = subs.getSkills().stream().map(x -> x.getId()).collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            subs.setSkills(dbSkills);
        }
        
        return this.subscriberRepository.save(subs);
    }

    public Subscriber findById(long id) {
        return this.subscriberRepository.findById(id).orElse(null);
    }

    public Subscriber updateSubscriber(Subscriber subsDB, Subscriber subsRequest) {
        //check skills
        if (subsRequest.getSkills() != null) {
            List<Long> reqSkills = subsRequest.getSkills().stream().map(x -> x.getId()).collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            subsDB.setSkills(dbSkills);
        }

        return this.subscriberRepository.save(subsDB);
    }

    public ResEmailJob convertJobToSendEmail(Job job) {
        ResEmailJob res = new ResEmailJob();
        res.setName(job.getName());
        res.setSalary(job.getSalary());
        res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
        List<Skill> skills = job.getSkills();
        List<ResEmailJob.SkillEmail> s = skills.stream().map(skill -> new ResEmailJob.SkillEmail(skill.getName()))
                .collect(Collectors.toList());
        res.setSkills(s);
        return res;
    }


    public void sendSubscribersEmailJobs() {

        Instant oneWeekAgo = Instant.now().minus(7, ChronoUnit.DAYS);

        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs == null || listSubs.isEmpty()) return;

        for (Subscriber sub : listSubs) {
            List<Skill> listSkills = sub.getSkills();
            if (listSkills == null || listSkills.isEmpty()) continue;

            // Lấy job mới
            List<Job> listJobs = this.jobRepository.findNewJobsBySkills(listSkills, oneWeekAgo);

            if (listJobs != null && !listJobs.isEmpty()) {

                List<ResEmailJob> arr = listJobs.stream()
                        .sorted(Comparator.comparing(Job::getCreatedAt).reversed())
                        .limit(5)
                        .map(job -> convertJobToSendEmail(job))
                        .collect(Collectors.toList());


                this.emailService.sendEmailFromTemplateSync(
                        sub.getEmail(),
                        "Cơ hội việc làm HOT mới cập nhật dành cho bạn",
                        "job",
                        sub.getName(),
                        arr
                );
            }
        }
    }

    //Đặt lịch tự động
    // @Scheduled(cron = "*/10 * * * * *")
    // public void testCron() {
    //     System.out.println(">>> TEST CRON");
    // }

    public Subscriber findByEmail(String email) {
        return this.subscriberRepository.findByEmail(email);
    }
}
