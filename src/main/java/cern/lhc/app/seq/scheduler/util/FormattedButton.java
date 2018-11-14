package cern.lhc.app.seq.scheduler.util;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
//import javafx.scene.paint.Color;

import java.awt.*;

import static de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon.*;
import static javafx.scene.paint.Color.*;

public class FormattedButton {

    Button butt;
    KeyCode associatedKeyCode = null;


    public FormattedButton(String buttonText, String buttonGraphic, String colour, int fontSize, KeyCode kc){
        butt = new Button();
        butt.setText(buttonText);
    }

    public FormattedButton(String buttonText, String buttonGraphic, String colour){
        Text nodeIcon = FontAwesomeIconFactory.get().createIcon(getFontAwesomeIcon(buttonGraphic));
        nodeIcon.setFill(getColour(colour));
        butt = new Button();
        butt.setText(buttonText);
        butt.setGraphic(nodeIcon);
    }

    public FormattedButton(String buttonText, String buttonGraphic){
        Text nodeIcon = FontAwesomeIconFactory.get().createIcon(getFontAwesomeIcon(buttonGraphic));
        butt = new Button();
        butt.setText(buttonText);
        butt.setGraphic(nodeIcon);
    }

    public FormattedButton(String buttonText){
        butt = new Button();
        getAndGuessButton(buttonText);
    }

    public Button getButton(){
        return butt;
    }

    private void getButton(String buttonText, String buttonGraphic, String colour, KeyCode kc){
        Text nodeIcon = FontAwesomeIconFactory.get().createIcon(getFontAwesomeIcon(buttonGraphic));
        nodeIcon.setFill(getColour(colour));
        butt.setText(buttonText);
        butt.setGraphic(nodeIcon);
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

    private FontAwesomeIcon getFontAwesomeIcon(String icon) {

        switch (icon) {
            case "Show":
                return CHECK;
            case "Instantiate":
                return BULLSEYE;
            case "Debug":
                return BUG;
            case "Instances":
                return AMAZON;
            case "Missons":
                return USER_SECRET;
            case "Start":
                return PLAY;
            case "Step Into":
                return SIGN_IN;
            case "Step Over":
                return SIGN_OUT;
            case "Step":
                return SIGN_OUT;
            case "Stop":
                return STOP;
            case "Pause":
                return PAUSE;
            case "Skip":
                return ANGLE_DOUBLE_DOWN;
            case "Connect":
                return PLUG;
            case "PAUSE":
                return PAUSE;
            case "STEP_OVER":
                return SIGN_OUT;
            case "STEP_INTO":
                return SIGN_IN;
            case "SKIP":
                return ANGLE_DOUBLE_DOWN;
            case "RESUME":
                return PLAY;
            default:
                return BEER;

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
