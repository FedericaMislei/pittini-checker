package it.quix.pittini.checker.dto;

import java.util.List;
import java.util.Optional;

public interface ConsoleConfig {

    String url();

    int sleepTimeInSecond();

    String envType();

    Optional<List<String>> alertEmails();

    Optional<List<String>> telegramKeys();
}
