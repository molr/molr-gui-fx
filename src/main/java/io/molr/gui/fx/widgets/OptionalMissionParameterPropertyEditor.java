package io.molr.gui.fx.widgets;

import io.molr.commons.domain.MissionParameter;
import io.molr.gui.fx.util.MoreEditors;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.PropertyEditor;
import org.minifx.fxcommons.util.ChangeListeners;

import java.util.function.Consumer;

import static io.molr.commons.util.Exceptions.illegalArgumentException;
import static io.molr.commons.util.MissionParameters.defaultValueFor;

/**
 * ControlsFx {@link PropertyEditor} for editing optional {@link MissionParameter}s.
 *
 * @param <T> the type of the {@link MissionParameter}
 */
public class OptionalMissionParameterPropertyEditor<T> implements PropertyEditor<T> {

    private final CheckBox checkbox;
    private final PropertyEditor<T> editor;
    private final Node editorContainer;

    public OptionalMissionParameterPropertyEditor(PropertySheet.Item item, MissionParameter<T> parameter) {
        if (parameter.isRequired()) {
            throw illegalArgumentException("This editor is for optional parameters, {} is not", parameter.placeholder().name());
        }
        checkbox = new CheckBox();
        editor = MoreEditors.getPropertyEditor(item);
        editor.setValue(parameter.defaultValue() == null ? defaultValueFor(parameter) : parameter.defaultValue());

        editorContainer = makeEditorBox();

        Consumer<Boolean> onSelectedChange = selected -> {
            editor.getEditor().disableProperty().set(!selected);
            if (!selected) {
                item.setValue(null);
            } else {
                item.setValue(editor.getValue());
            }
        };

        checkbox.setSelected(parameter.defaultValue() != null);
        onSelectedChange.accept(parameter.defaultValue() != null);

        checkbox.selectedProperty().addListener(ChangeListeners.onChange(onSelectedChange));
    }

    private Node makeEditorBox() {
        HBox box = new HBox(10);
        Node editorNode = editor.getEditor();
        box.getChildren().addAll(checkbox, editorNode);
        return box;
    }

    @Override
    public Node getEditor() {
        return editorContainer;
    }

    @Override
    public T getValue() {
        if (checkbox.selectedProperty().get()) {
            return editor.getValue();
        }
        return null;
    }

    @Override
    public void setValue(T value) {
        if (value != null) {
            editor.setValue(value);
        }
    }
}
