package com.portal.ristorinoya.repository;

import com.portal.ristorinoya.entity.ClickEvent;
import com.portal.ristorinoya.entity.ClickEvent.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {
    List<ClickEvent> findTop100ByStatusOrderByClickedAtAsc(Status status);
}
