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
import arc.scene.Element;
import arc.scene.event.ElementGestureListener;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.scene.event.Touchable;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.scene.ui.layout.WidgetGroup;
import arc.util.Tmp;
import arsenal.ArsenalVar;
import arsenal.content.grid.UnitGridData;
import mindustry.graphics.Pal;

import static arsenal.ArsenalVar.GRID_LEN;
import static mindustry.Vars.*;

public class UnitGridLayout extends WidgetGroup {
    private static final float GRID_UI_SIZE = 16f;
    private static final float GRID_LINE_STROKE = 4f;
    private final float baseSize = Scl.scl(5f);

    public UnitGridData current;

    float panX, panY, zoom = 1f, lastZoom = -1f;
    //public Element grid;
    public Table debug;

    public UnitGridLayout(){
        setTransform(false);
        setFillParent(true);

        //setSize(getPrefWidth(), getPrefHeight());

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
        });

        addListener(new InputListener(){
            @Override
            public boolean scrolled(InputEvent event, float x, float y, float amountX, float amountY){
                zoom = Mathf.clamp(zoom - amountY / 10f * zoom, 0.25f, 4f);
                return true;
            }
        });
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
        //Draw.color(Pal.accent);
        //Lines.rect(getPanRect());
        Draw.color(Pal.remove);
        Lines.rect(getBoundRect());

        Draw.color();
        if (current != null){
            Draw.alpha(0.3f);
            TextureRegion r = current.unit.fullIcon;
            Draw.rect(r,
                centerX() + panX * zoom,
                centerY() + panY * zoom,
                r.width * defaultScale() * zoom,
                r.height * defaultScale() * zoom
            );
        }

        drawGrid();
        drawSelect();

        Draw.reset();
        super.draw();
        Draw.reset();
    }

    private void drawGrid(){
        if (current == null) return;

        float len = GRID_LEN * defaultScale() * zoom;

        float xStart = centerX() - ((current.width - 1) / 2f) * len + panX * zoom;
        float yStart = centerY() - ((current.height - 1) / 2f) * len + panY * zoom;

        for (int x = 0; x < current.width; x++){
            for (int y = 0; y < current.height; y++){
                float drawX = xStart + x * len;
                float drawY = yStart + (current.height - 1 - y) * len;

                //if (current.grids.get(y * current.width + x) == 0){
                //    Draw.color(Pal.gray);
                //    Draw.alpha(0.3f);
                //    Draw.rect(ArsenalVar.gridOutline, drawX, drawY,
                //        GRID_LEN * defaultScale() * zoom, GRID_LEN * defaultScale() * zoom);
                //}

                if (current.grids.get(y * current.width + x) == 1){
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
        Draw.color(Pal.heal);
        Vec2 pos = screenToLocalCoordinates(Core.input.mouse());

        float len = GRID_LEN * defaultScale() * zoom;

        float xStart = centerX() - (current.width / 2f) * len + panX * zoom;
        float yStart = centerY() - (current.height / 2f) * len + panY * zoom;

        float xShift = pos.x + x - xStart;
        float yShift = pos.y + y - yStart;

        int xGrid = Mathf.floor(xShift / len);
        int yGrid = Mathf.floor(yShift / len);

        float drawX = xStart + len/2f + xGrid * len;
        float drawY = yStart + len/2f + yGrid * len;

        Fill.square(drawX, drawY, len/2f);

        Draw.reset();
    }

    private float defaultScale(){
        if (current != null){
            return Mathf.clamp(getWidth() / 2f / current.unit.fullIcon.width, 0.5f, 4f);
        }
        return 1f;
    }

    private float centerX(){
        return x + getWidth()/2f;
    }

    private float centerY(){
        return y + getHeight()/2f;
    }

    /*
    public void drawSelect(){
        Vec2 pos = screenToLocalCoordinates(Core.input.mouse());
        int tx = Mathf.floor(pos.x / unitSize);
        int ty = Mathf.floor(pos.y / unitSize);
        Draw.color(Pal.techBlue, 0.3f);
        if (queryGridPart!=null){
            //float shiftX = queryGridPart.width/2 == 0? unitSize/2: 0;
            //float shiftY = queryGridPart.height/2 == 0? 0: unitSize/2;
            Fill.rect(
                this.x + tx * unitSize + (queryGridPart.width * unitSize)/2,
                this.y + ty * unitSize + (queryGridPart.height * unitSize)/2,
                unitSize * queryGridPart.width, unitSize * queryGridPart.height
            );
            Draw.color();
            Draw.rect(
                queryGridPart.icon,
                this.x + tx * unitSize + (queryGridPart.width * unitSize)/2,
                this.y + ty * unitSize + (queryGridPart.height * unitSize)/2,
                unitSize * queryGridPart.width, unitSize * queryGridPart.height
            );
        }else {
            Fill.rect(
                this.x + tx * unitSize + unitSize/2,
                this.y + ty * unitSize + unitSize/2,
                unitSize, unitSize
            );
        }
        Draw.reset();
    }

    public void drawGridRect(int x, int y){
        Fill.rect(
            this.x + x * unitSize + unitSize/2,
            this.y + y * unitSize + unitSize/2,
            unitSize, unitSize
        );
    }

    public void drawGridOutline(int x, int y){
        Draw.alpha(0.5f);
        Draw.rect(
            ArsenalVar.gridOutline,
            this.x + x * unitSize + unitSize/2,
            this.y + y * unitSize + unitSize/2,
            unitSize,
            unitSize
        );
        Draw.alpha(1);
    }


    public void drawGridPart(GridPartData grid){
        Draw.rect(grid.icon,
            this.x + grid.startX * unitSize + (grid.width * unitSize)/2,
            this.y + grid.startY * unitSize + (grid.height * unitSize)/2,
            grid.width * unitSize, grid.height * unitSize
        );
        Draw.reset();
    }

    @Override
    public float getPrefWidth(){
        return bounds * unitSize;
    }

    @Override
    public float getPrefHeight(){
        return bounds * unitSize;
    }

     */

}
