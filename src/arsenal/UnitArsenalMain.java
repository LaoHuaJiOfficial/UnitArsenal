package arsenal;

import arc.*;
import arc.util.*;
import arsenal.content.AbilityList;
import arsenal.content.grid.GridPartList;
import arsenal.content.weapon.WeaponList;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.ui.dialogs.PlanetDialog;
import mindustry.ui.dialogs.ResearchDialog;

public class UnitArsenalMain extends Mod{

    public static String ModNameSprite(String name){
        return "unit-arsenal-" + name;
    }

    public UnitArsenalMain(){
        Events.on(ClientLoadEvent.class, e -> {
            PlanetDialog.debugSelect = true;
            ResearchDialog.debugShowRequirements = true;


            Time.runTask(10f, () -> {
                AbilityList.init();
                WeaponList.init();
            });
            Time.runTask(20f, GridPartList::init);
        });
    }
}
