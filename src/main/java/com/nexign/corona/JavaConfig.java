package com.nexign.corona;

import lombok.Getter;
import org.reflections.Reflections;

import java.util.Map;
import java.util.Set;

/**
 * Конфигуратор, подбирающий конкретную реализацию по переданному типу интерфейса в определённом пакете или мапе
 * Подбор подходящей реализации производится в определённом пакете или мапе
 *
 * ИТОГО: от конфига зависит какая конкретная реализация будет инстанцирована фабрикой.
 */
public class JavaConfig implements Config {

    @Getter
    private Reflections scanner;
    private Map<Class, Class> ifc2ImplClass; // для поддержки множественных реализаций
    private String path;

    /**
     *
     * @param packageToScan - имя пакета для сканирования имплементаций
     * @param ifc2ImplClass - мапа, где указывается соответствие интерфейса и реализации. Имеет приоритет.
     */
    public JavaConfig(String packageToScan, Map<Class, Class> ifc2ImplClass) {
        this.scanner = new Reflections(packageToScan); // передаём пакет, который нужно просканировать на наличие нужных классов и пр.
        this.path = packageToScan;
        this.ifc2ImplClass = ifc2ImplClass;
    }

    @Override
    public <T> Class<? extends T> getImplClass(Class<T> interface_type) {
        // если key не существует или value=null, то создаётся key с value = значение возвращенное лямбдой.
        // возвращает значение value (существующее или вычисленное), соответствующее переданному key

        // профит использования мапы ещё в том, что каждый раз не нужно сканировать. Если что-то в мапе есть, то достаём из неё. Это типа как кэш работает.
        return ifc2ImplClass.computeIfAbsent(interface_type, aClass -> {
            Set<Class<? extends T>> classes = scanner.getSubTypesOf(interface_type); // получить все классы, которые реализуют указанный на входе интерфейс
            if (classes.size() != 1) {
                throw new RuntimeException(interface_type + " имеет 0 или более одной реализации в пакете " + this.path + " Обновите конфиг.");
            }

            return classes.iterator().next();
        });
    }
}
