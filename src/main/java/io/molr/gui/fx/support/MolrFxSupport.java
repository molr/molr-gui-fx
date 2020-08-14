package io.molr.gui.fx.support;

import io.molr.commons.domain.Mission;

import java.util.Map;
import java.util.Set;

public interface MolrFxSupport {

    OngoingDebug debug(Mission mission);

    OngoingDebug debug(Mission mission, Map<String, Object> parameters);

    Set<Mission> executableMissions();

}
