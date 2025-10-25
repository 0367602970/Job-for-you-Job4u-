package huce.nguyentoan.job4u.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import huce.nguyentoan.job4u.domain.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    Page<Job> findAll(Specification<Job> spec, Pageable pageable);
    
}
