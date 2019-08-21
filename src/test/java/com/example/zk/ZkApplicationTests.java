package com.example.zk;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ZkApplicationTests {

    //zk地址
    private static final String ZK_ADDRESS = "127.0.0.1:2181";

    @Test
    public void createPersistentNode() throws InterruptedException {
        //ZkConnection默认3000ms超时
        ZkClient zkClient = new ZkClient(new ZkConnection(ZK_ADDRESS), 2000);
        zkClient.createPersistent("/usr", "usr1234");
        zkClient.close();
    }

    /**
     * 创建临时节点,当断开与zk的连接时,该节点消失
     * 可用于集群节点监控
     */
    @Test
    public void createEphemeralNode() throws InterruptedException {
        //ZkConnection默认3000ms超时
        ZkClient zkClient = new ZkClient(new ZkConnection(ZK_ADDRESS), 2000);
        zkClient.createEphemeral("/usr/admin", "123456");
        TimeUnit.SECONDS.sleep(5);
        zkClient.close();
    }

    /**
     * 递归的创建节点
     * 注意:1.貌似api中仅限永久节点可递归创建
     *     2.可以递归创建节点,但是不能递归赋值
     */
    @Test
    public void recursiveCreateNode() throws InterruptedException {
        //ZkConnection默认3000ms超时
        ZkClient zkClient = new ZkClient(new ZkConnection(ZK_ADDRESS), 2000);
        zkClient.createPersistent("/student/zhangsan/abc", true);
        zkClient.close();
    }

    /**
     * 普通删除节点和递归删除节点
     * @throws InterruptedException
     */
    @Test
    public void removeNode() throws InterruptedException {
        //ZkConnection默认3000ms超时
        ZkClient zkClient = new ZkClient(new ZkConnection(ZK_ADDRESS), 2000);
        //普通删除
        zkClient.delete("/student/zhangsan/abc");
        //递归删除
        zkClient.deleteRecursive("/student");
        zkClient.close();
    }

    /**
     * 读取节点内容
     */
    @Test
    public void readNodeData() {
        //ZkConnection默认3000ms超时
        ZkClient zkClient = new ZkClient(new ZkConnection(ZK_ADDRESS), 2000);
        //这个api是指当指定阅读的节点不存在时候,返回null
        String usr = zkClient.readData("/usr", true);
        System.out.println(usr);
        zkClient.close();
    }

    /**
     * 获取子节点列表
     */
    @Test
    public void  getChildren() {
        //ZkConnection默认3000ms超时
        ZkClient zkClient = new ZkClient(new ZkConnection(ZK_ADDRESS), 2000);
        //这个api是指当指定阅读的节点不存在时候,返回null
        List<String> children = zkClient.getChildren("/dubbo");
        children.forEach(System.out::println);
        zkClient.close();
    }


    /**
     * 更新节点
     */
    @Test
    public void writeData() {
        //ZkConnection默认3000ms超时
        ZkClient zkClient = new ZkClient(new ZkConnection(ZK_ADDRESS), 2000);
        //这个api是指当指定阅读的节点不存在时候,返回null
        zkClient.writeData("/usr", "新内容");
        zkClient.close();
    }


    /**
     * 订阅子节点变化
     */
    @Test
    public void subscribeChildChanges() throws InterruptedException {
        //ZkConnection默认3000ms超时
        ZkClient zkClient = new ZkClient(new ZkConnection(ZK_ADDRESS), 2000);

        zkClient.subscribeChildChanges("/usr", new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                System.out.println("parentPath: "+parentPath);
                System.out.println("currentChilds: "+currentChilds);
                System.out.println("=================");
            }
        });
        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
        //TODO 这个时候,我们在zktools上新增删除节点,都会感知到
    }

    /**
     * 订阅内容变化
     * @throws InterruptedException
     */
    @Test
    public void subscribeDataChanges () throws InterruptedException {
        //ZkConnection默认3000ms超时
        ZkClient zkClient = new ZkClient(new ZkConnection(ZK_ADDRESS), 2000);

        zkClient.subscribeDataChanges("/usr", new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.out.println("变更的节点为:" + dataPath + ", 变更内容为:" + data);
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println("当前节点被删除,原路径为:"+ dataPath);
            }
        });

        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
        //TODO 这个时候,我们在zktools上修改节点,都会感知到
    }


    /**
     * 订阅节点连接及状态的变化情况
     * @throws InterruptedException
     */
    @Test
    public void subscribeStateChanges () throws InterruptedException {
        //ZkConnection默认3000ms超时
        ZkClient zkClient = new ZkClient(new ZkConnection(ZK_ADDRESS), 2000);

        zkClient.subscribeStateChanges(new IZkStateListener() {
            @Override
            public void handleStateChanged(Watcher.Event.KeeperState state) throws Exception {
                System.out.println("当前节点状态改为:" + state.name());
            }

            /**
             * 当发生session expire异常进行重连时，由于原来的所有watcher和EPHEMERAL节点都已失效，可以在handleNewSession方法中进行相应的容错处理
             * @throws Exception
             */
            @Override
            public void handleNewSession() throws Exception {
                System.out.println("节点Session变化。。。");
            }

            @Override
            public void handleSessionEstablishmentError(Throwable error) throws Exception {

            }
        });

        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
        //TODO 这个时候,我们在zktools上修改节点,都会感知到
    }





}
