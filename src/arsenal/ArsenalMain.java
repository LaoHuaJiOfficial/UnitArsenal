package arsenal;

import arc.*;
import arc.util.Log;
import arc.util.Time;
import arsenal.utils.GridUtil;
import mindustry.content.UnitTypes;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.type.UnitType;

import static arsenal.ArsenalVar.unitGridsMap;
import static mindustry.Vars.content;

public class ArsenalMain extends Mod{

    public static String ModNameSprite(String name){
        return "unit-arsenal-" + name;
    }

    public ArsenalMain(){

        Events.on(ClientLoadEvent.class, e -> {

            Time.runTask(10f, () -> {
                ArsenalVar.init();
                for(UnitType unit: content.units()){
                    unitGridsMap.put(unit.name, GridUtil.getUnitRect(unit));
                }
                unitGridsMap.get(UnitTypes.obviate.name).debugConsoleOutput();

                ArsenalVar.unitGridDialog.show();
            });
        });
    }
}
