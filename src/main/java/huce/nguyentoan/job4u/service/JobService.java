package huce.nguyentoan.job4u.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import huce.nguyentoan.job4u.domain.Job;
import huce.nguyentoan.job4u.domain.Skill;
import huce.nguyentoan.job4u.domain.Response.ResultPaginationDTO;
import huce.nguyentoan.job4u.domain.Response.Job.ResCreateJobDTO;
import huce.nguyentoan.job4u.domain.Response.Job.ResUpdateJobDTO;
import huce.nguyentoan.job4u.repository.JobRepository;
import huce.nguyentoan.job4u.repository.SkillRepository;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
    }

    public ResCreateJobDTO createJob(Job j) {
        if (j.getSkills() != null) {
            List<Long> reqSkills = j.getSkills().stream().map(x -> x.getId()).collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            j.setSkills(dbSkills);
        }

        Job currentJob = this.jobRepository.save(j);

        ResCreateJobDTO dto = new ResCreateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setLocation(currentJob.getLocation());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setActive(currentJob.isActive());
        dto.setCreatedAt(currentJob.getCreatedAt());
        dto.setCreatedBy(currentJob.getCreatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills().stream().map(item -> item.getName()).collect(Collectors.toList());
            dto.setSkills(skills);
        }
        return dto;
    }

    public Optional<Job> handleFindById(long id) {
        return this.jobRepository.findById(id);
    }

    public ResUpdateJobDTO updateJob(Job j) {
        if (j.getSkills() != null) {
            List<Long> reqSkills = j.getSkills().stream().map(x -> x.getId()).collect(Collectors.toList());

            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            j.setSkills(dbSkills);
        }

        Job currentJob = this.jobRepository.save(j);

        ResUpdateJobDTO dto = new ResUpdateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setLocation(currentJob.getLocation());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setActive(currentJob.isActive());
        dto.setUpdatedAt(currentJob.getUpdatedAt());
        dto.setUpdatedBy(currentJob.getUpdatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills().stream().map(item -> item.getName()).collect(Collectors.toList());
            dto.setSkills(skills);
        }
        return dto;
    }

    public void deleteJob(long id) {
        this.jobRepository.deleteById(id);
    }

    public ResultPaginationDTO fecthAll(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageJobs = this.jobRepository.findAll(spec, pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageJobs.getTotalPages());
        mt.setTotal(pageJobs.getTotalElements());

        result.setMeta(mt);
        result.setResult(pageJobs.getContent());

        return result;
    }

    public List<Job> getJobByCompany(long companyId) {
        return this.jobRepository.findByCompanyId(companyId);
    }
}
