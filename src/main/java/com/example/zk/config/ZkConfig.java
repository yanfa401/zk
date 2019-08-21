package com.example.zk.config;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * zk配置类
 */
@SpringBootConfiguration
public class ZkConfig {

    @Value("${spring.zk.url}")
    private String ZK_ADDRESS;

    private ZkClient zkClient;

    /**
     * 把zk作为 bean注入
     * @return
     */
    @Bean(destroyMethod = "close")
    public ZkClient getZkclient() {
        zkClient = new ZkClient(new ZkConnection(ZK_ADDRESS), 5000);
        return zkClient;
    }
}
