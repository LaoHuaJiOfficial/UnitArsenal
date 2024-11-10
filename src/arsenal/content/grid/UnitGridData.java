package arsenal.content.grid;

import arc.math.geom.Point2;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.type.UnitType;

public class UnitGridData {
    public UnitType unit;
    public IntSeq grids;
    public Seq<Point2> weaponGrids;
    public Seq<Point2> armorGrids;
    public Seq<Point2> powerGrids;
    public Seq<Point2> engineGrids;
    //draw coord shift for units.
    public float xShift, yShift;
    public int width, height;

    public UnitGridData(UnitType unit, IntSeq grids, int width, int height, float xShift, float yShift){
        this.unit = unit;
        this.grids = grids;

        this.width = width;
        this.height = height;

        this.xShift = xShift;
        this.yShift = yShift;

        forceSetMirror();
    }

    public void debugConsoleOutput(){
        for (int y = 0; y < height; y++){
            StringBuilder str = new StringBuilder();
            for (int x = 0; x < width; x++){
                if (grids.get(y * width + x) == 0){
                    str.append("   ");

                }else {
                    str.append(" ").append(grids.get(y * width + x)).append(" ");
                }
            }
            Log.info(str);
        }
    }

    public void forceSetMirror(){
        int mirrorStep = (int) (width/2f);
        for (int x = 0; x < mirrorStep; x++){
            for (int y = 0; y < height; y++){
                int left = grids.get(y * width + x);
                int right = grids.get(y * width + (width - x - 1));

                if (left == right) continue;
                grids.set(y * width + x, 0);
                grids.set(y * width + (width - x - 1), 0);
            }
        }
    }
}
