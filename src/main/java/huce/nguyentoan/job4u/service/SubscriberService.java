package huce.nguyentoan.job4u.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import huce.nguyentoan.job4u.domain.Skill;
import huce.nguyentoan.job4u.domain.Subscriber;
import huce.nguyentoan.job4u.repository.SkillRepository;
import huce.nguyentoan.job4u.repository.SubscriberRepository;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
        this.subscriberRepository = subscriberRepository;
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
}
