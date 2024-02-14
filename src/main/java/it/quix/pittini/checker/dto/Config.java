package it.quix.pittini.checker.dto;

import io.smallrye.config.ConfigMapping;

import java.util.Map;

@ConfigMapping(prefix = "config", namingStrategy = ConfigMapping.NamingStrategy.VERBATIM)
public interface Config {

    String name();

    ConsoleConfig console();

    Map<String, RestConfig> rest();

    Map<String, NotificationsConfig> notification();

    Map<String,FolderConfig> folder();

    Map<String,DBConfig> db();

    Map<String,FsConfig> fs();

    Map<String,LogConfig> log();
}
