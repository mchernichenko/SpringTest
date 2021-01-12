package com.nexign.corona;

import java.util.Map;

/*
    Класс - раннер. Он нужен потому, что контекст у нас зависит от фабрики и фабрика зависит от контекста,
    Выходит, что одно без другого не создать, т.е. с одной стороны, при создании фабрики, мы должны передать в неё контекст и при создании контекста в неё нужно передать фабрику
    Контекст в фабрику мы передаём в конструкторе, но передать фабрику в конструктор контекста нельзя. Есть только вариант создать сначала контекст, создать фабрику и уже
    затем засеттить фабрику в контекст
 */
public class Application {
    public static ApplicationContext run(String packageToScan, Map<Class, Class> ifc2ImplClass) {

        JavaConfig config = new JavaConfig(packageToScan, ifc2ImplClass);

        // контексту нужен конфиг, т.к. теперь только контекcт определяет какую реализацию нужно создать, а фабрика только создаёт нужную реализацию
        ApplicationContext context = new ApplicationContext(config);

        /* фабрике нужен контекст т.е. все объекты, например в конфигураторах, должны создаваться только через контекст
           т.к. только в контексте у нас реализовано кеширование, и если создадим напрямую, то не все сингтоны закешируются
        */
        ObjectFactory objectFactory = new ObjectFactory(context);

        // todo - init all singletons which are not lazy

        // контексту нужна фабрика, т.к. теперь только через контекст создаются объекты или получаем из кэша готовые
        context.setFactory(objectFactory);
        return context;
    }
}
