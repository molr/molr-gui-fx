package io.molr.gui.fx.widgets;

import java.util.Optional;

import org.controlsfx.property.editor.PropertyEditor;

import io.molr.gui.fx.widgets.ParameterEditor.MissionParameterItem;

public interface PropertyEditorFactory {
	
	<T, U> Optional<PropertyEditor<T>> editorForItem(MissionParameterItem item);

}
