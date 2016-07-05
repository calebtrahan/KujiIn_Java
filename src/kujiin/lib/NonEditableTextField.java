package kujiin.lib;

import javafx.scene.control.TextField;

public class NonEditableTextField extends TextField {

    public NonEditableTextField() {
        super();
        setEditable(false);
    }

    public NonEditableTextField(String text) {
        super(text);
        setEditable(false);
    }

}
