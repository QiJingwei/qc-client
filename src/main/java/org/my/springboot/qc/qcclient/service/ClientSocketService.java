package org.my.springboot.qc.qcclient.service;

import lombok.extern.slf4j.Slf4j;
import org.my.springboot.qc.qcclient.config.ClientProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class ClientSocketService {
    private static final String RECORD_URL = "/app/v1/record";
    private static final String RECORD_ACK_URL = "/app/v1/recordack";
    private String WEBSOCKETURL;

    private volatile StompSession session;
    private volatile WebSocketStompClient stompClient;

    @Autowired
    private ClientProperties properties;

    @Autowired
    public void setUrl (ClientProperties properties) {
        WEBSOCKETURL = properties.getWsHost() + properties.getVersion() + properties.getWebsocket();
    }


    public boolean isConnected() {
        return session != null && session.isConnected();
    }

    public void connect() {
        if (isConnected()){
            log.info("websocket session has already connected");
        } else {
            doConnect();
        }
    }

    private void doConnect() {
        log.info("doConnect start");

        if (isConnected()) {
            log.info("already connected");
            return;
        }

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.setDefaultMaxTextMessageBufferSize(properties.getSocketMessageBufferSize());

        StompSessionHandler handler = new AbstractConnectSessionHandler() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                super.afterConnected(session, connectedHeaders);

                log.info("session connected [{}]", session.getSessionId());
            }
        };


        if (stompClient == null) {
            log.info("initializing stomp client");

            stompClient = new WebSocketStompClient(new StandardWebSocketClient(container));
        } else {
            log.info("stomp client already initialized, try start it");
            stompClient.start();
        }


        ListenableFuture<StompSession> future = stompClient.connect(WEBSOCKETURL, handler);

        try {
            session = future.get();
            session.setAutoReceipt(true);

            log.info("session connected");

        } catch (InterruptedException | ExecutionException e) {
            log.error("doConnect WebSocket connect failed.", e);
        }

    }


    private abstract class AbstractConnectSessionHandler extends StompSessionHandlerAdapter {

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            super.handleFrame(headers, payload);

            log.error("connect stomp error frame: " + headers.toString() + payload);
            if (session != null) {
                session.disconnect();
            }
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            super.handleException(session, command, headers, payload, exception);

            log.error("Handler connect exception");
            if (session != null) {
                session.disconnect();
            }

        }
    }


}
