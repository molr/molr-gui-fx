package io.molr.gui.fx.widgets;

import io.molr.commons.domain.MissionParameter;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.Editors;
import org.controlsfx.property.editor.PropertyEditor;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static io.molr.commons.util.MissionParameters.defaultValueFor;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.controlsfx.control.PropertySheet.Mode.CATEGORY;

public class ParameterEditor extends BorderPane {

    private final Set<MissionParameterItem> parameterItems;

    public ParameterEditor(Set<MissionParameter<?>> parameters) {
        requireNonNull(parameters, "parameters must not be null");
        this.parameterItems = itemsFrom(parameters);
        init();
    }

    private void init() {
        PropertySheet sheet = new PropertySheet();
        sheet.getItems().addAll(this.parameterItems);
        sheet.setMode(CATEGORY);
        sheet.setModeSwitcherVisible(true);
        sheet.setSearchBoxVisible(true);

        sheet.setPropertyEditorFactory(this::propertyEditorFor);

        setCenter(sheet);
    }

    private PropertyEditor<?> propertyEditorFor(PropertySheet.Item item) {
        MissionParameterItem parameterItem = (MissionParameterItem) item;
        if (parameterItem.parameter.isRequired()) {
            return getPropertyEditor(parameterItem);
        }
        return new OptionalMissionParameterPropertyEditor<>(parameterItem, parameterItem.parameter);
    }

    public Map<String, Object> parameterValues() {
        return parameterItems.stream()
                .filter(item -> !Objects.isNull(item.getValue()))
                .collect(toMap(item -> item.parameter.placeholder().name(), MissionParameterItem::getValue));
    }

    private static Set<MissionParameterItem> itemsFrom(Set<MissionParameter<?>> parameters) {
        return parameters.stream().map(MissionParameterItem::new).collect(toSet());
    }

    static <T> PropertyEditor<T> getPropertyEditor(MissionParameterItem item) {
        if(!item.parameter.allowedValues().isEmpty()) {
            return (PropertyEditor<T>) Editors.createChoiceEditor(item, item.parameter.allowedValues());
        }
        if (Boolean.class.isAssignableFrom(item.getType())) {
            return (PropertyEditor<T>) Editors.createCheckEditor(item);
        }
        if (Number.class.isAssignableFrom(item.getType())) {
            return (PropertyEditor<T>) Editors.createNumericEditor(item);
        }
        return (PropertyEditor<T>) Editors.createTextEditor(item);
    }

    public static class MissionParameterItem implements PropertySheet.Item {

        private final MissionParameter<?> parameter;
        private Object value;

        private MissionParameterItem(MissionParameter<?> parameter) {
            this.parameter = parameter;
            this.value = defaultValueFor(parameter);
        }

        @Override
        public Class<?> getType() {
            return parameter.placeholder().type();
        }

        @Override
        public String getCategory() {
            if (parameter.isRequired()) {
                return "Mandatory";
            } else {
                return "Optional";
            }
        }

        public MissionParameter<?> getParameter() {
            return this.parameter;
        }

        @Override
        public String getName() {
            return parameter.placeholder().name();
        }

        @Override
        public String getDescription() {
            return "Specify value for " + parameter.placeholder().name();
        }

        @Override
        public Object getValue() {
            return value;
        }

        @Override
        public void setValue(Object newValue) {
            this.value = newValue;
        }

        @Override
        public Optional<ObservableValue<? extends Object>> getObservableValue() {
            return Optional.empty();
        }
    }

}
