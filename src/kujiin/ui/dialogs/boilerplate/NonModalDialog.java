package kujiin.ui.dialogs.boilerplate;


import javafx.stage.Modality;
import javafx.stage.Stage;
import kujiin.ui.MainController;

public class NonModalDialog extends ModalDialog {

    public NonModalDialog(MainController Root, Stage stage, boolean minimizeparent) {
        super(Root, stage, minimizeparent);
        initModality(Modality.NONE);
    }
}
