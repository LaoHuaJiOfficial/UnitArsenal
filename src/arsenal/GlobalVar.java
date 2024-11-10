package arsenal;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Point2;
import arc.struct.Seq;

import static arsenal.UnitArsenalMain.ModNameSprite;

public class GlobalVar {
    public static final int GRID_LEN = 16;

    public static Seq<Seq<Point2>> unitGrids;
    public static TextureRegion weaponNoSprite, gridOutline;

    public static void init(){
        unitGrids = new Seq<>();

        weaponNoSprite = Core.atlas.find(ModNameSprite("weapon-no-sprite"));
        gridOutline = Core.atlas.find(ModNameSprite("grid-outline"));
    }
}
