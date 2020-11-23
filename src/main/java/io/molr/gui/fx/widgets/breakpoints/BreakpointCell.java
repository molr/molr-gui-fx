package io.molr.gui.fx.widgets.breakpoints;

import io.molr.commons.domain.Block;
import io.molr.commons.domain.BlockCommand;
import io.molr.commons.domain.MissionHandle;
import io.molr.gui.fx.widgets.ExecutableLine;
import io.molr.mole.core.api.Mole;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

public class BreakpointCell extends TreeTableCell<ExecutableLine, EnabledBlockAttributeCellData>{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BreakpointCell.class);
    
    private Mole mole;
    private MissionHandle handle;
    
    public BreakpointCell(Mole mole, MissionHandle handle) {
        this.mole = mole;
        this.handle = handle;
    }
    
    
    
    @Override
    protected void updateItem(EnabledBlockAttributeCellData item, boolean empty) {
        super.updateItem(item, empty);
        if(empty) {
            this.setContextMenu(null);
            this.setText(null);
            return;
        }
        
        TreeTableRow<ExecutableLine> row = getTreeTableRow();
        ExecutableLine line = row.getItem();
        
        if(line == null) {
            return;
        }
        
        this.textProperty().set(item.text());

        Block block = line.executable();
        ContextMenu contextMenu = new ContextMenu();

        if(item.allowedCommands().contains(BlockCommand.SET_BREAKPOINT)) {
            MenuItem setBreakpointItem = new MenuItem("SET_BREAKPOINT");
            setBreakpointItem.setOnAction(new EventHandler<ActionEvent>() {
                
                @Override
                public void handle(ActionEvent event) {
                      LOGGER.info(MessageFormat.format("Set breakpoint for handle={0} block={1}", block.id(), handle));
                      mole.instructBlock(handle, block.id(), BlockCommand.SET_BREAKPOINT);
                }
            });
            contextMenu.getItems().add(setBreakpointItem);
        }

        if (item.allowedCommands().contains(BlockCommand.UNSET_BREAKPOINT)) {
            MenuItem unsetBreakpointItem = new MenuItem("UNSET_BREAKPOINT");
            unsetBreakpointItem.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    LOGGER.info(MessageFormat.format("Unset breakpoint for handle={0} block={1}", block.id(), handle));
                    mole.instructBlock(handle, block.id(), BlockCommand.UNSET_BREAKPOINT);
                }
            });
            contextMenu.getItems().add(unsetBreakpointItem);
        }

        setContextMenu(contextMenu);
    }

}

