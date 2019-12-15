package org.my.springboot.qc.qcclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableConfigurationProperties
@EnableWebSocket
public class QcClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(QcClientApplication.class, args);
    }

}
