package arsenal.content.grid;

import arc.struct.IntSeq;
import arc.util.Log;
import mindustry.type.UnitType;

public class UnitGridData extends GridData{
    public UnitType unit;

    public UnitGridData(UnitType unit, IntSeq grids, int width, int height, float xShift, float yShift){
        super(grids, width, height, xShift, yShift);
        this.unit = unit;
    }

    public UnitGridData(UnitType unit, GridData gridData){
        super(gridData.grids, gridData.width, gridData.height, gridData.xShift, gridData.yShift);
        this.unit = unit;
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
