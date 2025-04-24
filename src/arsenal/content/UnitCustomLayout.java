package arsenal.content;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.input.KeyCode;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.event.ElementGestureListener;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.scene.event.Touchable;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.scene.ui.layout.WidgetGroup;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.entities.part.DrawPart;
import mindustry.entities.part.RegionPart;
import mindustry.entities.units.WeaponMount;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

import static arsenal.ArsenalVar.*;
import static mindustry.Vars.*;

public class UnitCustomLayout extends WidgetGroup {
    public UnitType playerUnitType;
    public Seq<Weapon> playerWeapons;

    public float panX, panY, zoom = 1f, lastZoom = -1f;
    public Table debug;

    public UnitCustomLayout(){
        setTransform(false);
        setFillParent(true);

        playerWeapons = new Seq<>();

        debug = new Table();
        debug.setFillParent(true);
        debug.touchable(() -> Touchable.disabled);
        debug.top().right();

        debug.add(new Label(() -> zoom + "")).right().padRight(8).row();
        debug.add(new Label(() -> (int)panX + "")).right().padRight(8).row();
        debug.add(new Label(() -> (int)panY + "")).right().padRight(8).row();

        debug.pack();

        addChild(debug);

        update(() -> {
            requestKeyboard();
            requestScroll();
        });

        addListener(new ElementGestureListener() {

            @Override
            public void zoom(InputEvent event, float initialDistance, float distance) {
                if (lastZoom < 0) {
                    lastZoom = zoom;
                }

                zoom = Mathf.clamp(distance / initialDistance * lastZoom, 0.25f, 4f);
            }

            @Override
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                panX += deltaX / zoom;
                panY += deltaY / zoom;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button) {
                lastZoom = zoom;
            }

            @Override
            public void tap(InputEvent event, float x, float y, int count, KeyCode button){
                Weapon weapon = unitGridDialog.currentWeapon;
                if(weapon == null) return;
                float shiftX = mousePanX(tilesize / 2f) - unitCenterX();
                float shiftY = mousePanY(tilesize / 2f) - unitCenterY();

                Weapon toAdd = weapon.copy();
                toAdd.x = shiftX / tilesize / zoom * 2;
                toAdd.y = shiftY / tilesize / zoom * 2;
                toAdd.mirror = false;
                toAdd.alternate = false;
                playerWeapons.add(toAdd);
            }
        });

        addListener(new InputListener(){
            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY){
                zoom = Mathf.clamp(zoom - amountY / 10f * zoom, 0.25f, 4f);
                return true;
            }
        });
    }

    public void redo(){
        if (playerWeapons.isEmpty()) return;
        playerWeapons.remove(playerWeapons.size - 1);
        //Weapon last = playerWeapons.peek();
        //if (last.mirror && !playerWeapons.isEmpty()) playerWeapons.peek();
    }

    public void reset(){
        panX = panY = 0;
        zoom = 1f;
    }

    public void apply(){
        Seq<WeaponMount> mounts = new Seq<>();
        for (Weapon weapon: playerWeapons) {
            mounts.add(new WeaponMount(weapon));
        }
        player.unit().mounts = mounts.toArray(WeaponMount.class);
    }

    public boolean shouldMirror(){
        if (unitGridDialog.currentWeapon == null) return false;
        return unitGridDialog.forceMirror && unitGridDialog.currentWeapon.mirror;
    }

    @Override
    public void draw(){
        validate();
        Draw.color(Pal.accent);
        Lines.stroke(2);
        Lines.rect(x, y, getWidth(), getHeight());

        if (playerUnitType != null){
            Draw.color();
            Draw.alpha(0.3f);


            TextureRegion r = playerUnitType.fullIcon;
            Draw.rect(r, unitCenterX(), unitCenterY(), scl(r.width), scl(r.height));

            Draw.alpha(1f);
            for(Weapon weapon: playerWeapons){
                TextureRegion region = Core.atlas.find(weapon.name + "-preview", Core.atlas.find(weapon.name));
                if (!weapon.mirror){
                    Draw.rect(region,
                            valuePanX(weapon.x * tilesize / 2),
                            valuePanY(weapon.y * tilesize / 2),
                            scl(region.width), scl(region.height));
                }else {
                    Draw.rect(region,
                            valuePanX(-weapon.x * tilesize / 2),
                            valuePanY(weapon.y * tilesize / 2),
                            scl(-region.width), scl(region.height));
                }
            }

            if (unitGridDialog.currentWeapon != null){
                Weapon hold = unitGridDialog.currentWeapon;
                TextureRegion region = Core.atlas.find(hold.name + "-preview", Core.atlas.find(hold.name));
                float shift = mousePanX(tilesize / 2f) - unitCenterX();
                Draw.rect(region,
                        unitCenterX() + shift,
                        mousePanY(tilesize / 2f),
                        scl(region.width), scl(region.height));
                if (shouldMirror()) Draw.rect(region,
                        unitCenterX() - shift,
                        mousePanY(tilesize / 2f),
                        scl(-region.width), scl(region.height));
            }
        }

        Draw.reset();
        super.draw();
        Draw.reset();
    }

    private float scl(float len){
        return len * zoom;
    }

    private float centerX(){
        return x + getWidth()/2f;
    }

    private float centerY(){
        return y + getHeight()/2f;
    }

    private float valuePanX(float value){
        return centerX() + (value + panX) * zoom;
    }

    private float valuePanY(float value){
        return centerY() + (value + panY) * zoom;
    }

    private float mousePanX(float value){
        mousePos(Tmp.v1);
        return Tmp.v1.x + value * zoom;
    }

    private float mousePanY(float value){
        mousePos(Tmp.v2);
        return Tmp.v2.y + value * zoom;
    }

    private void mousePos(Vec2 out){
        Vec2 pos = screenToLocalCoordinates(Core.input.mouse());
        out.set(pos.x + x, pos.y + y);
    }

    private float unitCenterX(){
        return centerX() + panX * zoom;
    }

    private float unitCenterY(){
        return centerY() + panY * zoom;
    }
}
