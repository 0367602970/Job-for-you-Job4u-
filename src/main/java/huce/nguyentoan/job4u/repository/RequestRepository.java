package huce.nguyentoan.job4u.repository;

import huce.nguyentoan.job4u.domain.UserReq;
import huce.nguyentoan.job4u.util.constant.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<UserReq, Long> {
    boolean existsByUserIdAndStatus(Long userId, RequestStatus status);
}
