package com.nexign.corona;

/*
  Класс по дизенфекции комнаты
  с нарушением принципов SOLID, в частности, он имеет от 7 до бесконечности Responsibility
  До бесконечности, потомучно, в будущем мы не знаем сколько может появиться реализаций ConsoleAnnouncer и PolismenImlp
 */
@Deprecated
public class CoronaDesinfector {

    /* Здесь идёт привязка к конкретной имплементации и если её нужно изменить, придётся вскрывать код этого класса
       и ещё прочих, которые также используют данную реализацию.
     */
    // private Announcer announcer  = new ConsoleAnnouncer();  // (!) на самом деле здесь 3-ответственнности(Responsibility): выбор имплементации, её создание и её настройка
    // private Polismen polismen = new PolismenImlp();         // (!) на самом деле тоже 3 Responsibility

    // вот так намного лучше, данный класс не знает какие обекты он получит, за это отвечает фабрика
    // фабрика смотрит тип, если он является интерфейсом, то идёт в конфиг за конкретной реализацией, на основе которой строится конкретный объект и возвращается фабрикой
    // private Announcer announcer = ObjectFactory.getInstance().createObject(Announcer.class); // получаем у фабрики объект, который реализует класс или интерфейс Announcer
    // private Polismen polismen = ObjectFactory.getInstance().createObject(Polismen.class);

    @InjectByType
    private Announcer announcer;
    @InjectByType
    private Polismen polismen;

    public void start(Room room) {

        announcer.announce("Начиаем дезинфекцию, все вон"); // сообщить всем присутствующим в комнате о начале дезинфекции и попросить всех всалить
        polismen.makePeopleLeaveRoom(); // разогнать всех кто не вышел после объявления

        desinfect(room);
        // todo сообщить всем присутствующим в комнате, что они могут вернуться обратно
        announcer.announce("Рискните зайти обратно");

    }

    private void desinfect(Room room) {                    // (!) зависимость
        System.out.println("Дезинфекция комнаты");
    }
}
