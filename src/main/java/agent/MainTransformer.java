package agent;
import javassist.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class MainTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {

        try {
            if ("runenvironment/TaskManager".equals(className)) {
                ClassPool pool = ClassPool.getDefault();
                CtClass clazz = pool.get("TaskManager/run");

                CtField startTiming = new CtField(CtClass.longType,"timeStart",clazz);//создаем long для таймера
                startTiming.setModifiers(Modifier.STATIC | Modifier.PRIVATE);
                clazz.addField(startTiming,"0.0");

                CtField stopTiming = CtField.make("private static long timeStop = 0.0;", clazz);
                clazz.addField(stopTiming);

                CtField timeElapsed = CtField.make("private static long timeElapsed = 0.0;",clazz);

                CtMethod startStuff = CtMethod.make("private static void startStuff(){" +
                        "timeStart = System.nanoTime();" +
                        "}",clazz);

                CtMethod stopStuff = CtMethod.make("private static void stopStuff(){" +
                        "timeStop = System.nanoTime();" +
                        "}",clazz);

                CtMethod countingMethod = CtMethod.make("private static void timeCount(){" +
                        "timeElapsed = (timeStop - timeStart)/1000000}" +
                        "", clazz);

//                CtMethod newMethod = CtMethod.make(""
//                        private static void newMethod() {
//                            System.out.println("x + y = " + (x + y));
//                            x += 10;
//                            y += 20;
//                            System.out.println("after update: x + y = " + (x + y));
//                        }
//                        "", clazz);

                clazz.addMethod(stopStuff);
                clazz.addMethod(countingMethod);
                clazz.getDeclaredMethod("run").insertBefore("startStuff();");//начинаем отсчет
                clazz.getDeclaredMethod("run").insertAfter("endStuff();");//заканчиваем отчет
                clazz.getDeclaredMethod("run").insertAfter("timeCount();");

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
}

