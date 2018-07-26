package cern.lhc.app.seq.scheduler.adapter.seq;

import cern.lhc.app.seq.scheduler.gui.commands.Open;
import cern.lhc.app.seq.scheduler.gui.commands.ResultChange;
import cern.lhc.app.seq.scheduler.gui.commands.RunStateChange;
import reactor.core.publisher.Flux;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class NoOpExecutableAdapter extends AbstractExecutableAdapter implements ExecutableAdapter {


}
