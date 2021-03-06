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

public class BlockAttributeCell extends TreeTableCell<ExecutableLine, EnabledBlockAttributeCellData>{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BlockAttributeCell.class);
    
    private Mole mole;
    private MissionHandle handle;
    
    private final BlockCommand setCommand;
    private final BlockCommand unsetCommand;
    
    public BlockAttributeCell(Mole mole, MissionHandle handle, BlockCommand setCommand, BlockCommand unsetCommand) {
        this.mole = mole;
        this.handle = handle;
        this.setCommand = setCommand;
        this.unsetCommand = unsetCommand;
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
        if(item.allowedCommands().contains(setCommand)) {
            MenuItem setBreakpointItem = new MenuItem(setCommand.name());
            setBreakpointItem.setOnAction(new EventHandler<ActionEvent>() {
                
                @Override
                public void handle(ActionEvent event) {
                      LOGGER.info(MessageFormat.format("Set breakpoint for handle={0} block={1}", block.id(), handle));
                      mole.instructBlock(handle, block.id(), setCommand);
                }
            });
            contextMenu.getItems().add(setBreakpointItem);
        }

        if (item.allowedCommands().contains(unsetCommand)) {
            MenuItem unsetBreakpointItem = new MenuItem(unsetCommand.name());
            unsetBreakpointItem.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    LOGGER.info(MessageFormat.format("Unset breakpoint for handle={0} block={1}", block.id(), handle));
                    mole.instructBlock(handle, block.id(), unsetCommand);
                }
            });
            contextMenu.getItems().add(unsetBreakpointItem);
        }

        setContextMenu(contextMenu);
    }

}

