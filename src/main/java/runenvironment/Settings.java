package runenvironment;


import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import static java.util.concurrent.TimeUnit.SECONDS;

public class Settings implements HelloMXBean {
    private final ArrayList<Tasks> tasks = new ArrayList<>();
    private final String[] args;
    private TaskManager taskManager;
    private ScheduledFuture<?> future;

    public Settings(String[] args) {
        this.args = args;
    }

    @Override
    public void submit(String name, String classpath, String mainClass, int period) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        try {
            taskManager = new TaskManager(classpath,mainClass,args);//создаем новое задание
            future = scheduledExecutorService.scheduleAtFixedRate(taskManager, 0, period, SECONDS);//создаем новый executor
            tasks.add(new Tasks(name, future, "running", taskManager));//присваиваем статус

        } catch (Exception e) {//если появляются иные исключение
            tasks.add(new Tasks(name, future, e.getClass().getSimpleName(), taskManager));//сохраняем неудачный запуск
            System.out.println("Error in initializing");
        }
        if (!taskManager.statusOfRunnable().equals("OK")){//если программа не запустилась
            for (Tasks task : tasks){
                if (task.getName().equals(name)){
                    task.getScheduledFuture().cancel(true);
                    task.setStatus(task.getStatusOfRunnable());
                }
            }
        }
    }

    @Override
    public String cancel(String name) {
        for (Tasks task : tasks) {
            if (task.getName().equals(name)) {
                task.getScheduledFuture().cancel(true);//отменяем задание
                if (!task.getStatusOfRunnable().equals("OK")){//если что-то пошло не так при завершении
                    task.setStatus(task.getStatusOfRunnable());
                    return "task was cancelled with some errors";
                }
                task.setStatus("cancelled");
                return "task was cancelled";
            }
        }
        return "no such a task";
}
    @Override
    public String status(String name) {
        for (Tasks task : tasks){
            if (task.getName().equals(name)) {//если что-то пошло не так во время запуска
                return task.getStatus();
            }
        }
        return "not found";
    }

    @Override
    public void showAllTasks() {
        for (Tasks task : tasks) {
            System.out.println(task.showPreview());
        }
    }

    @Override
    public void stopProfiling(String name) {

    }

    @Override
    public void startProfiling(String name) {

    }

}
