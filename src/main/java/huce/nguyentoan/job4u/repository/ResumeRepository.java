package huce.nguyentoan.job4u.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import huce.nguyentoan.job4u.domain.Resume;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long>, JpaSpecificationExecutor<Resume>{
    boolean existsByUserIdAndJobId(long userId, long jobId);
}
