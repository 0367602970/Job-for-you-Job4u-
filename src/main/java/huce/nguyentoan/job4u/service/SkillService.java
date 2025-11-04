package huce.nguyentoan.job4u.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import huce.nguyentoan.job4u.domain.Skill;
import huce.nguyentoan.job4u.domain.Response.ResultPaginationDTO;
import huce.nguyentoan.job4u.repository.SkillRepository;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public boolean isNameExist(String name) {
        return this.skillRepository.existsByName(name);
    }

    public Skill handleCreateSkill(Skill s) {
        return this.skillRepository.save(s);
    }

    public Skill fecthSkillById(Long id) {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        if (skillOptional.isPresent()) {
            return skillOptional.get();
        }
        return null;
    }

    public Skill handleUpdateSkill(Skill s) {
        return this.skillRepository.save(s);
    }

    public ResultPaginationDTO fetchAllSkills(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> pageUser = this.skillRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageUser.getContent());

        return rs;
    }

    public void handleDeleteSkill(long id) {
        //delete job (inside job_skill table)
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        Skill currentSkill = skillOptional.get();
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));

        //delete subscriber (inside subscriber_skill table)
        currentSkill.getSubscribers().forEach(subs -> subs.getSkills().remove(currentSkill));

        this.skillRepository.delete(currentSkill);
    }
}
