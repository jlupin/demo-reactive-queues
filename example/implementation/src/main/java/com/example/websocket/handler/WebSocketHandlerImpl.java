package com.example.websocket.handler;

import com.jlupin.impl.client.util.queue.JLupinClientQueueUtil;
import com.jlupin.interfaces.function.JLupinQueueReactiveFunction;
import com.jlupin.interfaces.microservice.partofjlupin.asynchronous.service.queue.JLupinQueueManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Component
public class WebSocketHandlerImpl extends TextWebSocketHandler {
    private final Logger logger = LoggerFactory.getLogger(WebSocketHandlerImpl.class);

    @Autowired
    private JLupinQueueManagerService jLupinQueueManagerService;
    @Autowired
    @Qualifier("sampleQueueClientUtil")
    private JLupinClientQueueUtil sampleQueueClientUtil;

    @Override
    protected void handleTextMessage(final WebSocketSession session, final TextMessage message) throws Exception {
        final String taskId = jLupinQueueManagerService.putTaskInput(
                "SAMPLE",
                "sampleMicroservice",
                "exampleService",
                "exampleMethod",
                new Object[]{
                        new Integer(message.getPayload())
                }
        );

        sampleQueueClientUtil.registerFunctionOnTaskResult(
                taskId,
                new JLupinQueueReactiveFunction() {
                    @Override
                    public void onSuccess(final String taskId, final Object result) {
                        try {
                            session.sendMessage(new TextMessage(((Integer) result).toString()));
                        } catch (IOException e) {
                            logger.error("Error while sending message.", e);
                        }
                    }

                    @Override
                    public void onError(final String taskId, final Throwable throwable) {
                        try {
                            session.sendMessage(new TextMessage(throwable.getMessage()));
                        } catch (IOException e) {
                            logger.error("Error while sending message.", e);
                        }
                    }
                }
        );
    }
}
