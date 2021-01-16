package com.nexign.quoters;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class ProfilingHandlerBeanPostProcessor implements BeanPostProcessor {

    // запоминаем оригинальные имена классов, для которых что-то хотим сделать (в нашем случае помеченных @Profiling),
    // т.к. на этапе postProcessAfterInitialization уже может прийти не оригинальный объект, а прокся. Но имя бина не меняется.
    private Map<String, Class> map = new HashMap<>();

    private ProfilingController profilingController = new ProfilingController();

    public ProfilingHandlerBeanPostProcessor() throws Exception {
        // получаем MBeanServer, в который можно регистрировать бины. Это никак не связано со Spring
        MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();

        // непосредственно регистрация бина, который требуется контролировать и даём ему имя, чтобы его можно было найти в JMX Console
        // по конвенции имя состоит из имени папки и непосредственно имени бина
        platformMBeanServer.registerMBean(profilingController, new ObjectName("profiling", "name", "controller"));
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        if (beanClass.isAnnotationPresent(Profiling.class)) {
            map.put(beanName, beanClass);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, String beanName) throws BeansException {
        Class beanClass = map.get(beanName);
        if (beanClass != null) { // если в мапе бин найден, то его нужно профилировать
            return Proxy.newProxyInstance(beanClass.getClassLoader(), beanClass.getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (profilingController.isEnabled()) {
                        System.out.println("Профилирую .....");
                        long start = System.nanoTime();
                        Object retVal = method.invoke(bean, args);
                        long finish = System.nanoTime();
                        System.out.println("Время выполнения: " + (finish-start));
                        System.out.println("Конец профилирования");
                        return retVal;
                    }
                    else  {
                        return method.invoke(bean, args);
                    }
                }
            });
        }
        return bean;
    }
}
