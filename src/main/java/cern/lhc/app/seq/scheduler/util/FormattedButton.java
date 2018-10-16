package cern.lhc.app.seq.scheduler.util;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class FormattedButton {


    public  Button getButton(String buttonText){
        Button butt = new Button();
        butt.setMinSize(100,100);
        butt.setText(buttonText);
        return butt;
    }

    public  Button getButton(String buttonText, String buttonGraphic){
        Text nodeIcon = FontAwesomeIconFactory.get().createIcon(getFontAwesomeIcon(buttonGraphic));
        Button butt = getButton(buttonText);
        butt.setGraphic(nodeIcon);
        return butt;
    }

    public  Button getButton(String buttonText, String buttonGraphic, String colour){
        Text nodeIcon = FontAwesomeIconFactory.get().createIcon(getFontAwesomeIcon(buttonGraphic));
        nodeIcon.setFill(getColour(colour));
        Button butt = getButton(buttonText);
        butt.setGraphic(nodeIcon);
        return butt;
    }

    public Button getButton(String buttonText, String buttonGraphic, String colour, int fontSize){
        Button butt = getButton(buttonText, buttonGraphic, colour);
        butt.setStyle("-fx-font-size: " + fontSize);
        return butt;
    }

    public Button getAndGuessButton(String buttonText){
        // PAUSE, STEP_OVER, STEP_INTO, SKIP, RESUME;
        switch (buttonText){
            case "PAUSE":
                return getButton("Pause","Pause","Blue");
            case "STEP_OVER":
                return getButton("Step Over","Step Into","Cyan");
            case "STEP_INTO":
                return getButton("Step Into","Step Over","LightGreen");
            case "SKIP":
                return getButton("Skip","Skip","LightBlue");
            case "RESUME":
                return getButton("Resume","Start","Green");
            default:
                return getButton(" ", " ", " ");


        }
    }

    private FontAwesomeIcon getFontAwesomeIcon(String icon) {

        switch (icon) {
            case "Available":
                return FontAwesomeIcon.CHECK;
            case "Instantiate":
                return FontAwesomeIcon.BULLSEYE;
            case "Debug":
                return FontAwesomeIcon.BUG;
            case "Instances":
                return FontAwesomeIcon.AMAZON;
            case "Missons":
                return FontAwesomeIcon.USER_SECRET;
            case "Start":
                return FontAwesomeIcon.PLAY;
            case "Step Into":
                return FontAwesomeIcon.SIGN_IN;
            case "Step Over":
                return FontAwesomeIcon.SIGN_OUT;
            case "Step":
                return FontAwesomeIcon.SIGN_OUT;
            case "Stop":
                return FontAwesomeIcon.STOP;
            case "Pause":
                return FontAwesomeIcon.PAUSE;
            case "Skip":
                return FontAwesomeIcon.ANGLE_DOUBLE_DOWN;
            case "Connect":
                return FontAwesomeIcon.PLUG;
            case "PAUSE":
                return FontAwesomeIcon.PAUSE;
            case "STEP_OVER":
                return FontAwesomeIcon.SIGN_OUT;
            case "STEP_INTO":
                return FontAwesomeIcon.SIGN_IN;
            case "SKIP":
                return FontAwesomeIcon.ANGLE_DOUBLE_DOWN;
            case "RESUME":
                return FontAwesomeIcon.PLAY;
            default:
                return FontAwesomeIcon.BEER;

        }
    }

    private Color getColour(String colour) {
        switch (colour) {
            case "Green":
                return Color.GREEN;
            case "Blue":
                return Color.BLUE;
            case "Red":
                return Color.RED;
            case "Yellow":
                return Color.YELLOW;
            case "Cyan":
                return Color.CYAN;
            case "LightGreen":
                return Color.LIGHTGREEN;
            case "LightBlue":
                return Color.LIGHTBLUE;
            default:
                return Color.BLACK;
        }
    }

}
