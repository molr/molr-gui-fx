package io.molr.gui.fx;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import io.molr.commons.domain.AgencyState;
import io.molr.commons.domain.Mission;
import io.molr.commons.domain.MissionInstance;

public class EmptyAgencyStates implements AgencyState{

	@Override
	public Set<Mission> executableMissions() {
		return ImmutableSet.of();
	}

	@Override
	public Set<MissionInstance> activeMissions() {
		return ImmutableSet.of();
	}

}
