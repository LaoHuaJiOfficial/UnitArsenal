package arsenal;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import arsenal.content.Hud;
import arsenal.content.UnitCustomDialog;
import mindustry.mod.Mods;
import mindustry.type.Weapon;
import mindustry.type.weapons.RepairBeamWeapon;

import static arsenal.ArsenalMain.ModNameSprite;
import static mindustry.Vars.content;
import static mindustry.Vars.mods;

public class ArsenalVar {
    public static final int GRID_LEN = 12;
    public static TextureRegion weaponNoSprite, gridOutline;

    public static Seq<Mods.LoadedMod> loadedMod;
    public static Seq<Seq<Weapon>> weapons;

    public static UnitCustomDialog unitGridDialog;
    public static Hud hud;

    public static void init(){
        weaponNoSprite = Core.atlas.find(ModNameSprite("weapon-no-sprite"));
        gridOutline = Core.atlas.find(ModNameSprite("grid-outline"));

        loadedMod = mods.list().select(mod -> mod.enabled() && !mod.meta.hidden);
        weapons = new Seq<>();

        content.units().each(unit -> {
            weapons.add(unit.weapons.select(w -> !(w instanceof RepairBeamWeapon)));
        });

        unitGridDialog = new UnitCustomDialog();
        hud = new Hud();
        hud.init();
    }
}
