package com.nexign.corona;

/**
 * Интерфейс настройки абсолютно любого объекта (BeanPostProсessor в Spring)
 * На каждую аннотацию, конвенцию, доп. настройку будет свой класс, реализующий этот интерфейс
 */
public interface ObjectConfigurator {
    void configure(Object t, ApplicationContext context);
}
