package io.molr.gui.fx.widgets.breakpoints;

import java.util.HashSet;
import java.util.Set;

import io.molr.commons.domain.BlockCommand;

public class BreakpointCellData {
    
    private static final String TEXT_BREAKPOINT = "X";
    private static final String TEXT_NO_BREAKPOINT = "";
    private final Set<BlockCommand> allowedCommands;
    private final boolean breakpoint;
    
    public BreakpointCellData(Set<BlockCommand> allowedCommands, boolean breakpoint) {
        this.allowedCommands = allowedCommands;
        this.breakpoint = breakpoint;
    }
    
    public Set<BlockCommand> allowedCommands(){
        return allowedCommands;
    }
    
    public boolean isBreakpoint() {
        return breakpoint;
    }
    
    public String text() {
        if(isBreakpoint()) {
            return TEXT_BREAKPOINT;
        }
        return TEXT_NO_BREAKPOINT;
    }
    
    public static BreakpointCellData undefined() {
        return new BreakpointCellData(new HashSet<>(), false);
    }

}

