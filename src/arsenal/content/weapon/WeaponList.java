package arsenal.content.weapon;

import arc.struct.Seq;
import mindustry.type.UnitType;
import mindustry.type.weapons.RepairBeamWeapon;

import static mindustry.Vars.content;

public class WeaponList {
    public static Seq<WeaponData> weapons;

    public static void init(){
        weapons = new Seq<>();

        Seq<WeaponData> weaponTemp = new Seq<>();
        for(int i = 0; i < content.units().size; i++){
            UnitType unit = content.unit(i);
            for (int j = 0; j < unit.weapons.size; j++){
                var weapon = unit.weapons.get(j);
                if (weapon instanceof RepairBeamWeapon) continue;
                weaponTemp.add(new WeaponData(weapon, unit, j, i));
            }
        }

        for (int i = 0; i < weaponTemp.size; i++){
            WeaponData weapon1 = weaponTemp.get(i);
            boolean isUnique = true;
            for (int j = 0; j < weapons.size; j++){
                WeaponData weapon2 = weapons.get(j);
                if (weapon1.name.equals(weapon2.name)) {
                    isUnique = false;
                    break;
                }
            }
            if (isUnique) {
                weapons.add(weapon1);
            }
        }
    }
}
