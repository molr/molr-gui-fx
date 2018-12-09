package io.molr.gui.fx.util;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.Editors;
import org.controlsfx.property.editor.PropertyEditor;

public final class MoreEditors {

    /**
     * Tries to get the most specialized {@link PropertyEditor} for the given item
     *
     * @param item the item that has to be modified with the returned editor
     * @param <T>  the type of the editor's value
     * @return a specialized editor that is able to edit the provided item (fallback to text editor)
     */
    public static <T> PropertyEditor<T> getPropertyEditor(PropertySheet.Item item) {
        if (Boolean.class.isAssignableFrom(item.getType())) {
            return (PropertyEditor<T>) Editors.createCheckEditor(item);
        }
        if (Number.class.isAssignableFrom(item.getType())) {
            return (PropertyEditor<T>) Editors.createNumericEditor(item);
        }
        return (PropertyEditor<T>) Editors.createTextEditor(item);
    }

    private MoreEditors() {
        /* static methods */
    }
}
