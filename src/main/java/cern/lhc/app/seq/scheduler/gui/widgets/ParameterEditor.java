package cern.lhc.app.seq.scheduler.gui.widgets;

import javafx.beans.value.ObservableValue;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.Editors;
import org.controlsfx.property.editor.PropertyEditor;
import org.molr.commons.domain.MissionParameter;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
        if (Boolean.class.isAssignableFrom(item.getType())) {
            return Editors.createCheckEditor(item);
        }
        if (Number.class.isAssignableFrom(item.getType())) {
            return Editors.createNumericEditor(item);
        }
        return Editors.createTextEditor(item);
    }

    public Map<String, Object> parameterValues() {
        return parameterItems.stream()
                .filter(i -> !Objects.isNull(i.value))
                .collect(toMap(i -> i.param.placeholder().name(), i -> i.value));
    }

    private final static Set<MissionParameterItem> itemsFrom(Set<MissionParameter<?>> parameters) {
        return parameters.stream().map(MissionParameterItem::new).collect(toSet());
    }

    private static class MissionParameterItem implements PropertySheet.Item {

        private final MissionParameter<?> param;
        private Object value;

        private MissionParameterItem(MissionParameter<?> param) {
            this.param = param;
            this.value = param.defaultValue();
        }

        @Override
        public Class<?> getType() {
            return param.placeholder().type();
        }

        @Override
        public String getCategory() {
            if (param.isRequired()) {
                return "mandatory";
            } else {
                return "optional";
            }
        }

        @Override
        public String getName() {
            return param.placeholder().name();
        }

        @Override
        public String getDescription() {
            return "";
        }

        @Override
        public Object getValue() {
            return value;
        }

        @Override
        public void setValue(Object value) {
            this.value = value;
        }

        @Override
        public Optional<ObservableValue<? extends Object>> getObservableValue() {
            return Optional.empty();
        }
    }


}
