package arsenal.content.ui;

import arc.scene.ui.Button;
import arc.scene.ui.layout.Table;
import arsenal.ArsenalVar;
import mindustry.Vars;
import mindustry.gen.Iconc;

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
        arsenalButton.clicked(() -> ArsenalVar.unitGridDialog.show());

        table.row().add(arsenalButton);
    }
}
