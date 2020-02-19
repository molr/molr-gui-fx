package io.molr.gui.fx.widgets.breakpoints;

import io.molr.commons.domain.Block;
import io.molr.commons.domain.BlockCommand;
import io.molr.commons.domain.MissionHandle;
import io.molr.commons.domain.RunState;
import io.molr.gui.fx.widgets.ExecutableLine;
import io.molr.mole.core.api.Mole;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableRow;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author krepp
 */
public class BreakpointCell extends TreeTableCell<ExecutableLine, Boolean>{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BreakpointCell.class);
    
    private Mole mole;
    private MissionHandle handle;
    
    public BreakpointCell(Mole mole, MissionHandle handle) {
        this.mole = mole;
        this.handle = handle;
    }
    
    
    
    @Override
    protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        if(empty) {
            this.setContextMenu(null);
            this.setText(null);
            return;
        }
        
        TreeTableRow<ExecutableLine> row = getTreeTableRow();
        ExecutableLine line = row.getItem();
        
        if(line == null) {
            System.out.println("line is null");
            return;
        }
        System.out.println("updateCalled "+item);
        Block block = line.executable();
        this.textProperty().set(item.toString());
        ContextMenu contextMenu = new ContextMenu();

        RunState runState = line.runStateProperty().get();
        if(runState == RunState.UNDEFINED) {
            MenuItem setBreakpointItem = new MenuItem("SET_BREAKPOINT");
            //set breakpoint makes no sense if breakpoint is already passed
            setBreakpointItem.setOnAction(new EventHandler<ActionEvent>() {
                
                @Override
                public void handle(ActionEvent event) {
                      LOGGER.info(MessageFormat.format("Set breakpoint for handle={0} block={1}", block.id(), handle));
                      mole.instructBlock(handle, block.id(), BlockCommand.SET_BREAKPOINT);
                }
            });
            contextMenu.getItems().add(setBreakpointItem);
        }



        MenuItem unsetBreakpointItem = new MenuItem("UNSET_BREAKPOINT");
        unsetBreakpointItem.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                LOGGER.info(MessageFormat.format("Unset breakpoint for handle={0} block={1}", block.id(), handle));
                mole.instructBlock(handle, block.id(), BlockCommand.UNSET_BREAKPOINT);     
            }
        });

        contextMenu.getItems().add(unsetBreakpointItem);
        setContextMenu(contextMenu);
    }

    // You can choose a logger (needed imports are given in the import section as comments):
    // for libraries:
    // private static final Logger LOGGER = LoggerFactory.getLogger(BreakpointCheckbox.class);
    // for applications:
    // private static final AppLogger LOGGER = AppLogger.getLogger();
}

