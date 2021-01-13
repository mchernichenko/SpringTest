package com.nexign.quoters;

import lombok.Getter;
import lombok.Setter;

/* Есть конвенция по имени интерфейса, достаточно старая поэтому и чутка странная. К имени класса нужно добавить MBean
   Далее в интерфейсе нужно описать те методы, которые будут доступны в JConcole
   Далее нужно будет зарегистрировать этот бин в MBeanServer, там где мы его создадим, а это в ProfilingHandlerBeanPostProcessor
 */

public class ProfilingController implements ProfilingControllerMBean {

    @Getter
    @Setter
    private boolean enabled = true;

}
