package it.quix.pittini.checker.dto;

import java.util.Optional;

public interface NotificationsConfig {

    NotificationConfig ok();

    NotificationConfig ko();

    NotificationConfig err();

    Optional<String> icq();

    Optional<String> telegram();

    Optional<String> emails();
}
