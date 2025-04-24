package arsenal;

import arc.Events;
import arc.util.Time;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.mod.Mod;

public class ArsenalMain extends Mod{

    public static String ModNameSprite(String name){
        return "unit-arsenal-" + name;
    }

    public ArsenalMain(){

        Events.on(ClientLoadEvent.class, e -> {
            Time.runTask(10f, ArsenalVar::init);
        });
    }
}
