package com.nexign.corona;

@Singleton
//@Deprecated
public class RecommendatorImpl implements Recommendator {

    @InjectProperty("wisky")   // инициализация филда из applicetion.property с именем wisky, еули не указать, то будет искаться пропекти с именем филда
    private String alcohol;

    @Override
    public void recommend() {
        System.out.println("НА ПРАВАХ РЕКЛАМЫ: Для дизенфекции выпейте " + alcohol);
    }

}
