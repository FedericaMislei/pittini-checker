package it.quix.pittini.checker.dto;


import io.smallrye.common.constraint.Nullable;

public interface RestConfig extends CheckConfig {
    @Nullable
    String url();

    @Nullable
    String minute();

}
