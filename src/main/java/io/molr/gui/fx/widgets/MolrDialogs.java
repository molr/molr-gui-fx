package io.molr.gui.fx.widgets;

import io.molr.commons.domain.Mission;
import io.molr.commons.domain.MissionParameterDescription;
import javafx.scene.control.Dialog;

import java.util.Map;

import static javafx.scene.control.ButtonType.APPLY;
import static javafx.scene.control.ButtonType.CANCEL;

/**
 * Contains static utility methods for mol related dialogs
 */
public final class MolrDialogs {

    private MolrDialogs() {
        throw new UnsupportedOperationException("Only static methods");
    }

    public static final Dialog<Map<String, Object>> parameterDialogFor(Mission mission, MissionParameterDescription description) {
        ParameterEditor editor = new ParameterEditor(description.parameters());

        Dialog<Map<String, Object>> dialog1 = new Dialog<>();
        dialog1.setTitle("Parameters for mission '" + mission.name() + "'.");
        dialog1.setHeaderText("Please check and complete the parameters for this mission.");

        dialog1.getDialogPane().setContent(editor);
        dialog1.getDialogPane().getButtonTypes().addAll(APPLY, CANCEL);

        dialog1.setResultConverter(b -> {
            if (b == APPLY) {
                return editor.parameterValues();
            }
            return null;
        });
        return dialog1;
    }
}
