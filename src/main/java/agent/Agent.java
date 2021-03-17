package agent;

import javax.management.ObjectName;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;

public class Agent {
    public static void premain(String agentArgs, Instrumentation instrumentation) throws Exception {
        var transformer = new MainTransformer();
        ManagementFactory.getPlatformMBeanServer().registerMBean(
                transformer, new ObjectName("jmxtest:type=Transformer"));
        instrumentation.addTransformer(new MainTransformer());//при загрузке классо нужно вызывать maintransformer
    }

}
