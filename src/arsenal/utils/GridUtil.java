package arsenal.utils;

import arc.Core;
import arc.graphics.Pixmap;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.BoolSeq;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Tmp;
import arsenal.content.grid.UnitGridData;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

import static arsenal.ArsenalVar.GRID_LEN;

public class GridUtil {
    //sample 4 pixel to check valid grid
    public static final Point2[] samplingPixels = {new Point2(3, 3), new Point2(3, 12), new Point2(12, 12), new Point2(12, 3)};

    public static void AddUnitWeapons(Unit unit, WeaponMount weapon){
        WeaponMount[] weaponTemp = new WeaponMount[unit.mounts.length + 1];
        System.arraycopy(unit.mounts, 0, weaponTemp, 0, unit.mounts.length);
        weaponTemp[unit.mounts.length] = weapon;
        unit.mounts = weaponTemp;
    }

    public static UnitGridData getUnitRect(UnitType unit){
        TextureRegion region = unit.fullIcon;

        int widthStep = Mathf.ceil((float) region.width / GRID_LEN);
        int heightStep = Mathf.ceil((float) region.height / GRID_LEN);

        int width = widthStep * GRID_LEN;
        int height = heightStep * GRID_LEN;

        Pixmap pixmap = Core.atlas.getPixmap(region).crop();

        float padLeft = (width - pixmap.width) / 2f, padBot = (height - pixmap.height) / 2f;
        int startLeft = -(int) padLeft, startBot = -(int) padBot;

        IntSeq tmpPoints = new IntSeq();
        tmpPoints.setSize(width * height);

        for (int x = 0; x < widthStep; x++){
            for (int y = 0; y < heightStep; y++){
                int xCoord = startLeft + x * GRID_LEN;
                int yCoord = startBot + y * GRID_LEN;

                int sampleCount = 0;
                for (Point2 point2: samplingPixels){
                    int pixel = pixmap.get(xCoord + point2.x, yCoord + point2.y);
                    //check for empty pixels, +1 if true
                    if ((pixel & 0x000000ff) == 0) sampleCount++;
                }

                if (sampleCount > 2){tmpPoints.set(y * widthStep + x, 0);}else {tmpPoints.set(y * widthStep + x, 2);}
            }
        }
        pixmap.dispose();

        return new UnitGridData(unit, tmpPoints, widthStep, heightStep, padLeft, padBot);
    }
}
