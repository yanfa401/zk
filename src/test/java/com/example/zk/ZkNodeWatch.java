package com.example.zk;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ZkNodeWatch {

    //zk地址
    private static final String ZK_ADDRESS = "127.0.0.1:2181";

    int appsCount = 0;

    Set<String> registedNodeKeySet = new HashSet<>();

    @Test
    public void mainTest() throws InterruptedException {
        ZkClient zkClient = new ZkClient(new ZkConnection(ZK_ADDRESS), 2000);
        zkClient.subscribeChildChanges("/chgcApps", new IZkChildListener() {

            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                if (!CollectionUtils.isEmpty(currentChilds) && currentChilds.size() > appsCount) {
                    appsCount = currentChilds.size();
                    System.out.println("新增了节点,当前上线机器数量:"+appsCount);
                }
                else if (currentChilds.size() < appsCount) {
                    appsCount = currentChilds.size();
                    System.out.println("移除了节点,当前上线机器数量:"+appsCount);
                }
                if (!CollectionUtils.isEmpty(currentChilds)) {
                    for (String node : currentChilds) {
                        if (!registedNodeKeySet.contains("/chgcApps/" + node)) {
                            registedNodeKeySet.add("/chgcApps/" + node);
                            zkClient.subscribeDataChanges("/chgcApps/" + node, new IZkDataListener() {
                                @Override
                                public void handleDataChange(String dataPath, Object data) throws Exception {

                                }

                                @Override
                                public void handleDataDeleted(String dataPath) throws Exception {
                                    System.out.println("当前机器下线:"+ dataPath);
                                    registedNodeKeySet.remove(dataPath);
                                }
                            });
                        }
                    }
                }
                System.out.println("已注册池中包含key:" + registedNodeKeySet);
            }
        });

        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }


    /**
     * 模拟上线
     */
    @Test
    public void up1() throws InterruptedException {
        ZkClient zkClient = new ZkClient(new ZkConnection(ZK_ADDRESS), 2000);
        zkClient.createEphemeral("/chgcApps/127.0.0.1");
        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }

    /**
     * 模拟上线
     */
    @Test
    public void up2() throws InterruptedException {
        ZkClient zkClient = new ZkClient(new ZkConnection(ZK_ADDRESS), 2000);
        zkClient.createEphemeral("/chgcApps/127.0.0.2");
        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }

    /**
     * 模拟上线
     */
    @Test
    public void up3() throws InterruptedException {
        ZkClient zkClient = new ZkClient(new ZkConnection(ZK_ADDRESS), 2000);
        zkClient.createEphemeral("/chgcApps/127.0.0.3");
        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }
}
