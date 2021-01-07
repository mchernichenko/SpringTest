package com.nexign.corona;

public class ConsoleAnnouncerWithAdvert implements Announcer {
    // private Recommendator recomendator = ObjectFactory.getInstance().createObject(Recommendator.class);

    @InjectByType
    private Recommendator recomendator;

    @Override
    public void announce(String message) {
        System.out.println(message);
        recomendator.recommend();
    }
}
