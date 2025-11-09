package com.portal.ristorinoya.scheduler;


import com.portal.ristorinoya.client.restaurantnotify.RestaurantNotifyClient;
import com.portal.ristorinoya.entity.ClickEvent;
import com.portal.ristorinoya.repository.ClickEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component @RequiredArgsConstructor @Slf4j
public class ClickNotifyScheduler {

    private final ClickEventRepository repo;
    private final RestaurantNotifyClient client;

    @Value("${ristorino.notification.retries.max-attempts:5}") private int maxAttempts;

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    //@Scheduled(fixedDelayString = "${ristorino.notification.retries.fixed-delay-ms:60000}")
    public void processPending() {
        var pendings = repo.findTop100ByStatusOrderByClickedAtAsc(ClickEvent.Status.PENDING);
        for (var ev : pendings) {
            try {
                client.notifyClick(ev.getRestaurant().getId(), ev.getPromotion().getId(), ev.getClickedAt().format(ISO));
                ev.setStatus(ClickEvent.Status.SENT);
            } catch (Exception ex) {
                ev.setAttempts(ev.getAttempts() + 1);
                if (ev.getAttempts() >= maxAttempts) ev.setStatus(ClickEvent.Status.FAILED);
                log.warn("Notify failed (attempt {} of {}): {}", ev.getAttempts(), maxAttempts, ex.getMessage());
            }
            repo.save(ev);
        }
    }
}
