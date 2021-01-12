package com.nexign.corona;

/**
 * Конфигуратор для объектов которые нужно проксировать.
 * Использовать существующий ObjectConfigurator не можем, т.к. сигнатура не подходит. Данный метод возвращает не void, а объект, т.к.
 * данный конфигуратор заменяет объект на другой в отличие от простой настройки.
 *
 * Можно было бы добавить новый метод в интерфейс, но правило SOLID говорит делать тонкие интерфейсы, а не толстые, т.е. с одним методом, тогда и лямбды можно юзать, да и проще как-то
 */
public interface ProxyConfigurator {
    /* implClass здесь нужен не просто так. Кажется, что класс можно получить из передаваемого объекта "t", но ориентироваться на вызов метода
       t.getClass нельзя, т.к. при прохождении по цепочке прокси-конфигураторов, каждый из прокси-конфигураторов может вернуть прокси.
        Чтобы иметь возможность обернуть объект в прокси несколько раз и получить прокси в прокси нужно передавать исходный класс, т.к. только у него аннотация @Deprecated,
        а у прокси объекта уже нет никакой аннотации => его уже в прокси никак нельзя будет завернуть.
    */
    Object replaceWithProxyIfNeeded(Object t, Class implClass);
}
