package io.molr.gui.fx.util;

import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import static javafx.scene.paint.Color.*;

public class FormattedButton {

    Button butt;
    KeyCode associatedKeyCode = null;

    public FormattedButton(String buttonText, String buttonGraphic, String colour, int fontSize, KeyCode kc){
        butt = new Button();
        butt.setText(buttonText);
    }
    
    public FormattedButton(String buttonText, String buttonGraphic, String colour){
        butt = new Button();
        butt.setText(buttonText);
        butt.setGraphic(glyphFor(buttonGraphic, colour));
    }

    public FormattedButton(String buttonText, String buttonGraphic){
        this(buttonText, buttonGraphic, "Black");
    }

    public FormattedButton(String buttonText){
        butt = new Button();
        getAndGuessButton(buttonText);
    }

    public Button getButton(){
        return butt;
    }

	private Glyph glyphFor(String buttonGraphic, String color) {
    	GlyphFont fontAwesome = GlyphFontRegistry.font("FontAwesome");
    	Glyph glyph = fontAwesome.create(getFontAwesomeIcon(buttonGraphic)).color(getColour(color));
    	return glyph;
	}
	 
    
    private void getButton(String buttonText, String buttonGraphic, String colour, KeyCode kc){
        butt.setText(buttonText);
        butt.setGraphic(glyphFor(buttonGraphic, colour));
        butt.setTooltip(new Tooltip(kc.getName()));
       // assignKeyboardShortcut(kc);
        associatedKeyCode = kc;
    }


    public KeyCode getKeyCode(){
        return associatedKeyCode;
    }

    public Button getAndGuessButton(String buttonText){
        // PAUSE, STEP_OVER, STEP_INTO, SKIP, RESUME;
        switch (buttonText){
            case "PAUSE":
                getButton("Pause","Pause","Blue", KeyCode.F2);
                return butt;
            case "STEP_OVER":
                getButton("Step Over","Step Into","Cyan",KeyCode.F6);
                return butt;
            case "STEP_INTO":
                getButton("Step Into","Step Over","LightGreen",KeyCode.F7);
                return butt;
            case "SKIP":
                getButton("Skip","Skip","LightBlue", KeyCode.F8);
                return butt;
            case "RESUME":
                 getButton("Resume","Start","Green",KeyCode.F5);
                return butt;
            default:
                 getButton(" ", " ", " ",null);
                return butt;
        }
    }

    private FontAwesome.Glyph getFontAwesomeIcon(String icon) {
    	System.out.println("calledWithIcon>"+icon);
        switch (icon) {
            case "Show":
                return FontAwesome.Glyph.CHECK;
            case "Instantiate":
                return FontAwesome.Glyph.BULLSEYE;
            case "Debug":
                return FontAwesome.Glyph.BUG;
            case "Instances":
                return FontAwesome.Glyph.SITEMAP;
            case "Missons":
                return FontAwesome.Glyph.USER_SECRET;
            case "Start":
                return FontAwesome.Glyph.PLAY;
            case "Step Into":
                return FontAwesome.Glyph.SIGN_IN;
            case "Step Over":
                return FontAwesome.Glyph.SIGN_OUT;
            case "Step":
                return FontAwesome.Glyph.SIGN_OUT;
            case "Stop":
                return FontAwesome.Glyph.STOP;
            case "Pause":
                return FontAwesome.Glyph.PAUSE;
            case "Skip":
                return FontAwesome.Glyph.ANGLE_DOUBLE_DOWN;
            case "Connect":
                return FontAwesome.Glyph.PLUG;
            case "PAUSE":
                return FontAwesome.Glyph.PAUSE;
            case "STEP_OVER":
                return FontAwesome.Glyph.SIGN_OUT;
            case "STEP_INTO":
                return FontAwesome.Glyph.SIGN_IN;
            case "SKIP":
                return FontAwesome.Glyph.ANGLE_DOUBLE_DOWN;
            case "RESUME":
                return FontAwesome.Glyph.PLAY;
            default:
                return FontAwesome.Glyph.BEER;

        }
    }

    private Color getColour(String colour) {
        switch (colour) {
            case "Green":
                return GREEN;
            case "Blue":
                return BLUE;
            case "Red":
                return RED;
            case "Yellow":
                return YELLOW;
            case "Cyan":
                return CYAN;
            case "LightGreen":
                return LIGHTGREEN;
            case "LightBlue":
                return LIGHTBLUE;
            default:
                return Color.BLACK;
        }
    }

}
