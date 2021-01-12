package com.nexign.quoters;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// важно указать, что анотация должны быть доступна в runtime, иначе она не попадёт в байткод и reclection её не увидит
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectRandomInt {
    int min();
    int max();
}
