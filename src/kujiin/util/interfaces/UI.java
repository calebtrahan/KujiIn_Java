package kujiin.util.interfaces;

import kujiin.ui.MainController;

public interface UI {

    void setupListeners(MainController Root);
    void setupTooltips();
    void setDisable(boolean disable);
    boolean cleanup();

}
