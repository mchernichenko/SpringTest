package com.nexign.corona;

public class CoronaDesinfector {

    public void start(Room room) {
        // todo сообщить всем присутствующим в комнате о наале дезинфекции и попросить всех всалить
        // todo разогнать всех кто не вышел после объявления
        desinfect(room);
        // todo сообщить всем присутствующим в комнате, что они могут вернуться обратно
    }

    private void desinfect(Room room) {
        System.out.println("Дезинфекция комнаты");
    }
}
