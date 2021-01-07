package com.nexign.corona;

import lombok.SneakyThrows;
import java.lang.reflect.Field;

/**
 * Конфигуратор отвечающий за аннотацию @InjectByType, который создаёт объект с типом аннотированного филда и сеттит его в данный филд.
 */
public class InjectByTypeAnnotationObjectConfiguratorImpl implements ObjectConfigurator {

    @Override
    @SneakyThrows
    public void configure(Object t, ApplicationContext context) {
        /* если филд класса аннотирован @InjectByType, то его нужно просеттить объектом, который подходит под тип этого филда
         */
        for (Field field : t.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(InjectByType.class)) {
                field.setAccessible(true); // делаем филд доступным для записи
                Object object = context.getObject(field.getType()); // ObjectFactory.getInstance().createObject(field.getType()); // создаём и конфигурим объект с помощью фабрики
                field.set(t, object); // сеттим в филд полученный объект
            }
        }
    }
}
