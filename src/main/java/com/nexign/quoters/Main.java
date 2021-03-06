package com.nexign.quoters;

import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.GenericGroovyApplicationContext;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        /* при поднятии контекста, сам контекст сканируется и анализируется xmlBeanDefenitionReader`ом, который считывает все декларации бинов
           и кладёт их в Map BeanDefinitions <id_бина, его декларация (из какого класса создавать, initMethod, названия пропертей и прочие подробности) >
           За создание и хранение всех объектов отвечает BeanFactory, который работает по созданной мапе BeanDefinitions и
           созданные объекты сохраняет в контейнер IoC Container.
           NB: При поднятии контекста создаются и сохраняются только Singlton`ы. Остальные бины создаются когда они нужны.
         */

        /* если провести параллель с corona, то там мы в раннер передавали имя пакета и мапу, которая затем передавалась в конфиг.
           Конфиг, по переданному типу, производил поиск подходящей реализации в указанном пакете или мапе <интерфейс, имплементация>
           В спринге используется конфиг ввиде xml, по которому строится схожая мапа BeanDefinitions, куда складируются все декларации бинов
           Далее, при создании фабрики, она понимает, какие декларации являются конфигами, т.к. классы конфигураций реализуют определённые интерфейсы,
           и создаются схожие (с corona) листы конфигураторов, через которые фабрика будет прогонять создаваемые бины.
         */
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
        PropertiesBeanDefinitionReader

        // все объекты запрашиваются уже у контекста по интерфейсу или по классу, причём все бины после создания настраиваются, т.е
        // прогоняются через BeanPostProcessor и только после попадают в контейнер и отдаются контекстом. Задействован паттерн Chain of Responsibility
        //while (true) {
          //  Thread.sleep(1000);
            // NB: бин нужно создавать по интерфейсу, т.к. имя класса может быть изменено из-за проксирования.
            // Если класс TerminatorQuoter пометить аннотацией @Profiling, то вернётся прокси на этот объект, а он, как мы знаем,
            // создаётся динамически. В итоге, запрашиваемый класс TerminatorQuoter.class может быть не найден,
            // т.к. если его проксировать, то на самом деле будет искаться класс, имя которому придумает JVM
            context.getBean(Quoter.class).sayQuote();
        //}
    }
}
