package kujiin.interfaces;

import javafx.concurrent.Service;

import java.io.File;

public interface Exportable {

    Service<Boolean> getexportservice();
    Boolean exportedsuccesfully();
    Boolean cleanuptempfiles();
    File getFinalexportfile();
    Boolean mixentrainmentandambience();
    Boolean sessionreadyforFinalExport();
}
