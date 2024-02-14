package it.quix.pittini.checker.dto;

import java.time.LocalDateTime;

public class CheckResult {

    private Boolean lastCheck;
    private Boolean prevCheck;
    private LocalDateTime lastError;

    public void error() {
        prevCheck = lastCheck;
        lastCheck = null;
    }

    public void ok() {
        prevCheck = lastCheck;
        lastCheck = true;
    }

    public void ko() {
        prevCheck = lastCheck;
        lastCheck = false;
    }

    public boolean isToNotify(String errorCron) {
        if(lastCheck == null) {
            // Errore sempre
            return true;
        }
        if(lastCheck) {
            // Se prima ero in errore
            if(prevCheck == null || !prevCheck) {
                return true;
            }
        }
        if(!lastCheck) {
            // Se prima ero in uno stato diverso
            if(prevCheck == null || prevCheck) {
                return true;
            }
            // Se prima ero in errore ed è passato il timeout
            if(prevCheck != null && !prevCheck) {
                // TODO
                return false;
            }
        }
        return false;
    }

    public String getIcqPrefix() {
        if(lastCheck == null) {
            return ":no_entry_sign: ";
        }
        if(lastCheck) {
           return ":white_check_mark: ";
        }
        if(!lastCheck) {
           return ":red_circle: ";
        }
        return "";
    }
}
