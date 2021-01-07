package com.nexign.corona;

public class ConsoleAnnouncer implements Announcer {
    @Override
    public void announce(String message) {
        System.out.println(message + ", бляха муха!");
    }
}
