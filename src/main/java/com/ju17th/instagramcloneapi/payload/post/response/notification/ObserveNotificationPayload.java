package com.ju17th.instagramcloneapi.payload.post.response.notification;

public class ObserveNotificationPayload extends NotificationPayload {
    private boolean isObserved;

    public boolean isObserved() {
        return isObserved;
    }

    public void setObserved(boolean observed) {
        isObserved = observed;
    }
}
