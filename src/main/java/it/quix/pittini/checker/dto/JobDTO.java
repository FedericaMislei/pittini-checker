package it.quix.pittini.checker.dto;

import lombok.Data;

@Data
public class JobDTO {
    private String name;
    private String context;
    private String cronExpression;
    private boolean recoveryYN;
    private String className;
    private boolean isDisabled;

    public String toString(){
        return "Name: "+name+
                "\nContext: "+context+
                "\nCronEXP: "+cronExpression+
                "\nisRecovery: "+recoveryYN+
                "\nClass Name: "+className+
                "\n  \n";
    }
}
