package org.my.springboot.qc.qcclient.config;

import lombok.extern.slf4j.Slf4j;
import org.my.springboot.qc.qcclient.service.ClientSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ServerConnectionCommandLineRunner implements CommandLineRunner {

    @Autowired
    ClientSocketService clientSocketService;

    @Override
    public void run(String... args) {
        try {
            clientSocketService.connect();
        } catch (Exception e) {
            log.error("connection initialization failed, please check", e);
        }
    }
}
