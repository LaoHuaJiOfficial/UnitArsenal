package arsenal.content;

import arsenal.content.grid.WeaponGridData;

public class OccupiedGridData {
    public int pos;
    public WeaponGridData weaponGridData;

    public OccupiedGridData(int pos, WeaponGridData weaponGridData){
        this.pos = pos;
        this.weaponGridData = weaponGridData;
    }
}
