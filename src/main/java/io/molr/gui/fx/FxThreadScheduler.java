package io.molr.gui.fx;

import javafx.application.Platform;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class FxThreadScheduler {
	
	private final static Scheduler INSTANCE = Schedulers.fromExecutor(Platform::runLater);
	
	public static Scheduler instance() {
		return INSTANCE;
	}
	
}
