package arsenal.content.grid;

import arc.struct.IntSeq;
import mindustry.type.Weapon;

public class WeaponGridData extends GridData{
    public Weapon weapon;

    public WeaponGridData(Weapon weapon, IntSeq grids, int width, int height, float xShift, float yShift){
        super(grids, width, height, xShift, yShift);
        this.weapon = weapon;
    }

    public WeaponGridData(Weapon weapon, GridData gridData){
        super(gridData.grids, gridData.width, gridData.height, gridData.xShift, gridData.yShift);
        this.weapon = weapon;
    }
}
