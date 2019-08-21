package com.example.zk.service.impl;

import javax.annotation.PostConstruct;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.zk.service.ChangePropsService;

@Service
public class ChangePropsServiceImpl implements ChangePropsService {

    volatile String val = "default";

    @Autowired
    private ZkClient zkClient;

    @PostConstruct
    public void subscribe() {
        zkClient.subscribeDataChanges("/usr" , new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                val = (String) data;
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                val = null;
            }
        });
    }

    /**
     * 修改path中的data值
     *
     * @param str
     */
    @Override
    public void change(String str) {
        zkClient.writeData("/usr", str);
    }

    /**
     * 获取成员变量val的值
     */
    @Override
    public String getVal() {
        return this.val;
    }
}
