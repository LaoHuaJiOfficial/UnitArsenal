package arsenal.content.grid;

import arc.struct.IntSeq;
import mindustry.type.UnitType;

public class GridData {
    public IntSeq grids;
    //draw coord shift for units.
    public float xShift, yShift;
    public int width, height;

    public GridData(IntSeq grids, int width, int height, float xShift, float yShift){
        this.grids = grids;

        this.width = width;
        this.height = height;

        this.xShift = xShift;
        this.yShift = yShift;
    }
}
