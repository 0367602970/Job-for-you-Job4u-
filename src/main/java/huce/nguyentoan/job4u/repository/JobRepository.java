package huce.nguyentoan.job4u.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import huce.nguyentoan.job4u.domain.Job;
import huce.nguyentoan.job4u.domain.Skill;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    Page<Job> findAll(Specification<Job> spec, Pageable pageable);

    List<Job> findBySkillsIn(List<Skill> skills);

    @Query("""
        SELECT j FROM Job j JOIN j.skills s
        WHERE s IN :skills AND j.active = true
        AND (j.createdAt >= :cutoff OR j.updatedAt >= :cutoff)
        """)
    List<Job> findNewJobsBySkills(@Param("skills") List<Skill> skills,
                                  @Param("cutoff") Instant cutoff);

    List<Job> findByCompanyIdAndActiveTrue(long companyId);
    List<Job> findByCompanyId(long companyId);

    @Query("""
            SELECT j FROM Job j
            JOIN j.skills s
            WHERE s.id IN :skillId AND j.id <> :currentJobId
            AND j.active = true
            GROUP BY j.id
            ORDER BY COUNT(s.id) DESC
            """)
    List<Job> findRelatedJobs(
            @Param("currentJobId") long currentJobId,
            @Param("skillId") List<Long> skillId
    );

    long count();
}
