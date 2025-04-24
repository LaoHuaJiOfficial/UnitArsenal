package arsenal.content;

import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.Button;
import arc.scene.ui.ButtonGroup;
import arc.scene.ui.Dialog;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Scaling;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.core.Version;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Iconc;
import mindustry.graphics.Pal;
import mindustry.mod.Mods;
import mindustry.type.Category;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.weapons.RepairBeamWeapon;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.world.Block;

import static arsenal.ArsenalVar.*;
import static mindustry.Vars.content;
import static mindustry.Vars.player;

public class UnitCustomDialog extends BaseDialog {
    public UnitCustomLayout unitGrid;

    public UnitType currentUnitType;
    public Weapon currentWeapon;
    public boolean forceMirror;

    public Table fullTable, unitSelection, weaponSelection, settingTable, gridEditTable, selectionTable;
    public ButtonGroup<Button> modSelected, unitSelected, weaponSelected;

    public UnitCustomDialog() {
        super("Arsenal Panel");
        clear();
        margin(0f);

        fullTable = new Table();

        settingTable = new Table();
        gridEditTable = new Table();
        selectionTable = new Table();

        modSelected = new ButtonGroup<>();
        modSelected.setMinCheckCount(0);
        modSelected.setMaxCheckCount(1);

        unitSelected = new ButtonGroup<>();
        unitSelected.setMinCheckCount(0);
        unitSelected.setMaxCheckCount(1);

        weaponSelected = new ButtonGroup<>();
        weaponSelected.setMinCheckCount(0);
        weaponSelected.setMaxCheckCount(1);

        unitGrid = new UnitCustomLayout();

        unitSelection = new Table();
        weaponSelection = new Table();

        settingTable.background(Styles.black);
        settingTable.table(cont -> {
            cont.label(() -> "SETTINGS").row();

            Button exit = new Button();
            exit.table(b -> b.label(() -> Iconc.exit + " Exit")).size(196f, 32f).pad(2);
            exit.clicked(this::hide);

            Button mirror = new Button();
            mirror.table(b -> b.label(() -> Iconc.flipX + " Mirror")).size(196f, 32f).pad(2);
            mirror.clicked(() -> forceMirror = !forceMirror);
            mirror.setChecked(forceMirror);

            Button redo = new Button();
            redo.table(b -> b.label(() -> Iconc.redo + " Redo")).size(196f, 32f).pad(2);
            redo.clicked(unitGrid::redo);

            Button reset = new Button();
            reset.table(b -> b.label(() -> Iconc.refresh + " Reset")).size(196f, 32f).pad(2);
            reset.clicked(unitGrid::reset);

            Button apply = new Button();
            apply.table(b -> b.label(() -> Iconc.units + " Apply")).size(196f, 32f).pad(2);
            apply.clicked(unitGrid::apply);

            cont.add(exit).pad(4).row();
            cont.add(mirror).pad(4).row();
            cont.add(redo).pad(4).row();
            cont.add(reset).pad(4).row();
            cont.add(apply).pad(4).row();

        }).pad(0, 4, 0, 4).margin(4);

        gridEditTable.table(cont -> {
            cont.setFillParent(true);
            cont.add(unitGrid).expand().fill();
        }).expand().fill();

        selectionTable.background(Styles.black);
        selectionTable.table(cont -> {
            //modded content selection.
            cont.pane(p -> {
                //load vanilla
                Button button = new Button();
                modSelected.add(button);
                button.setStyle(Styles.squareTogglei);
                button.margin(8);
                button.table(t -> {
                    t.image(Blocks.duo.uiIcon).size(32, 32);
                    Label label = new Label(() -> Vars.appName + " : " + Version.build);
                    label.setSize(280, 0);
                    t.add(label).left().maxWidth(280).expandX().fillX().pad(0, 12, 0, 12);
                }).expandX().fillX();
                button.clicked(() -> rebuildUnitSelection(null, false));

                p.add(button).expandX().fillX().row();

                //load modded content
                for (Mods.LoadedMod mod: loadedMod){
                    Button modButton = new Button();
                    modSelected.add(modButton);

                    modButton.setStyle(Styles.squareTogglei);
                    modButton.margin(8);
                    modButton.table(t -> {
                        t.image(modFirstIcon(mod)).size(32, 32);
                        Label label = new Label(() -> mod.meta.internalName + " [white]: " + mod.meta.version);
                        label.setSize(280, 0);
                        t.add(label).left().maxWidth(280).expandX().fillX().pad(0, 12, 0, 12);
                    }).expandX().fillX();
                    modButton.clicked(() -> rebuildUnitSelection(mod, false));

                    p.add(modButton).expandX().fillX().row();
                }
            }).minHeight(200).maxHeight(200).row();
            cont.table(t -> t.image().color(Pal.accent).fillX().size(320, 4)).fillX().margin(4).row();
            cont.pane(p -> p.add(unitSelection).expand().fill()).minHeight(200).maxHeight(200).row();
            cont.table(t -> t.image().color(Pal.accent).fillX().size(320, 4)).fillX().margin(4).row();
            cont.pane(p -> p.add(weaponSelection).top().expand().fill()).expand().fill().row();
        }).top().expand().fill();

        fullTable.table(cont -> {
            cont.add(settingTable).expandY().fill();
            cont.add(gridEditTable).expand().fill();
            cont.add(selectionTable).expandY().fill();
        }).expand().fill();

        rebuildUnitSelection(null, true);
        rebuildWeaponSelection();

        add(fullTable).expand().fill();
    }

