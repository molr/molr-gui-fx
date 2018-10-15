package cern.lhc.app.seq.scheduler.util;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class FormattedButton {

    public FormattedButton() {
    }

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
            case "Step":
                return FontAwesomeIcon.SIGN_OUT;
            case "Stop":
                return FontAwesomeIcon.STOP;
            case "Pause":
                return FontAwesomeIcon.PAUSE;
            case "Connect":
                return FontAwesomeIcon.PLUG;
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
            default:
                return Color.BLACK;
        }
    }

}
