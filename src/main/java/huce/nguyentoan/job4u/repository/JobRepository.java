package huce.nguyentoan.job4u.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import huce.nguyentoan.job4u.domain.Job;
import huce.nguyentoan.job4u.domain.Skill;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    Page<Job> findAll(Specification<Job> spec, Pageable pageable);

    List<Job> findBySkillsIn(List<Skill> skills);

    List<Job> findByCompanyId(long companyId);
}
