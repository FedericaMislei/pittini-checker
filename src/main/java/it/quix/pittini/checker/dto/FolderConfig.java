package it.quix.pittini.checker.dto;

public interface FolderConfig extends  CheckConfig{

    String path();
    String olderThenMinutes();
    String checkSubfolders();

}
