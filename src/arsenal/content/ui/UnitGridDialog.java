package arsenal.content.ui;

import mindustry.ui.dialogs.BaseDialog;

public class UnitGridDialog extends BaseDialog {
    public UnitGridLayout unitGrid = new UnitGridLayout();
    public UnitGridDialog() {
        super("Arsenal Panel");
        clear();
        margin(0f);

        add(unitGrid);
    }
}
