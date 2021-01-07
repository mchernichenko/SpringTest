package com.nexign.corona;

import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Аннотация для инициализации филда определённым значением
 * Значение берётся из "application.properties" по имени фолда или указанному в аннотации.
 * Эта логика определяется конфигуратором InjectPropertyAnnotationObjectConfigurator
 */

@Retention(RUNTIME) // это важно, без этого не работает
public @interface InjectProperty {
    String value() default "";  // для проперти можно задать значение, по умолчанию пусто
}
