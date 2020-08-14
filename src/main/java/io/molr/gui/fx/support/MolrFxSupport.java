package io.molr.gui.fx.support;

import io.molr.commons.domain.Mission;
import io.molr.mole.core.support.domain.VoidStub0;
import io.molr.mole.core.support.domain.VoidStub1;
import io.molr.mole.core.support.domain.VoidStub2;
import io.molr.mole.core.support.domain.VoidStub3;

import java.util.Map;
import java.util.Set;

public interface MolrFxSupport {

    OngoingDebug debug(Mission mission);

    OngoingDebug debug(Mission mission, Map<String, Object> parameters);

    default OngoingDebug debug(VoidStub0 stub0) {
        return debug(stub0.mission());
    }

    default <P1> OngoingDebug debug(VoidStub1<P1> stub, P1 p1) {
        return debug(stub.mission(), stub.parameters(p1));
    }

    default <P1, P2> OngoingDebug debug(VoidStub2<P1, P2> stub, P1 p1, P2 p2) {
        return debug(stub.mission(), stub.parameters(p1, p2));
    }

    default <P1, P2, P3> OngoingDebug debug(VoidStub3<P1, P2, P3> stub, P1 p1, P2 p2, P3 p3) {
        return debug(stub.mission(), stub.parameters(p1, p2, p3));
    }


    Set<Mission> executableMissions();

}
