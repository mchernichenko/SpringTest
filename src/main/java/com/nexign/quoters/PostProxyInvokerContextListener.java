package com.nexign.quoters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Method;


public class PostProxyInvokerContextListener implements ApplicationListener<ContextRefreshedEvent> {

    /* главная фабрика спринга. Инжектить его сюда это нормально, т.к. это спринговы Listener
       а Spring инжектить Spring нормально. Вот если мы тоже самое сделали бы в TermanatorQuoter - был бы ужас.
       Как делают, чтобы из фабрики достать бин, но это не правильное использование спринга
     */

    @Autowired
    private ConfigurableListableBeanFactory factory;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        String[] names = context.getBeanDefinitionNames();
        for (String name : names) {
            // с именем бина работать нельзя, т.к. класс его реализующий может быть прокси
            // т.к.нам нужна исходная имплементация, т.к. в ней нужно искать аннотации, то за ней нужно ходить в фабрику спринга, которая изначально в мапу
            // сохранила контекст при его поднятии
            BeanDefinition beanDefinition = factory.getBeanDefinition(name);
            String originalClassName = beanDefinition.getBeanClassName(); // достаём оригинальное название класса, которое в xml прописали
            try {
                Class<?> originalClass = Class.forName(originalClassName);
                Method[] methods = originalClass.getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(PostProxy.class)) {
                        // если класс аннотирован @PostProxy, то нужно запустить метод бина, но уже у не исходной имлементации, а у прокси
                        Object bean = context.getBean(name);
                        Method currentMethod = bean.getClass().getMethod(method.getName(), method.getParameterTypes());
                        currentMethod.invoke(bean);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
