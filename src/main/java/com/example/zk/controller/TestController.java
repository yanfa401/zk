package com.example.zk.controller;


import java.util.List;

import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.zk.service.ChangePropsService;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ZkClient zkClient;

    @Autowired
    private ChangePropsService changePropsService;

    /**
     * 获取data
     * @param path
     * @return
     */
    @GetMapping("/{path}")
    public String getNode(@PathVariable("path") String path) {
        return zkClient.readData("/" + path, true);
    }

    /**
     * 获取子节点列表
     * @param path
     * @return
     */
    @GetMapping("/getChildren/{path}")
    public List<String> getChildren(@PathVariable("path") String path) {
        return zkClient.getChildren("/" + path);
    }

    @GetMapping("/getVal")
    public String getVal() {
        return changePropsService.getVal();
    }

    @GetMapping("/change/{str}")
    public void change(@PathVariable("str") String str) {
        changePropsService.change(str);
    }

}
