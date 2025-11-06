package huce.nguyentoan.job4u.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import huce.nguyentoan.job4u.domain.Subscriber;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long>{
    boolean existsByEmail(String email);

    Subscriber findByEmail(String email);
}
