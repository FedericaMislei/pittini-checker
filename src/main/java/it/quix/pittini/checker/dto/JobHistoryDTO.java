package it.quix.pittini.checker.dto;
import lombok.Data;
@Data
public class JobHistoryDTO {
    private int counter;
    private String name;
    private String context;
    private long avgDuration;
    private String state;

    @Override
    public String toString(){
    return "Number of Executions: "+counter+
    "\nName: "+name+
    "\nContext: "+context+
    "\nAverage Duration: "+avgDuration+"ms"+
    "\nState of Last Iteration: "+state+
            "\n \n";
    }
}
