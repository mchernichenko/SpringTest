package com.nexign.quoters;

import lombok.Setter;

import javax.annotation.PostConstruct;

@Profiling // профилирование - в данном случае хотим писать в лог сколько метод работает
@DeprecatedClass(newImpl = T1000.class) // аннотация подменяет клас на новый при загрузке контекста
public class TerminatorQuoter implements Quoter {

    /* для того чтобы настраивать всё через xml обязательно делать сеттеры, т.к. с точки зрения xml-го спринга это просто не проперти
     т.к. spring через reflection будет пытаться сеттер вызвать и если его нет, то всё упадёт
     */
    @Setter
    private String message;

    @InjectRandomInt(min=2, max=10) // кастомная аннотация
    private int repeat;

    public TerminatorQuoter() {
        System.out.println("Phase_1");
    }

    // чтобы эта аннотация заработала, в контексте нужно указать обработчик, его писать не нужно, он стандартный
    // называется CommonAnnotationBeanPostProcessor
    @PostConstruct
    public void init() {
        System.out.println("Phase_2");
        System.out.println(repeat);
    }

    // все методы, аннотированные @PostProxy запускаются сами в тот момент, когда уже всё настроено и все Proxy уже сгенерировались и это может делать ContextListener
    @Override
  //  @PostProxy
    public void sayQuote() {
        System.out.println("Phase_3");
        for (int i = 0; i < repeat; i++) {
            System.out.println("message = " + message);
        }
    }
}
