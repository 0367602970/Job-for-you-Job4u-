package huce.nguyentoan.job4u.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import huce.nguyentoan.job4u.domain.Resume;
import huce.nguyentoan.job4u.domain.User;
import huce.nguyentoan.job4u.repository.ResumeRepository;
import huce.nguyentoan.job4u.util.SecurityUtil;
import huce.nguyentoan.job4u.util.error.IdInvalidException;
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
    private final UserService userService;
    private final ResumeRepository resumeRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository,
                      UserService userService, ResumeRepository resumeRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.userService = userService;
        this.resumeRepository = resumeRepository;
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
        Optional<Job> jobOpt = this.jobRepository.findById(id);
        if (jobOpt.isPresent()) {
            Job job = jobOpt.get();
            List<Resume> resumes = this.resumeRepository.findByJob(job);
            this.resumeRepository.deleteAll(resumes);
        }
        this.jobRepository.deleteById(id);
    }

    public ResultPaginationDTO fecthAll(Specification<Job> spec, Pageable pageable) {
        Specification<Job> finalSpec;

        if (spec == null) {
            finalSpec = (root, query, cb) -> cb.isTrue(root.get("active"));
        } else {
            finalSpec = spec.and((root, query, cb) -> cb.isTrue(root.get("active")));
        }

        Page<Job> pageJobs = this.jobRepository.findAll(finalSpec, pageable);

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

    public List<Job> findAll() {
        return this.jobRepository.findAll();
    }

    public List<Job> getJobByCompany(long companyId) {
        return this.jobRepository.findByCompanyIdAndActiveTrue(companyId);
    }

    public List<Job> getRelatedJobs(long jobId) {
        Job job = this.jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Không tìm thấy công việc"));

        List<Long> skillId = job.getSkills()
                .stream()
                .map(Skill::getId)
                .toList();

        if (skillId.isEmpty()) {
            return List.of();
        }

        return this.jobRepository.findRelatedJobs(jobId, skillId);
    }

    public long findCompanyByUser() throws IdInvalidException{
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        if (email.isEmpty()) {
            throw new IdInvalidException("Không tìm thấy người dùng");
        }
        User currentUser = this.userService.handleGetUserByUsername(email);
        if (currentUser == null) {
            throw new IdInvalidException("Không tìm thấy người dùng");
        }
        long companyId = currentUser.getCompany().getId();
        if (companyId == 0) {
            throw new IdInvalidException("Bạn không thuộc công ty nào");
        }
        return companyId;
    }

    public List<Job> findJobByCompany(long companyId) {
        return this.jobRepository.findByCompanyId(companyId);
    }

    public long countJob() {
        return this.jobRepository.count();
    }
}
