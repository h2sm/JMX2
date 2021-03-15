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

                CtField intFiled = new CtField(CtClass.intType, "x", clazz);
                intFiled.setModifiers(Modifier.STATIC | Modifier.PRIVATE);
                clazz.addField(intFiled, "0");

                CtField doubleField = CtField.make("private static double y = 200.0;", clazz);
                clazz.addField(doubleField);

//                CtMethod newMethod = CtMethod.make("""
//                        private static void newMethod() {
//                            System.out.println("x + y = " + (x + y));
//                            x += 10;
//                            y += 20;
//                            System.out.println("after update: x + y = " + (x + y));
//                        }
//                        """, clazz);
             //   clazz.addMethod(newMethod);
                clazz.getDeclaredMethod("run").insertAfter("newMethod();");

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

