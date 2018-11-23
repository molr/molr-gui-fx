/*
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package cern.lhc.app.seq.scheduler.gui.widgets.progress;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;

/**
 * A class containing a {@link TreeTableCell} implementation that draws a {@link ProgressBar} node with a text overlay
 * inside the cell.
 * <p>
 * This class is based on the java ProgressBarTreeTableCell (and is actually a copy of it with some changes)
 */
public class TextProgressBarTreeTableCell<S> extends TreeTableCell<S, Progress> {

    /***************************************************************************
     *                                                                         *
     * Static cell factories                                                   *
     *                                                                         *
     **************************************************************************/

    /**
     * Provides a {@link ProgressBar} that allows easy visualisation of a Number value as it proceeds from 0.0 to 1.0.
     * If the value is -1, the progress bar will appear indeterminate.
     *
     * @return A {@link Callback} that can be inserted into the {@link TreeTableColumn#cellFactoryProperty() cell
     * factory property} of a TreeTableColumn, that enables visualisation of a Number as it progresses from 0.0 to 1.0.
     */
    public static <S> Callback<TreeTableColumn<S, Progress>, TreeTableCell<S, Progress>> forTreeTableColumn() {
        return param -> new TextProgressBarTreeTableCell<S>();
    }


    /***************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/

    private final TextProgressBar progressBar;

    private ObservableValue<Progress> observable;


    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a default {@link TextProgressBarTreeTableCell} instance
     */
    public TextProgressBarTreeTableCell() {
        this.getStyleClass().add("progress-bar-tree-table-cell");

        this.progressBar = new TextProgressBar();
        this.progressBar.setMaxWidth(Double.MAX_VALUE);
    }


    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateItem(Progress item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setGraphic(null);
        } else {
            progressBar.progressProperty().unbind();

            final TreeTableColumn<S, Progress> column = getTableColumn();
            observable = column == null ? null : column.getCellObservableValue(getIndex());

            if (observable != null) {
                progressBar.progressProperty().bind(observable);
            } else if (item != null) {
                progressBar.setProgress(item);
            }

            setGraphic(progressBar);
        }
    }
}
