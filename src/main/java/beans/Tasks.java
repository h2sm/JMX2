package beans;

import java.util.concurrent.ScheduledFuture;

public class Tasks {
    private final String name;
    private final ScheduledFuture<?> scheduledFuture;
    private String status;
    private final TaskManager taskManager;

    public Tasks(String name, ScheduledFuture<?> sF, String status, TaskManager tm){
        this.name=name;
        this.scheduledFuture=sF;
        this.status = status;
        this.taskManager=tm;
    }

    public String showPreview(){
       return name + " " + status;
    }

    public String getStatus() {
        return status;
    }

    public ScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }

    public String getName() {
        return name;
    }


    public void setStatus(String status) {//глобальный статус
        this.status = status;
    }
    public String getStatusOfRunnable(){//статус запускаемого класса в taskmanager
        return taskManager.statusOfRunnable();
    }
    
}
