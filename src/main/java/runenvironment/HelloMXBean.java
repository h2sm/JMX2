package runenvironment;

public interface HelloMXBean {
    void submit(String name, String classpath, String mainClass, int period) throws Exception;
    String cancel(String name);
    String status(String name);
    void showAllTasks();
    void stopProfiling(String name);
    void startProfiling(String name);
}
