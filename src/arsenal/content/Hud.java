package arsenal.content;

import arc.scene.ui.Button;
import arc.scene.ui.layout.Table;
import arsenal.ArsenalVar;
import mindustry.Vars;
import mindustry.gen.Iconc;

import static mindustry.Vars.player;

public class Hud {
    public Button arsenalButton;
    public void init(){
        Table minimap = Vars.ui.hudGroup.find("minimap");
        Table table = (Table) minimap.parent;


        arsenalButton = new Button();
        arsenalButton.table(t -> {
            t.setWidth(table.getWidth());
            t.label(() -> Iconc.settings + " ARSENAL");
        }).expand();
        arsenalButton.visibility = () -> player.unit() != null && player.unit().type() != null;
        arsenalButton.clicked(() -> ArsenalVar.unitGridDialog.show());

        table.row().add(arsenalButton);
    }
}
