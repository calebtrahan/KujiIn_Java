package kujiin.ui.dialogs.alerts;

import kujiin.xml.Preferences;

public class ConfirmationDialogWithOption extends ConfirmationDialog {

    private boolean Option;

    public ConfirmationDialogWithOption(Preferences preferences, String titletext, String headertext, String contenttext, String yesbuttontext, String nobuttontext, boolean modal) {
        super(preferences, titletext, headertext, contenttext, yesbuttontext, nobuttontext, modal);
    }
    public ConfirmationDialogWithOption(Preferences preferences, String titletext, String headertext, String contenttext, String yesbuttontext, String nobuttontext) {
        super(preferences, titletext, headertext, contenttext, yesbuttontext, nobuttontext);
    }
    public ConfirmationDialogWithOption(Preferences preferences, String title, String header, String content, boolean modal) {
        super(preferences, title, header, content, modal);
    }
    public ConfirmationDialogWithOption(Preferences preferences, String title, String header, String content) {
        super(preferences, title, header, content);
    }

    public boolean hasOption() {
        return Option;
    }

}