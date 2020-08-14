package io.molr.gui.fx.support;

import io.molr.commons.domain.MissionHandle;
import io.molr.mole.core.api.Mole;

import static io.molr.commons.domain.StrandCommand.RESUME;
import static java.util.Objects.requireNonNull;

public class SimpleMissionControl {

    private final Mole mole;
    private final MissionHandle handle;

    public SimpleMissionControl(Mole mole, MissionHandle handle) {
        this.mole = requireNonNull(mole, "mole must not be null");
        this.handle = requireNonNull(handle, "handle must not be null");
    }

    public SimpleMissionControl and() {
        return this;
    }

    public void resume() {
        mole.instructRoot(handle, RESUME);
        /* This might still be a bit brittle. Strongly speaking, there is no concurrency guarantee ... so it might be that it is not always allowed ...*/
    }
}
