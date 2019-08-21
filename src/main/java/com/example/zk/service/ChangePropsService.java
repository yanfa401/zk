package com.example.zk.service;

/**
 * 修改配置Service
 */
public interface ChangePropsService {

    /**
     * 修改path中的data值
     * @param str
     */
    void change(String str);

    /**
     * 获取成员变量val的值
     */
    String getVal();
}
