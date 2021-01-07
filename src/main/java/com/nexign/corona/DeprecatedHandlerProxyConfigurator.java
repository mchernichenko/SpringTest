package com.nexign.corona;

import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/*
  Проксирование объектов помеченных как @Deprecated
 */
public class DeprecatedHandlerProxyConfigurator implements ProxyConfigurator {

    /**
     *
     * @param t - объект который нужно запроскировать
     * @param implClass
     * @return
     */
    @Override
    public Object replaceWithProxyIfNeeded(Object t, Class implClass) {
        // для каждого метода класса помеченног аннотацией @Deprecated возвращаем прокси-метод, расширяющий его логику: добавляющий надпись о том что метод deprecated
        if (implClass.isAnnotationPresent(Deprecated.class)) {
            if (implClass.getInterfaces().length == 0) {  // если проксируемый объект не имеет интерфейса, то прокси нужно создавать методом из библиотеки cglib
                return Enhancer.create(implClass, (net.sf.cglib.proxy.InvocationHandler) (proxy, method, args) -> getInvocationHandlerLogic(t, method, args));
            }
            return Proxy.newProxyInstance(implClass.getClassLoader(), implClass.getInterfaces(), (proxy, method, args) -> getInvocationHandlerLogic(t, method, args));
        } else {
            return t;
        }
    }

    private Object getInvocationHandlerLogic(Object t, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
        System.out.println("WARNING: ********* Method Deprecated ********");
        return method.invoke(t, args); // вызываем аналогичный метод уже у исходного объекта и то что он фозращает вернём и мы
    }
}
