package com.portal.ristorinoya.repository;

import com.portal.ristorinoya.entity.Restaurant;
import com.portal.ristorinoya.entity.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Page<Promotion> findByIsActiveTrueAndStartAtBeforeAndEndAtAfterOrderByPriorityDesc(
            OffsetDateTime now1, OffsetDateTime now2, Pageable pageable);
}