    @Override
    public Dialog show() {
        unitGrid.playerUnitType = player.unit().type();
        Seq<Weapon> weapons = new Seq<>();
        for (WeaponMount mount: player.unit().mounts){
            if (mount.weapon instanceof RepairBeamWeapon) continue;
            weapons.add(mount.weapon);
        }
        unitGrid.playerWeapons = weapons;
        unitGrid.reset();
        return super.show();
    }

    private void rebuildUnitSelection(Mods.LoadedMod mod, boolean all){
        unitSelection.clear();
        unitSelected.clear();
        int idx = 0;
        int column = 8;

        for (UnitType unit: content.units()){
            if (all || unit.isVanilla() && mod == null || unit.isModded() && unit.minfo.mod == mod){
                Button unitSelect = new Button();
                unitSelect.table(t -> t.image(unit.uiIcon).size(32, 32));
                unitSelect.setStyle(Styles.squareTogglei);
                unitSelect.setSize(32, 32);
                unitSelect.margin(4);
                unitSelect.clicked(() -> {
                    currentUnitType = unit;
                    rebuildWeaponSelection();
                });

                unitSelection.add(unitSelect);
                unitSelected.add(unitSelect);

                idx++;
                if (idx % column == 0) {
                    unitSelection.row();
                }
            }
        }

        if (idx % column != 0){
            int extraBlank = column - idx % column;
            for (int i = 0; i < extraBlank; i++){
                Table blank = new Table();
                blank.table(t -> t.image().color(Color.clear).size(32, 32)).margin(4);
                unitSelection.add(blank);
            }
        }
    }

    private void rebuildWeaponSelection(){
        weaponSelection.clear();
        weaponSelected.clear();

        if (currentUnitType == null){
            weaponSelection.label(() -> "SELECT A UNIT...");
        }else {
            for (Weapon weapon: weapons.get(currentUnitType.id)){
                Button weaponButton = new Button();
                weaponButton.table(t -> {
                    t.image(weapon.region).size(64, 64).scaling(Scaling.fit);
                    t.label(() -> weapon.name + "\nMirror: " + weapon.mirror).expandX();
                }).size(320, 0).expandX().fillX();
                weaponButton.clicked(() -> currentWeapon = weapon);

                weaponSelection.add(weaponButton).row();
                weaponSelected.add(weaponButton);
            }
        }
    }

    private TextureRegion modFirstIcon(Mods.LoadedMod mod){
        for (Block block: content.blocks()){
            if (block.category == Category.turret && block.minfo.mod == mod){
                return block.uiIcon;
            }
        }
        return Blocks.duo.uiIcon;
    }
}
