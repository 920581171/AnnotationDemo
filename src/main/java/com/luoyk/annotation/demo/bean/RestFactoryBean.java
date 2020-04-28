package com.luoyk.annotation.demo.bean;

import org.springframework.beans.factory.FactoryBean;

/**
 * 工厂Bean，生成某一个类型Bean实例
 *
 * @author 92058
 */
public class RestFactoryBean implements FactoryBean<Object> {

    //返回的对象实例
    private Object object;

    //Bean的类型
    private Class<?> type;

    @Override
    public Object getObject() {
        return this.object;
    }

    @Override
    public Class<?> getObjectType() {
        return this.type;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
}
