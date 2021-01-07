package com.nexign.corona;

import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Аннотация для создания объекта с типом аннотированного филда и инициализация филда созданым объектом
 */
@Retention(RUNTIME)
public @interface InjectByType {
}
