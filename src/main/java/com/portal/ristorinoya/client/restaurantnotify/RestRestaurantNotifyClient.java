package com.portal.ristorinoya.client.restaurantnotify;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.beans.factory.annotation.Value;
import java.time.OffsetDateTime;

@Component
@ConditionalOnProperty(prefix="ristorino.notification.rest", name="enabled", havingValue = "true")
@RequiredArgsConstructor
public class RestRestaurantNotifyClient implements RestaurantNotifyClient {

    @Value("${ristorino.notification.rest.base-url}") private String baseUrl;
    @Value("${ristorino.notification.rest.path}") private String path;

    private final RestTemplate rt = new RestTemplate();

    @Override
    public void notifyClick(Long restaurantId, Long promotionId, String clickedAtIso) {
        var url = UriComponentsBuilder.fromHttpUrl(baseUrl).path(path).toUriString();
        var body = """
      {"restaurantId": %d, "promotionId": %d, "clickedAt": "%s", "source":"Ristorino"}
      """.formatted(restaurantId, promotionId, clickedAtIso);
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var req = new HttpEntity<>(body, headers);
        rt.postForEntity(url, req, String.class);
    }
}
