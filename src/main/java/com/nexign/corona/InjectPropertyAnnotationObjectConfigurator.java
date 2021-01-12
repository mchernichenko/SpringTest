package com.nexign.corona;

import javassist.util.proxy.ProxyFactory;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

/**
 * Поддержка аннотации InjectProperty, для конфигурации (настройки) объектов значениями из .properties
 * Суть конфигурации заключается в том, чтобы для переданого объекта определить филды с аннотицией @InjectProperty и
 * если они найдены, то засеттить в него значение из "application.properties" или из самой аннотации.
 */
public class InjectPropertyAnnotationObjectConfigurator implements ObjectConfigurator {

    private Map<String, String> propertiesMap;

    @SneakyThrows
    public InjectPropertyAnnotationObjectConfigurator() {
        // считываем файл .properties в мапу один раз при создании конфигурации
        String path = ClassLoader.getSystemClassLoader().getResource("application.properties").getPath();
        Stream<String> lines = new BufferedReader(new FileReader(path)).lines();
        propertiesMap = lines.map(line -> line.split("=")).collect(toMap(arr -> arr[0], arr -> arr[1]));
    }

    @Override
    @SneakyThrows
    public void configure(Object t, ApplicationContext context) {

        // аннотаций может быть очень много, и для поддержки новой нужно было бы вскрывать код JavaConfig, а это нарушение принципа SOLID (O - open close principle), поэтому вынесли сюда.
        // т.е. система должна быть открыта для расширения, но закрыта для внесения изменения в существующий код

        Class<?> implClass = t.getClass();
        for (Field field : implClass.getDeclaredFields()) {  // пробегаемся по всем филдам класса и смотрим какие из них помечены аннотицией InjectProperty для инициализации
            InjectProperty annotation = field.getAnnotation(InjectProperty.class);

            if (annotation != null) { // если аннотируемые филды есть, то берём значение, либо из аннотиции, либо из .properties
                String value;
                if (annotation.value().isEmpty()) {
                    value = propertiesMap.get(field.getName()); // берём значение по имени филда
                } else {
                    value = propertiesMap.get(annotation.value());  // берём значение по имени из аннотации
                }

                field.setAccessible(true); // прежде чем засетить филд, а но может быть private, его нужно временно сделать доступным для записи
                field.set(t, value);      // устанавливаем филд объекта "t" в значение value

            }
        }
    }
}
