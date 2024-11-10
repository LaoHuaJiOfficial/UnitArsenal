package arsenal.content.grid;

import arsenal.content.weapon.WeaponData;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Unit;

import static arsenal.GlobalVar.GRID_LEN;

public class GridPartData {
    public int width;
    public int height;
    public int startX;
    public int startY;
    public WeaponData weapon;
    public TextureRegion icon;

    public GridPartData(WeaponData weapon){
        WeaponDataGrid(weapon);
    }

    public GridPartData setStart(int x, int y){
        this.startX = x;
        this.startY = y;
        return this;
    }

    public void WeaponDataGrid(WeaponData weapon){
        this.weapon = weapon;
        this.width = calcLength(weapon.weapon.region.width);
        this.height = calcLength(weapon.weapon.region.height);
        this.icon = weapon.icon;
    }

    public int calcLength(int len){
        return (len / GRID_LEN) > 0? (len / GRID_LEN): 1;
    }

    public void apply(Unit unit){
        var region = unit.type.region;
        weapon.apply(unit,
            (region.width%GRID_LEN/2 + (startX-1)*GRID_LEN + width*GRID_LEN/2 - region.width/2)/ 4,
            (region.height%GRID_LEN/2 + (startY-1)*GRID_LEN + height*GRID_LEN/2 - region.height/2)/ 4
        );
    }
}
