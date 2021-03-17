package runenvironment;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

public class TaskManager implements Runnable {
    private final String classpath;
    private final String mainClass;
    private final String[] args;
    private String exc = "OK";


    public TaskManager(String cp, String mC, String[] args) {
        this.classpath=cp;
        this.mainClass=mC;
        this.args=args;
    }

    public String statusOfRunnable(){//статус, если что-то пошло не так во время рантайма
        return exc;
    }

    private ClassLoader newLoader(String dir) throws Exception {
        var path = Path.of(dir);
        if (!Files.isDirectory(path))
            throw new RuntimeException();
        return new URLClassLoader(new URL[]{path.toUri().toURL()});
    }
    @Override
    public void run() {
        try {
            var loader = newLoader(classpath);
            var clazz =loader.loadClass(mainClass);
            clazz.getMethod("main",String[].class).invoke(null, (Object) args);

        } catch (Exception exception) {
            exception.printStackTrace();
            exc = exception.getClass().getSimpleName();
            System.out.println("что-то пошло не так....");
        }
    }

}


