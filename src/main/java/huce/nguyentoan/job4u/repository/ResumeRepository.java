package huce.nguyentoan.job4u.repository;

import huce.nguyentoan.job4u.domain.Job;
import huce.nguyentoan.job4u.util.constant.ResumeStateEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import huce.nguyentoan.job4u.domain.Resume;

import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long>, JpaSpecificationExecutor<Resume>{
    boolean existsByUserIdAndJobId(long userId, long jobId);

    int countByJobIdAndStatus(long jobId, ResumeStateEnum status);

    List<Resume> findByJob(Job job);
}
