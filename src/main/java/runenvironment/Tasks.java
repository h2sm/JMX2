package runenvironment;

import java.util.concurrent.ScheduledFuture;

public class Tasks {
    private final String name;
    private ScheduledFuture<?> scheduledFuture;
    private String status;
    private final TaskManager taskManager;
    private final String classpath;
    private final String mainClass;
    private final String[] args;
    private boolean isProfiling;
    private int period;

    public Tasks(String name, ScheduledFuture<?> sF, String status, TaskManager tm, String cp, String mc, String[] ar, boolean isProfiling, int period){
        this.name=name;
        this.scheduledFuture=sF;
        this.status = status;
        this.taskManager=tm;
        this.classpath = cp;
        this.mainClass=mc;
        this.args=ar;
        this.isProfiling=isProfiling;
        this.period=period;
    }

    public boolean isProfiling() {
        return isProfiling;
    }

    public void setProfiling() {
        isProfiling = true;
    }

    public void stopProfiling() {
        isProfiling=false;
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

    public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
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
    public TaskManager getTaskManager(){return taskManager;}

    public String getClasspath() {
        return classpath;
    }

    public String getMainclass() {
        return mainClass;
    }

    public String[] getArgs() {
        return args;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getPeriod() {
        return period;
    }
}
