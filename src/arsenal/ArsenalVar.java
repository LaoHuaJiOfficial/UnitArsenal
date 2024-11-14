package arsenal;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arsenal.content.grid.UnitGridData;
import arsenal.content.grid.WeaponGridData;
import arsenal.content.ui.Hud;
import arsenal.content.ui.UnitGridDialog;
import mindustry.Vars;
import mindustry.mod.Mods;
import mindustry.type.UnitType;

import static arsenal.ArsenalMain.ModNameSprite;
import static mindustry.Vars.mods;

public class ArsenalVar {
    public static final int GRID_LEN = 12;
    public static TextureRegion weaponNoSprite, gridOutline;


    //key: unit inner name, value: unit grid
    public static ObjectMap<String, UnitGridData> unitGridsMap;
    //key: weapon inner name, value: weapon grid
    public static ObjectMap<String, WeaponGridData> weaponGridsMap;
    public static ObjectMap<UnitType, Seq<WeaponGridData>> unitWeaponGridMap;

    public static Seq<Mods.LoadedMod> loadedMod;

    public static UnitGridDialog unitGridDialog;
    public static Hud hud;

    public static void init(){
        weaponNoSprite = Core.atlas.find(ModNameSprite("weapon-no-sprite"));
        gridOutline = Core.atlas.find(ModNameSprite("grid-outline"));

        loadedMod = mods.list().select(mod -> mod.enabled() && !mod.meta.hidden);

        unitGridsMap = new ObjectMap<>(Vars.content.units().size);
        weaponGridsMap = new ObjectMap<>();
        unitWeaponGridMap = new ObjectMap<>();

        unitGridDialog = new UnitGridDialog();
        hud = new Hud();
        hud.init();

        //customUnitDialog = new CustomUnitDialog();
    }
}
