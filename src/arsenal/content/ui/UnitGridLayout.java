package arsenal.content.ui;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.scene.event.Touchable;
import arc.scene.ui.layout.WidgetGroup;
import arc.struct.Seq;
import arc.util.Align;
import arsenal.ArsenalVar;
import arsenal.content.grid.GridPartData;
import mindustry.graphics.Pal;

import static mindustry.Vars.player;

public class UnitGridLayout extends WidgetGroup {
    public static final float GRID_UI_SIZE = 32f;
    public UnitGridLayout(){
        setTransform(false);
        setFillParent(true);

        //setOrigin(Align.bottomLeft);
        setSize(getPrefWidth(), getPrefHeight());
        touchable(() -> Touchable.disabled);
    }

    @Override
    public void draw(){
        validate();

        Lines.stroke(4f);
        Draw.color(Pal.accent);
        Lines.rect(x, y, getWidth(), getHeight());
        Draw.color(Pal.gray);

        Lines.line(x + GRID_UI_SIZE, y + GRID_UI_SIZE, x + getWidth() - GRID_UI_SIZE, y + GRID_UI_SIZE);
        Lines.line(x + GRID_UI_SIZE, y + GRID_UI_SIZE, x + GRID_UI_SIZE, y + getHeight() - GRID_UI_SIZE);

        Draw.reset();
        super.draw();
        Draw.reset();
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
