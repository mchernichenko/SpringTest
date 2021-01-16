package com.nexign.quoters;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Random;

/*( кастомная аннотация, чтобы её воспринимал Spring нужно унаследоваться от BeanPostProcessor - по сути
   это конфигуратор, через который прогоняются все созданные бины, перед тем как они попадут в контейнер
   NB: Чтобы про наш конфигуратор узнал Spring, его нужно обязательно прописать в контекст (в нашем случае через xml)
 */
public class InjectRandomIntAnnotationBeanPostProcessor implements BeanPostProcessor {

    // вызывается до init метода и всегда работает с оригинальным объектом (не с прокси).
    // В beanName всегда приходит то, что указали в контексте, например, id="terminatorQuoterId"
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            InjectRandomInt annotation = field.getAnnotation(InjectRandomInt.class); // у каждого филда пытаемся получить аннотацию @InjectRandomInt
            if (annotation != null) { // если аннотация есть, то вытаскиваем из ней значения
                int min = annotation.min();
                int max = annotation.max();
                Random random = new Random();
                int i = min + random.nextInt(max - min);
                field.setAccessible(true);
                ReflectionUtils.setField(field,bean,i); // использовать field.set (i) не можем, т.к. интерфейс BeanPostProcessor не предполагает проброс исключений, а try-catch не хотим
            }

        }
        return bean;
    }

    // вызывается после init метода и может работать как с оригинальным так и с приксированным объектом
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
