package arsenal;

import arc.Events;
import arc.struct.Seq;
import arc.util.Time;
import arsenal.content.grid.WeaponGridData;
import arsenal.utils.GridUtil;
import mindustry.content.UnitTypes;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.mod.Mod;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

import static arsenal.ArsenalVar.*;
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
                    unitGridsMap.put(unit.name, GridUtil.getUnitGrid(unit));
                }

                for(UnitType unit: content.units()){
                    Seq<WeaponGridData> weaponGridDataSeq = new Seq<>();
                    for(Weapon weapon: unit.weapons){
                        WeaponGridData weaponGridData = GridUtil.getWeaponGrid(weapon);
                        weaponGridsMap.put(weapon.name, weaponGridData);
                        weaponGridDataSeq.add(weaponGridData);
                    }
                    unitWeaponGridMap.put(unit, weaponGridDataSeq);

                    unitGridsMap.put(unit.name, GridUtil.getUnitGrid(unit));
                }

                unitGridsMap.get(UnitTypes.omura.name).debugConsoleOutput();

                //ArsenalVar.unitGridDialog.show();
            });
        });
    }
}
