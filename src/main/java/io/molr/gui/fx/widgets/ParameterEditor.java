package io.molr.gui.fx.widgets;

import io.molr.commons.domain.MissionParameter;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import org.controlsfx.control.CheckListView;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.PropertySheet.Item;
import org.controlsfx.property.editor.AbstractPropertyEditor;
import org.controlsfx.property.editor.Editors;
import org.controlsfx.property.editor.PropertyEditor;

import java.util.ArrayList;
import java.util.Collection;
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

	@SuppressWarnings("unchecked")
	static <T, U> PropertyEditor<T> getPropertyEditor(MissionParameterItem item) {
		boolean isCollectionType = Collection.class.isAssignableFrom(item.getType());
		if (isCollectionType) {
			MissionParameter<T> param = (MissionParameter<T>) item.getParameter();
			if(param.allowedValues()!=null) {
				if(param.allowedValues().size()>0) {
					Collection<U> allowedValues = (Collection<U>) item.getParameter().allowedValues().iterator().next();
					return (PropertyEditor<T>) collectionItemEditor(item, allowedValues);
				}
				else {
					throw new IllegalArgumentException("allowedValues must not be empty " + item);
				}
			}
			else {
				throw new IllegalArgumentException("allowedValues must not be null for "+item);
			}
		}
		if (!item.parameter.allowedValues().isEmpty()) {
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

	public static final <T> PropertyEditor<?> collectionItemEditor(Item property, final Collection<T> choices) {
		
		final CheckListView<T> checkListView = new CheckListView<T>();
		checkListView.setItems(FXCollections.observableArrayList(choices));
		
	    HBox buttonPane = new HBox();
	    buttonPane.setPadding(new Insets(10, 10, 10, 10));
	    buttonPane.setSpacing(10);

		Button selectAll = new Button("select all");
		selectAll.setOnAction(actionEvent -> {
			checkListView.getCheckModel().checkAll();
		});
		buttonPane.getChildren().add(selectAll);
		
		Button deselectAll = new Button("deselect all");
		deselectAll.setOnAction(actionEvent->{
			checkListView.getCheckModel().clearChecks();
		});
		buttonPane.getChildren().add(deselectAll);
		
		Pane editorPane = new VBox();
		editorPane.getChildren().add(checkListView);
		editorPane.getChildren().add(buttonPane);

		return new AbstractPropertyEditor<Collection<T>, Node>(property, editorPane) {

			@Override
			public void setValue(Collection<T> values) {
				values.forEach(value -> {
					checkListView.getCheckModel().check(value);
				});
			}

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			protected ObservableValue<Collection<T>> getObservableValue() {
				ObservableList<T> checkedItems = checkListView.getCheckModel().getCheckedItems();
				return new SimpleListProperty(checkedItems);
			}
			
			@Override
			public Collection<T> getValue() {
				return new ArrayList<>(super.getValue());
			}
		};
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
