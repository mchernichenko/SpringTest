package com.nexign.corona;

import lombok.Setter;
import lombok.SneakyThrows;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.ObjectInputFilter;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import static java.util.stream.Collectors.toMap;

/*
  Фабрика по созданию объекта произвольного типа (класса), а также его настройки.
  В данном случае значения атрибутов объекта берём из .properties. Каталог поиска класса определяется к конструкторе.
 */
public class ObjectFactory {

    /* Контекст должен зависеть от фабрики, т.к. он создайт объекты с помощью фабрики, но с другой стороны и фабрика должна знать про контекст,
       т.к. в конфигураторах могут создаваться объекты и их нужно создавать ненапрямую, а также через контекст, т.к. в контексте  логика по кешированию и
       если мы пойдём мимо неё, то не все синглтоны будут закешированы
     */
    private final ApplicationContext context;
    private List<ObjectConfigurator> configurators = new ArrayList<>();
    private List<ProxyConfigurator> proxyConfigurators = new ArrayList<>();

    @SneakyThrows
    public ObjectFactory(ApplicationContext context) {
        this.context = context;
        // инициируем список конфигураторов для создаваемых объектов, которые будут найдены в той же папке pathPack
        for (Class<? extends ObjectConfigurator> aClass : context.getConfig().getScanner().getSubTypesOf(ObjectConfigurator.class)) {
            configurators.add(aClass.getDeclaredConstructor().newInstance());
        }
        for (Class<? extends ProxyConfigurator> aClass : context.getConfig().getScanner().getSubTypesOf(ProxyConfigurator.class)) {
            proxyConfigurators.add(aClass.getDeclaredConstructor().newInstance());
        }
    }

    /** СОздание и конфигурирование объектов
     *
     * @param implClass класс-тип объекта
     * @param <T> тип возвращаемого объекта
     * @return возвращает экземпляр объекта типа <T>
     */
    @SneakyThrows
    public <T> T createObject(Class<T> implClass) {

        T t = implClass.getDeclaredConstructor().newInstance(); // предполагается, что у найденного класса есть конструктор по умолчанию и через него создаётся экземпляр

        // Вся магия по настройке объекта тут. Т.е. перед тем как отдать объект, его можно настроить согласно нашим конвенциям, которые мы придумаем.

        /* ничего страшного в том, что некоторые конфигураторы отработают вхолостую, т.е. ничего по факту не настроят,
           например, не у каждого объекта филды инициализируются с помощью аннотации InjectProperty. Это разовая, зато универсальная настройка.
         */
        configure(t); // просим каждый конфигуратор настроить наш объект

        /* филды помеченные аннотациями конфигурятся фабрикой после создания объекта и, если мы обратимся к филдам в конструкторе, то получим NPE, т.к. они ещё не проинициализированны.
           Как привило, перейдя на фабрику, нам от конструктора мало что нужно, т.к. конфигурация объекта производится фабрикой, но иногда что-то нужно.
           Приходим к концепции Second face constructor, т.е. это метод, через который нужно прогнать объект после его настройки.
           Есть уже конвенция для этого, такие методы помечаются как @PostConstruct
         */
        invokeInit(implClass, t);

        /* если нужно, то проксируем объект. В нашем случае, проксируем объекты, классы которых помечены как @Deprecated
           Для таких объектов дополнительно выводится сообщение, при каждом вызове методов такого класса
        */
        t = wrapWithProxyIfNeeded(implClass, t);

        return t;
    }

    private <T> T wrapWithProxyIfNeeded(Class<T> implClass, T t) {
        for (ProxyConfigurator proxyConfigurator : proxyConfigurators) {
            t = (T) proxyConfigurator.replaceWithProxyIfNeeded(t, implClass);
        }
        return t;
    }

    private <T> void invokeInit(Class<T> implClass, T t) throws IllegalAccessException, InvocationTargetException {
        for (Method method : implClass.getMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                method.invoke(t);
            }
        }
    }

    private <T> void configure(T t) {
        configurators.forEach(objectConfigurator -> objectConfigurator.configure(t, context));
    }
}
