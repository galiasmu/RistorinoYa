package com.portal.ristorinoya.client.restaurantnotify;


public interface RestaurantNotifyClient {
    void notifyClick(Long restaurantId, Long promotionId, String clickedAtIso);
}
