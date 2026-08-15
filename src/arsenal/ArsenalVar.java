package arsenal;

import arc.Core;
import arc.Events;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import arsenal.content.Hud;
import arsenal.content.UnitCustomDialog;
import mindustry.entities.units.WeaponMount;
import mindustry.game.EventType.Trigger;
import mindustry.gen.Unit;
import mindustry.mod.Mods;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.weapons.RepairBeamWeapon;

import static arsenal.ArsenalMain.ModNameSprite;
import static mindustry.Vars.content;
import static mindustry.Vars.mods;
import static mindustry.Vars.player;

public class ArsenalVar {
    public static final int GRID_LEN = 12;
    public static TextureRegion weaponNoSprite, gridOutline;

    public static Seq<Mods.LoadedMod> loadedMod;
    public static Seq<Seq<Weapon>> weapons;

    public static UnitCustomDialog unitGridDialog;
    public static Hud hud;

    private static final String PREFIX = "arsenal-loadout-";
    private static Unit lastUnit;

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

        // Tự động áp dụng lại loadout đã lưu mỗi khi player nhận unit mới (respawn).
        Events.run(Trigger.update, () -> {
            Unit unit = player.unit();
            if (unit != null && unit != lastUnit){
                lastUnit = unit;
                applySavedLoadout(unit);
            }
        });
    }

    public static void saveLoadout(UnitType type, Seq<Weapon> list){
        StringBuilder sb = new StringBuilder();
        for (Weapon w : list){
            if (sb.length() > 0) sb.append(";");
            sb.append(w.name).append(":").append(w.x).append(":").append(w.y);
        }
        Core.settings.put(PREFIX + type.name, sb.toString());
    }

    public static Seq<Weapon> loadLoadout(UnitType type){
        Seq<Weapon> result = new Seq<>();
        String raw = Core.settings.getString(PREFIX + type.name, "");
        if (raw.isEmpty()) return result;

        for (String part : raw.split(";")){
            String[] f = part.split(":");
            if (f.length != 3) continue;

            Weapon base = findWeaponByName(f[0]);
            if (base == null) continue;

            Weapon w = base.copy();
            w.x = Float.parseFloat(f[1]);
            w.y = Float.parseFloat(f[2]);
            w.mirror = false;
            w.alternate = false;
            result.add(w);
        }
        return result;
    }

    private static Weapon findWeaponByName(String name){
        for (Seq<Weapon> list : weapons){
            for (Weapon w : list){
                if (w.name.equals(name)) return w;
            }
        }
        return null;
    }

    private static void applySavedLoadout(Unit unit){
        Seq<Weapon> saved = loadLoadout(unit.type());
        if (saved.isEmpty()) return;

        Seq<WeaponMount> mounts = new Seq<>();
        for (Weapon w : saved) mounts.add(new WeaponMount(w));
        unit.mounts = mounts.toArray(WeaponMount.class);
    }
}
