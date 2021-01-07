package com.nexign.corona;

import javax.annotation.PostConstruct;

public class PolismenImlp implements Polismen {

    @InjectByType
    private Recommendator recommendator;

    @PostConstruct
    public void init() {
        System.out.println(recommendator.getClass());
    }

    @Override
    public void makePeopleLeaveRoom() {
        System.out.println("пиф, паф, бах, бах");
    }
}
