package arsenal.content.ui;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.input.KeyCode;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.scene.event.ElementGestureListener;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.scene.event.Touchable;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.scene.ui.layout.WidgetGroup;
import arc.struct.ObjectMap;
import arc.util.Log;
import arc.util.Tmp;
import arsenal.ArsenalVar;
import arsenal.content.grid.UnitGridData;
import arsenal.content.grid.WeaponGridData;
import arsenal.utils.GridUtil;
import mindustry.entities.units.WeaponMount;
import mindustry.graphics.Pal;
import mindustry.type.Weapon;

import java.util.concurrent.atomic.AtomicBoolean;

import static arsenal.ArsenalVar.GRID_LEN;
import static mindustry.Vars.*;

public class UnitGridLayout extends WidgetGroup {
    private static final float GRID_UI_SIZE = 16f;
    private static final float GRID_LINE_STROKE = 4f;
    private final float baseSize = Scl.scl(5f);

    public UnitGridData currentUnit;
    public ObjectMap<Integer, WeaponGridData> occupyWeapon;

    public int mouseX, mouseY;
    float panX, panY, zoom = 1f, lastZoom = -1f;
    //public Element grid;
    public Table debug;

    public UnitGridLayout(){
        setTransform(false);
        setFillParent(true);

        //setSize(getPrefWidth(), getPrefHeight());

        occupyWeapon = new ObjectMap<>();

        debug = new Table();
        debug.setFillParent(true);
        debug.touchable(() -> Touchable.disabled);
        debug.top().right();

        debug.add(new Label(() -> zoom + "")).right().padRight(8).row();
        debug.add(new Label(() -> (int)panX + "")).right().padRight(8).row();
        debug.add(new Label(() -> (int)panY + "")).right().padRight(8).row();
        debug.add(new Label(() -> mouseX + ":" + mouseY)).right().padRight(8).row();

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
                WeaponGridData weapon = ArsenalVar.unitGridDialog.currentSelectedWeapon;
                if(weapon == null) return;


                //size for a grid
                float len = GRID_LEN * defaultScale() * zoom;

                //left bottom corner
                float xStart = centerX() - (currentUnit.width / 2f) * len + panX * zoom;
                float yStart = centerY() - (currentUnit.height / 2f) * len + panY * zoom;

                //distance from mouse to start
                float xShift = getOX() + x - xStart;
                float yShift = getOY() + y - yStart;

                //big grid
                int xGrid = Mathf.floor(xShift / len);
                int yGrid = Mathf.floor(yShift / len);

                //small grid
                int xSmallGrid = Mathf.floor(xShift / (len / 2f));
                int ySmallGrid = Mathf.floor(yShift / (len / 2f));

                if (canAdd(xGrid, yGrid, xSmallGrid, ySmallGrid)){
                    int weaponStartX, weaponStartY;

                    if (weapon.width % 2 != 0){
                        weaponStartX = xGrid - (weapon.width/2);
                    }else {
                        int gx = xSmallGrid - weapon.width + 1;
                        if (gx <= 0) gx--;
                        weaponStartX = gx / 2;
                    }
                    if (weapon.height % 2 != 0){
                        weaponStartY = yGrid - (weapon.height/2);
                    }else {
                        int gy = ySmallGrid - weapon.height + 1;
                        if (gy <= 0) gy--;
                        weaponStartY = gy / 2;
                    }

                    Log.info(weaponStartX + ":" + weaponStartY);

                    occupyWeapon.put((weaponStartY * currentUnit.width + weaponStartX), weapon);
                }

                // In mobile, placing the query is done in a separate button.
                //if(!mobile) placeQuery();
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

    public void reset(){
        panX = panY = 0;
        zoom = 1f;

        occupyWeapon.clear();
    }

    public void apply(){
        player.unit().mounts = new WeaponMount[]{};

        for(var key: occupyWeapon.keys()){
            WeaponGridData value = occupyWeapon.get(key);
            int ox = key % currentUnit.width;
            int oy = key / currentUnit.width;

            UnitGridData unit = currentUnit;

            float blX = - (float) (unit.width * GRID_LEN) / tilesize / 2;
            float blY = - (float) (unit.height * GRID_LEN) / tilesize / 2;

            float weaponX = blX + (ox * GRID_LEN + (value.width * GRID_LEN / 2f)) / tilesize;
            float weaponY = blY + (oy * GRID_LEN + (value.height * GRID_LEN / 2f)) / tilesize;

            Weapon newWeapon = value.weapon.copy();
            newWeapon.x = weaponX * 2;
            newWeapon.y = weaponY * 2;
            newWeapon.alternate = false;
            newWeapon.mirror = false;

            WeaponMount weaponMount = new WeaponMount(newWeapon);

            GridUtil.AddUnitWeapons(player.unit(), weaponMount);
        }
    }

    private boolean canAdd(int xGrid, int yGrid, int xSmallGrid, int ySmallGrid){
        WeaponGridData grid = ArsenalVar.unitGridDialog.currentSelectedWeapon;

        int weaponStartX, weaponStartY;

        if (grid.width % 2 != 0){
            weaponStartX = xGrid - (grid.width/2);
        }else {
            int gx = xSmallGrid - grid.width + 1;
            if (gx <= 0) gx--;
            weaponStartX = gx / 2;
        }
        if (grid.height % 2 != 0){
            weaponStartY = yGrid - (grid.height/2);
        }else {
            int gy = ySmallGrid - grid.height + 1;
            if (gy <= 0) gy--;
            weaponStartY = gy / 2;
        }

        for (int x = 0; x < grid.width; x++){
            for (int y = 0; y < grid.height; y++){
                int ux = weaponStartX + x;
                int uy = weaponStartY + y;
                if (grid.getGridBottomLeft(x, y) != 0){
                    //check empty unit grid, false if empty
                    if (currentUnit.getGridBottomLeft(ux, uy) == 0){
                        return false;
                    }
                    //check other attached grids. false if occupied
                    AtomicBoolean occupied = new AtomicBoolean(false);
                    for(var key: occupyWeapon.keys()){
                        WeaponGridData value = occupyWeapon.get(key);
                        int ox = key % currentUnit.width;
                        int oy = key / currentUnit.width;

                        int sx = ux - ox;
                        int sy = uy - oy;
                        if (value.getGridBottomLeft(sx, sy) != 0){
                            occupied.set(true);
                        }
                    }
                    if (occupied.get()){
                        return false;
                    }
                }
            }
        }


        return true;
    }

    protected Rect getBoundRect(){
        return Tmp.r1.set(x - GRID_LINE_STROKE/2, y - GRID_LINE_STROKE/2, getWidth(), getHeight());
    }

    protected Rect getPanRect(){
        float
            w = Core.graphics.getWidth(),
            h = Core.graphics.getHeight(),
            size = baseSize * zoom * world.width();

        return Tmp.r2.set(w/2f + panX * zoom - size/2f, h/2f + panY * zoom - size/2f, size, size);
    }

    @Override
    public void draw(){
        validate();

        Lines.stroke(GRID_LINE_STROKE);
        Draw.color(Pal.remove);
        Lines.rect(getBoundRect());

        Draw.color();
        if (currentUnit != null){
            Draw.alpha(0.3f);
            TextureRegion r = currentUnit.unit.fullIcon;
            Draw.rect(r,
                centerX() + panX * zoom,
                centerY() + panY * zoom,
                r.width * defaultScale() * zoom,
                r.height * defaultScale() * zoom
            );
        }

        drawGrid();
        drawSelect();

        for(var key: occupyWeapon.keys()){
            WeaponGridData value = occupyWeapon.get(key);
            int ox = key % currentUnit.width;
            int oy = key / currentUnit.width;
            drawWeapon(ox, oy, value);
        }



        Draw.reset();
        super.draw();
        Draw.reset();
    }

    private void drawGrid(){
        if (currentUnit == null) return;

        float len = GRID_LEN * defaultScale() * zoom;

        float xStart = centerX() - ((currentUnit.width - 1) / 2f) * len + panX * zoom;
        float yStart = centerY() - ((currentUnit.height - 1) / 2f) * len + panY * zoom;

        for (int x = 0; x < currentUnit.width; x++){
            for (int y = 0; y < currentUnit.height; y++){
                float drawX = xStart + x * len;
                float drawY = yStart + (currentUnit.height - 1 - y) * len;
                if (currentUnit.grids.get(y * currentUnit.width + x) == 1){
                    Draw.color(Pal.accent);
                    Draw.alpha(0.3f);
                    Draw.rect(ArsenalVar.gridOutline, drawX, drawY,
                        GRID_LEN * defaultScale() * zoom, GRID_LEN * defaultScale() * zoom);
                }
            }
        }
    }

    private void drawSelect(){
        Draw.reset();
        Vec2 pos = screenToLocalCoordinates(Core.input.mouse());

        //size for a grid
        float len = GRID_LEN * defaultScale() * zoom;

        //left bottom corner
        float xStart = centerX() - (currentUnit.width / 2f) * len + panX * zoom;
        float yStart = centerY() - (currentUnit.height / 2f) * len + panY * zoom;

        //distance from mouse to start
        float xShift = pos.x + x - xStart;
        float yShift = pos.y + y - yStart;

        //big grid
        int xGrid = Mathf.floor(xShift / len);
        int yGrid = Mathf.floor(yShift / len);

        mouseX = xGrid;
        mouseY = yGrid;

        //small grid
        int xSmallGrid = Mathf.floor(xShift / (len / 2f));
        int ySmallGrid = Mathf.floor(yShift / (len / 2f));

        float drawX = xStart + len/2f + xGrid * len;
        float drawY = yStart + len/2f + yGrid * len;

        float drawSmallX = xStart + len/4f + xSmallGrid * len / 2f;
        float drawSmallY = yStart + len/4f + ySmallGrid * len / 2f;

        Draw.color(Pal.heal);
        Draw.alpha(0.5f);
        Fill.square(drawX, drawY, len/2f);
        Draw.color(Pal.remove);
        Draw.alpha(0.5f);
        Fill.square(drawSmallX, drawSmallY, len/4f);

        Draw.color();
        if (ArsenalVar.unitGridDialog.currentSelectedWeapon != null){
            WeaponGridData grid = ArsenalVar.unitGridDialog.currentSelectedWeapon;

            int weaponStartX, weaponStartY;

            if (grid.width % 2 != 0){weaponStartX = xGrid - (grid.width/2);}else {
                int gx = xSmallGrid - grid.width + 1;
                if (gx <= 0) gx--;
                weaponStartX = gx / 2;
            }
            if (grid.height % 2 != 0){weaponStartY = yGrid - (grid.height/2);}else {
                int gy = ySmallGrid - grid.height + 1;
                if (gy <= 0) gy--;
                weaponStartY = gy / 2;
            }

            drawWeapon(weaponStartX, weaponStartY, grid);

            for(int x = 0; x < grid.width; x++){
                for(int y = 0; y < grid.height; y++){
                    if (grid.grids.get((grid.height - y - 1) * grid.width + x) == 0) continue;
                    int gx = x + weaponStartX;
                    int gy = y + weaponStartY;
                    Draw.color(Pal.techBlue);
                    Draw.alpha(0.3f);
                    Fill.square(
                        xStart + len / 2f + gx * len,
                        yStart + len / 2f + gy * len,
                        len / 2f
                    );
                    //if (currentUnit.grids.get(gy * currentUnit.width + gx) != 0){
                    //}else {
                    //    Draw.color(Pal.remove);
                    //    Draw.alpha(0.5f);
                    //    Fill.square(
                    //        xStart + len / 2f + gx * len,
                    //        yStart + len / 2f + gy * len,
                    //        len / 2f
                    //    );
                    //}
                }
            }
        }

        Draw.reset();
    }

    private void drawWeapon(int weaponStartX, int weaponStartY, WeaponGridData weapon){
        float len = GRID_LEN * defaultScale() * zoom;
        TextureRegion region = weapon.weapon.region;

        //left bottom corner
        float xStart = centerX() - (currentUnit.width / 2f) * len + panX * zoom;
        float yStart = centerY() - (currentUnit.height / 2f) * len + panY * zoom;

        float drawWeaponX = xStart + weaponStartX * len + weapon.width * len / 2f;
        float drawWeaponY = yStart + weaponStartY * len + weapon.height * len / 2f;

        Draw.rect(region, drawWeaponX, drawWeaponY,  region.width * defaultScale() * zoom, region.height * defaultScale() * zoom);

    }

    private float defaultScale(){
        if (currentUnit != null){
            //return Mathf.clamp(getWidth() / 2f / currentUnit.unit.fullIcon.width, 0.5f, 4f);
            return getWidth() / 2f / currentUnit.unit.fullIcon.width;
        }
        return 1f;
    }

    private float centerX(){
        return x + getWidth()/2f;
    }

    private float getOX(){
        return x;
    }

    private float centerY(){
        return y + getHeight()/2f;
    }

    private float getOY(){
        return y;
    }
}
