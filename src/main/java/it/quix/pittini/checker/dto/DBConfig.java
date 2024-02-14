package it.quix.pittini.checker.dto;

public interface DBConfig extends CheckConfig,RestConfig{

    String kind();
    String driver();
    String username();
    String password();
    String sql();


}
