package arsenal.content.grid;

import arsenal.content.weapon.WeaponData;
import arsenal.content.weapon.WeaponList;
import arc.struct.Seq;

public class GridPartList {
    public static Seq<GridPartData> GridWeaponList;

    public static void init(){
        GridWeaponList = new Seq<>();
        for (WeaponData weapon: WeaponList.weapons){
            GridWeaponList.add(new GridPartData(weapon));
        }
    }
}
