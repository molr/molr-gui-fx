package io.molr.gui.fx.widgets;

import io.molr.commons.domain.MissionParameter;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
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
import java.util.stream.Collectors;

import static io.molr.commons.util.MissionParameters.defaultValueFor;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.controlsfx.control.PropertySheet.Mode.CATEGORY;

public class ParameterEditor extends BorderPane {

	private static PropertyEditorFactory customPropertyEditorFactory;
	
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
		return parameterItems.stream().filter(item -> !Objects.isNull(item.getValue()))
				.collect(toMap(item -> item.parameter.placeholder().name(), MissionParameterItem::getValue));
	}

	private static Set<MissionParameterItem> itemsFrom(Set<MissionParameter<?>> parameters) {
		return parameters.stream().map(MissionParameterItem::new).collect(toSet());
	}

	public static void registerCustomPropertyEditorFactory(PropertyEditorFactory factoryToBeRegistered) {
		if(customPropertyEditorFactory!=null) {
			throw new IllegalStateException("Custom property editor already registered.");
		}
		customPropertyEditorFactory = factoryToBeRegistered;
	}
	
	@SuppressWarnings("unchecked")
	static <T, U> PropertyEditor<T> getPropertyEditor(MissionParameterItem item) {
		if(customPropertyEditorFactory!=null) {
			Optional<PropertyEditor<T>> customEditor = customPropertyEditorFactory.editorForItem(item);
			if(customEditor.isPresent()) {
				return customEditor.get();
			}
		}
		
		boolean isCollectionType = Collection.class.isAssignableFrom(item.getType());
		if (isCollectionType) {
			MissionParameter<T> param = (MissionParameter<T>) item.getParameter();
			if (param.allowedValues() != null) {
				if (param.allowedValues().size() > 0) {
					Collection<U> allowedValues = (Collection<U>) item.getParameter().allowedValues().iterator().next();
					return (PropertyEditor<T>) collectionItemEditor(item, allowedValues);
				} else {
					Collection<U> allowed = new ArrayList<>();
					return (PropertyEditor<T>) customListItemEditor(item, allowed);
				}
			} else {
				throw new IllegalArgumentException("allowedValues must not be null for " + item);
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
		configureDefaultPaddingSpacing(buttonPane);

		Button selectAll = new Button("select all");
		selectAll.setOnAction(actionEvent -> {
			checkListView.getCheckModel().checkAll();
		});
		buttonPane.getChildren().add(selectAll);

		Button deselectAll = new Button("deselect all");
		deselectAll.setOnAction(actionEvent -> {
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

	public static final <T> PropertyEditor<?> customListItemEditor(Item property, final Collection<T> choices) {		
		
		ObservableList<String> observableChoices = FXCollections.observableArrayList(choices.stream().map(choice->choice.toString()).collect(Collectors.toList()));

		ListView<String> elements = new ListView<>(observableChoices);
		elements.setCellFactory(listView->{
			ListCell<String> cell = new ListCell<>();
			ContextMenu elementContextMenu = new ContextMenu();
			
			MenuItem deleteItem = new MenuItem();
			deleteItem.textProperty().set("delete");
			deleteItem.setOnAction(event->{
				observableChoices.remove(cell.indexProperty().get());
			});
			elementContextMenu.getItems().add(deleteItem);
			
			
			cell.textProperty().bind(cell.itemProperty());
			
			cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty)->{
				if(isNowEmpty) {
					cell.setContextMenu(null);
				}
				else {
					cell.setContextMenu(elementContextMenu);
				}
			});
			
			return cell;
		});
				
		HBox buttonPane = new HBox();
		configureDefaultPaddingSpacing(buttonPane);

		Button deleteAllButton = new Button("clear list");
		deleteAllButton.setOnAction(actionEvent -> {
			observableChoices.clear();
		});
		buttonPane.getChildren().add(deleteAllButton);

		HBox newItemBox = new HBox();
		configureDefaultPaddingSpacing(newItemBox);
		Label newItemLabel = new Label("Item:");
		TextField newItemField = new TextField();
		newItemBox.getChildren().add(newItemLabel);
		newItemBox.getChildren().add(newItemField);
		Button addNewItemButton = new Button("add item");
		addNewItemButton.setOnAction(actionEvent -> {
			String itemToAdd = newItemField.getText();
			observableChoices.add(itemToAdd);
		});
		newItemBox.getChildren().add(addNewItemButton);

		Pane editorPane = new VBox();
		editorPane.getChildren().add(elements);
		editorPane.getChildren().add(newItemBox);
		editorPane.getChildren().add(buttonPane);

		return new AbstractPropertyEditor<Collection<T>, Node>(property, editorPane) {

			@Override
			public void setValue(Collection<T> values) {
				if(values==null) {
					return;
				}
			}

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			protected ObservableValue<Collection<T>> getObservableValue() {
				return new SimpleListProperty(observableChoices);
			}

			@Override
			public Collection<T> getValue() {
				//super.getValue results in calling getObservableValue above
				return new ArrayList<>(super.getValue());
			}
		};

	}

	private static void configureDefaultPaddingSpacing(HBox buttonPane) {
		buttonPane.setPadding(new Insets(10, 10, 10, 10));
		buttonPane.setSpacing(10);
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
