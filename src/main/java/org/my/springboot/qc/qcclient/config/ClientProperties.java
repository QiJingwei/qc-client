package org.my.springboot.qc.qcclient.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "qc.client")
public class ClientProperties {
    private int socketMessageBufferSize = 16 * 1024 * 1024;
    
    
    private String wsHost;
    
    private String version;

    private String websocket = "/qc-websocket";
}
