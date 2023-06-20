package su.nexmedia.engine.api.data.task;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.NexPlugin;
import su.nexmedia.engine.api.data.AbstractDataHandler;
import su.nexmedia.engine.api.data.AbstractEmptyDataHandler;
import su.nexmedia.engine.api.server.AbstractTask;

public class DataSynchronizationTask<P extends NexPlugin<P>> extends AbstractTask<P> {

    private final AbstractEmptyDataHandler<P> dataHandler;

    public DataSynchronizationTask(@NotNull AbstractEmptyDataHandler<P> dataHandler) {
        super(dataHandler.plugin(), dataHandler.getConfig().syncInterval, true);
        this.dataHandler = dataHandler;
    }

    @Override
    public void action() {
        this.dataHandler.onSynchronize();
    }
}
