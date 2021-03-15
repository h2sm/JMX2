package beans;
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
            if ("main/Main".equals(className)) {
                ClassPool pool = ClassPool.getDefault();
                CtClass clazz = pool.get("main.Main");

                CtField intFiled = new CtField(CtClass.intType, "x", clazz);
                intFiled.setModifiers(Modifier.STATIC | Modifier.PRIVATE);
                clazz.addField(intFiled, "99");

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
                clazz.getDeclaredMethod("main").insertAfter("newMethod();");

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

