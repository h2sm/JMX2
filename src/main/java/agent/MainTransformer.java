package agent;
import javassist.*;
import javassist.bytecode.Descriptor;
import runenvironment.TaskManager;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;


public class MainTransformer implements ClassFileTransformer {
    private final String path = "D:\\Java_laba\\TestHelloWorld\\build\\classes\\java\\main";

    @Override
    public byte[] transform(ClassLoader loader,
                            String className,
                            Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        System.out.println("classname " + className);

        try {
            if (className.equals("Main")) {//если загружаемый класс был передан через jmx
                System.out.println("ok " + className);
//                ClassPool pool = ClassPool.getDefault();
//                pool.insertClassPath(path);
//                CtClass clazz = pool.get("Main");
//
//                CtField startTiming = CtField.make("private static double timeStart = System.currentTimeMillis();", clazz);
//                clazz.addField(startTiming);
//
//                CtField stopTiming = CtField.make("private static double timeStop = 0.0;", clazz);
//                clazz.addField(stopTiming);
//
//                CtField timeElapsed = CtField.make("private static double timeElapsed = 0.0;",clazz);
//                clazz.addField(timeElapsed);
//
//                CtMethod stopStuff = CtMethod.make("private static void stopStuff(){" +
//                        "timeStop = System.currentTimeMillis();" +
//                        "timeElapsed = timeStop - timeStart;" +
//                        "System.out.println(\"[profile] task \" + \" time elapsed : \" + timeElapsed);\n" +
//                        "}",clazz);
//
//                clazz.addMethod(stopStuff);
//
//                clazz.getDeclaredMethod("main").insertAfter("stopStuff();");
//                clazz.writeFile("D:\\Java_laba");
//                clazz.detach();
//                return clazz.toBytecode();

                ClassPool classPool = ClassPool.getDefault();
                classPool.insertClassPath(path);

                CtMethod method = classPool
                        .getMethod(Descriptor.toJavaName(className),"main");
                method.insertAfter("System.out.println " +
                        "(\"...hello.\");");
                byte[] newClass = method.getDeclaringClass().toBytecode();
                method.getDeclaringClass().detach();
                return newClass;
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

