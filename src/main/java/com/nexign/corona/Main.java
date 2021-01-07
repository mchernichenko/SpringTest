package com.nexign.corona;

import java.util.HashMap;
import java.util.Map;

public class Main
{
    public static void main( String[] args )
    {
        /* Как только перешли на создание объектов с помощью аннотаций т.е. перешли на IoC, то создавать объекты через new уже нельзя.
           Т.к. если внутри объекта создаются другие связанные объекты через аннотации, то будет NPE, т.к. аннотация всего лишь маркер,
           а не команда для исполнения чего либо => фактически внутренние объекты для создания внешнего объекта созданы не будут => NPE.
         */
        //CoronaDesinfector desinfector = new CoronaDesinfector();
        // CoronaDesinfector desinfector = ObjectFactory.getInstance().createObject(CoronaDesinfector.class);

        /* Скрываем работу с фабрикой за контекстом. Это делается для того чтобы поддержать кеширование синглтонов.
           Мапа - это по сути централизованное место для создания всех объектов, которое может быть вынесено во внешнюю конфигурацию !!!
           если нужно поменять реализацию, то не нужно лезть в код. Получаем гибкость.
         */
        ApplicationContext context = Application.run("com.nexign", new HashMap<>(Map.of(Announcer.class, ConsoleAnnouncerWithAdvert.class)));
        CoronaDesinfector desinfector = context.getObject(CoronaDesinfector.class);
        desinfector.start(new Room());

    }
}
