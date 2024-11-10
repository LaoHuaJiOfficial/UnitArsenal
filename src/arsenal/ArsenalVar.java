package arsenal;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Point2;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arsenal.content.CustomUnitDialog;
import arsenal.content.grid.UnitGridData;
import arsenal.content.ui.UnitGridDialog;
import mindustry.Vars;
import mindustry.ctype.Content;

import static arsenal.ArsenalMain.ModNameSprite;

public class ArsenalVar {
    public static final int GRID_LEN = 16;
    public static TextureRegion weaponNoSprite, gridOutline;


    //key: unit inner name, value: unit grid
    public static ObjectMap<String, UnitGridData> unitGridsMap;


    public static UnitGridDialog unitGridDialog;

    public static void init(){
        weaponNoSprite = Core.atlas.find(ModNameSprite("weapon-no-sprite"));
        gridOutline = Core.atlas.find(ModNameSprite("grid-outline"));

        unitGridsMap = new ObjectMap<>(Vars.content.units().size);
        unitGridDialog = new UnitGridDialog();

        //customUnitDialog = new CustomUnitDialog();
    }
}
