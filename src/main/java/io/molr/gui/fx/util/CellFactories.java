package io.molr.gui.fx.util;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.util.function.Function;

/**
 * Provides some utility methods to create cell factories.
 */
public class CellFactories {

    private CellFactories() {
        /* only static methods */
    }

    /**
     * Creates a cell factory which maps an item to a string, given the provided mapper.
     *
     * @param toTextMapper the mapper to use to map non-null objects to strings
     * @param <T>          the type of the list item
     * @return a factory which can be used in setCellFactory methods
     */
    public static final <T> Callback<ListView<T>, ListCell<T>> nonNullItemText(Function<T, String> toTextMapper) {
        return new Callback<ListView<T>, ListCell<T>>() {
            @Override
            public ListCell<T> call(ListView<T> param) {
                return new ListCell<T>() {
                    @Override
                    protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setText(null);
                        } else {
                            setText(toTextMapper.apply(item));
                        }
                    }
                };
            }
        };
    }
}
