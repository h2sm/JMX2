package agent;
import javassist.*;
import runenvironment.TaskManager;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class MainTransformer implements ClassFileTransformer,MainTransformerMBean {
    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        System.out.println("classname " + className);

        try {
            if (className.equals("main/Main")) {//если загружаемый класс был передан через jmx
                ClassPool pool = ClassPool.getDefault();
                CtClass clazz = pool.get("main.Main");

                CtField startTiming = CtField.make("private static long timeStart = 0.0", clazz);
                clazz.addField(startTiming);

                CtField stopTiming = CtField.make("private static long timeStop = 0.0;", clazz);
                clazz.addField(stopTiming);

                CtField timeElapsed = CtField.make("private static long timeElapsed = 0.0;",clazz);
                clazz.addField(timeElapsed);

                CtMethod startStuff = CtMethod.make("private static void startStuff(){" +
                        "timeStart = System.nanoTime();" +
                        "}",clazz);

                CtMethod stopStuff = CtMethod.make("private static void stopStuff(){" +
                        "timeStop = System.nanoTime();" +
                        "timeElapsed = (timeStop - timeStart)/1000000;" +
                        "System.out.println(\"[profile] task \" + className + \" time elapsed : \" + timeElapsed);\n" +
                        "}",clazz);
                clazz.addMethod(startStuff);
                clazz.addMethod(stopStuff);

                clazz.getDeclaredMethod("run").insertBefore("startStuff();");
                clazz.getDeclaredMethod("run").insertAfter("stopStuff();");

                return clazz.toBytecode();
            }
            else {
                return classfileBuffer;
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public String test() {
        return "Test123";
    }

    @Override
    public void startProfiling() {

    }

    @Override
    public void stopProfiling() {

    }

}

