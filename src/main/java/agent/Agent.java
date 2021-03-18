package agent;

import java.lang.instrument.Instrumentation;

public class Agent {
    public static void premain(String agentArgs, Instrumentation instrumentation) throws Exception {
        instrumentation.addTransformer(new MainTransformer(),true);//при загрузке классо нужно вызывать maintransformer
    }

}
