package com.nexign.corona;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Класс отвечает за кеширование синглтонов.
 * Через контекст также будут создаваться объекты, корорых нет в кеше, но создание будет делегироваться фабрике, так, что всё норм.
 * Какая конкретная реализация будет инстанцирована зависит от config`a переданного в контекст
 * Прим.: реализовывать кеширование синглтонов в самой фабрике ниразу не Single Responsibility, поэтому здесь
 */
public class ApplicationContext {
    @Setter
    private ObjectFactory factory; // контекст обязан зависеть от фабрики, т.к. контекст создаёт объекты, когда они запрашиваются
    private Map<Class, Object> cache = new ConcurrentHashMap<>(); // кеш синглтонов, где key - это интерфейс, value - реализация данного интерфейса
    @Getter
    private Config config; // от конфига зависит какая конкретная реализация будет инстанцирована фабрикой

    public ApplicationContext(Config config) {
        this.config = config;
    }

    /** Получение объекта из кэша, если нет, то запрашиваем создание объекта у фаборики и кладём его в кэш, если созданный объект singleton
     *  Поэтому он и называется get, а не create, т.к. не факт, что объект будет создан, а не достан готовый из кэша
     *
     * @param type
     * @param <T>
     * @return
     */
    public <T> T getObject(Class<T> type) {

        Class<? extends T> implClass = type;

        // проверяем, есть ли запрашиваемый объект в кэше, и если есть возращаем его
        if (cache.containsKey(type)) {
            return (T) cache.get(type);
        }

        // достаём имплементацию по переданному интерфейсу
        if (implClass.isInterface()) { // т.к. нам нужна конкретная реализация, то проверяем, что если переданный тип является интерфейсом, то
            implClass = config.getImplClass(type);  // ищем класс, реализующий указанный интерфейс, либо в мапе, либо в указанной пакете
        }

        T t = factory.createObject(implClass); // создать объект найденной имплементации

        // проверяем, нужно ли созданный объект положить в кеш, а в кэш кладёт только объекты, помеченные аннотацией @Singleton
        if (implClass.isAnnotationPresent(Singleton.class)) {
            cache.put(type, t);
        }
        return t;
    }

}
