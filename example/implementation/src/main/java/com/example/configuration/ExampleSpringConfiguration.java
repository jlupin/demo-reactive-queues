package com.example.configuration;

import com.jlupin.impl.client.delegator.balance.JLupinQueueLoadBalancerDelegatorImpl;
import com.jlupin.impl.client.util.JLupinClientUtil;
import com.jlupin.impl.client.util.queue.JLupinClientQueueUtil;
import com.jlupin.interfaces.client.delegator.JLupinDelegator;
import com.jlupin.interfaces.common.enums.PortType;
import com.jlupin.interfaces.microservice.partofjlupin.asynchronous.service.queue.JLupinQueueManagerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
@ComponentScan({
        "com.example",
        "com.jlupin.servlet.monitor.configuration"
})
public class ExampleSpringConfiguration {
    @Bean
    public JLupinDelegator getQueueJLupinDelegator() {
        final JLupinDelegator jLupinDelegator = JLupinClientUtil.generateInnerMicroserviceLoadBalancerDelegator(PortType.QUEUE);
        ((JLupinQueueLoadBalancerDelegatorImpl) jLupinDelegator).setGetStatusAnalyseAndChooseHighestFromAllEnvironment(true);
        return jLupinDelegator;
    }

    @Bean
    public JLupinQueueManagerService getJLupinQueueManagerService() {
        return JLupinClientUtil.generateRemote(getQueueJLupinDelegator(), "queueMicroservice", "jLupinQueueManagerService", JLupinQueueManagerService.class);
    }

    @Bean(name = "sampleQueueClientUtil")
    public JLupinClientQueueUtil getSampleQueueClientUtil() {
        return new JLupinClientQueueUtil("SAMPLE", getJLupinQueueManagerService());
    }

    @PostConstruct
    public void start() {
        final JLupinClientQueueUtil sampleQueueClientUtil = getSampleQueueClientUtil();
        sampleQueueClientUtil.start();
    }

    @PreDestroy
    public void stop() {
        final JLupinClientQueueUtil sampleQueueClientUtil = getSampleQueueClientUtil();
        sampleQueueClientUtil.stop();
    }
}

