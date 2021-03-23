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
    public void testSubmit(){//для тестов
        submit("test", "D:\\Java_laba\\TestHelloWorld\\build\\classes\\java\\main", "Main", 5);
    }

    @Override
    public void submit(String name, String classpath, String mainClass, int period) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        try {
            taskManager = new TaskManager(classpath,mainClass,args);//TaskManager - исключительно для ЗАПУСКА
            future = scheduledExecutorService.scheduleAtFixedRate(taskManager, 0, period, SECONDS);//создаем новый executor
            tasks.add(new Tasks(name, future, "running", taskManager, classpath, mainClass, args,false, period));//в Tasks будет хранится ВСЯ информация о заданной таске

        } catch (Exception e) {//если появляются иные исключение
            tasks.add(new Tasks(name, future, e.getClass().getSimpleName(), taskManager,classpath, mainClass, args,false, period));//сохраняем неудачный запуск
            System.out.println("Error in initializing");
            future.cancel(true);
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
        for (Tasks task : tasks){
            if (task.getName().equals(name)){
                if (task.isProfiling()){
                    task.getScheduledFuture().cancel(true);
                    task.stopProfiling();
                    submit(task.getName(),task.getClasspath(),task.getMainclass(),task.getPeriod());
                }
            }
        }
    }

    @Override
    public void startProfiling(String name) {
        for (Tasks task : tasks) {
            if (task.getName().equals(name)){
                if (!task.isProfiling()){
                    task.getScheduledFuture().cancel(true);
                    ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
                    var profiling = new TaskManagerT(task.getClasspath(),task.getMainclass(),task.getArgs());
                    ScheduledFuture<?> sf = service.scheduleAtFixedRate(profiling,0,task.getPeriod(), SECONDS);
                    task.setProfiling();
                    task.setScheduledFuture(sf);
                }
            }
        }
    }

}
