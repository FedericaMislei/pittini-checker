package it.quix.pittini.checker.dto;

public interface LogConfig extends CheckConfig{

    String path();
    String level();
    String files();
}
