package agent;
import javassist.*;
import javassist.bytecode.Descriptor;
import runenvironment.TaskManager;

import java.io.ByteArrayInputStream;
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
        System.out.println("classname " + className);

        try {
            if (className.equals("Main")) {//если загружаемый класс был передан через jmx
                ClassPool classPool = ClassPool.getDefault();
                classPool.appendClassPath(new LoaderClassPath(loader));
                CtClass cc = classPool.get(className);
                CtMethod[] methods = cc.getMethods();

                for( CtMethod method : methods){
                    if (method.getName().equals("main")){
                        System.out.println("Entering "+method.getName() + " of " + className);
                        cc.defrost();
                        method.addLocalVariable("elapsedTime", CtClass.longType);
                        method.insertBefore("elapsedTime = System.currentTimeMillis();");
                        method.insertAfter("{elapsedTime = System.currentTimeMillis() - elapsedTime;"
                                + "System.out.println(\"Method Executed in ms: \" + elapsedTime);}");
                    }
                }
                return cc.toBytecode();

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

