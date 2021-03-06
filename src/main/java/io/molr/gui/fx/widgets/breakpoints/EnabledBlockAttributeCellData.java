package io.molr.gui.fx.widgets.breakpoints;

import io.molr.commons.domain.BlockCommand;

import java.util.HashSet;
import java.util.Set;

public class EnabledBlockAttributeCellData {
    
    private static final String TEXT_BREAKPOINT = "X";
    private static final String TEXT_NO_BREAKPOINT = "";
    private final Set<BlockCommand> allowedCommands;
    private final boolean breakpoint;
    
    public EnabledBlockAttributeCellData(Set<BlockCommand> allowedCommands, boolean breakpoint) {
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
    
    public static EnabledBlockAttributeCellData undefined() {
        return new EnabledBlockAttributeCellData(new HashSet<>(), false);
    }

}

