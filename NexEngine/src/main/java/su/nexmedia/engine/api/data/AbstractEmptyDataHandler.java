package su.nexmedia.engine.api.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.NexPlugin;
import su.nexmedia.engine.api.data.config.DataConfig;
import su.nexmedia.engine.api.data.connection.AbstractDataConnector;
import su.nexmedia.engine.api.data.serialize.ItemStackSerializer;
import su.nexmedia.engine.api.data.sql.SQLColumn;
import su.nexmedia.engine.api.data.sql.SQLCondition;
import su.nexmedia.engine.api.data.sql.SQLValue;
import su.nexmedia.engine.api.data.task.DataSaveTask;
import su.nexmedia.engine.api.data.task.DataSynchronizationTask;
import su.nexmedia.engine.api.manager.AbstractManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractEmptyDataHandler<P extends NexPlugin<P>> extends AbstractManager<P> {
    protected final DataConfig config;
    protected Gson gson;
    private DataSynchronizationTask<P> synchronizationTask;
    private DataSaveTask<P> saveTask;

    protected AbstractEmptyDataHandler(@NotNull P plugin) {
        this(plugin, new DataConfig(plugin.getConfig()));
    }

    protected AbstractEmptyDataHandler(@NotNull P plugin, @NotNull DataConfig config) {
        super(plugin);

        this.config = config;
    }

    public abstract void onSynchronize();

    public abstract void onSave();

    public abstract void onPurge();

    @Override
    protected void onLoad() {
        this.gson = this.registerAdapters(new GsonBuilder().setPrettyPrinting()).create();

        if (this.config != null) {
            if (this.getConfig().saveInterval > 0) {
                if (this.saveTask == null) {
                    this.saveTask = new DataSaveTask<>(this);
                    this.saveTask.start();
                    this.plugin.info("Enabled data auto-save with " + config.saveInterval + " seconds interval.");
                }
            }

            if (this.getConfig().syncInterval > 0) {
                if (this.getDataType() != StorageType.SQLITE) {
                    if (this.synchronizationTask == null) {
                        this.synchronizationTask = new DataSynchronizationTask<>(this);
                        this.synchronizationTask.start();
                        this.plugin.info("Enabled data synchronization with " + config.syncInterval + " seconds interval.");
                    }
                } else {
                    this.plugin.warn("Data synchronization is useless for local databases (SQLite). It will be disabled.");
                }
            }

            if (this.getConfig().purgeEnabled && this.getConfig().purgePeriod > 0) {
                this.onPurge();
            }
        }
    }

    @Override
    protected void onShutdown() {
        if (this.synchronizationTask != null) {
            this.synchronizationTask.stop();
            this.synchronizationTask = null;
        }
        if (this.saveTask != null) {
            this.saveTask.stop();
            this.saveTask = null;
        }
        this.onSynchronize();
        this.onSave();
    }

    @NotNull
    public DataConfig getConfig() {
        return this.config;
    }

    @NotNull
    public StorageType getDataType() {
        return this.getConfig().storageType;
    }

    @NotNull
    public String getTablePrefix() {
        if (this.getConfig().tablePrefix.isEmpty()) {
            return this.plugin.getName().replace(" ", "_").toLowerCase();
        }
        return this.getConfig().tablePrefix;
    }

    @Nullable
    public abstract AbstractDataConnector getConnector();

    @NotNull
    protected GsonBuilder registerAdapters(@NotNull GsonBuilder builder) {
        return builder.registerTypeAdapter(ItemStack.class, new ItemStackSerializer());
    }

    @Nullable
    protected abstract Connection getConnection() throws SQLException;

    public abstract void createTable(@NotNull String table, @NotNull List<SQLColumn> columns);

    public abstract void renameTable(@NotNull String from, @NotNull String to);

    public abstract void addColumn(@NotNull String table, @NotNull SQLValue... columns);

    public abstract void renameColumn(@NotNull String table, @NotNull SQLValue... columns);

    public abstract void dropColumn(@NotNull String table, @NotNull SQLColumn... columns);

    public abstract boolean hasColumn(@NotNull String table, @NotNull SQLColumn column);

    public abstract void insert(@NotNull String table, @NotNull List<SQLValue> values);

    public abstract void update(@NotNull String table, @NotNull List<SQLValue> values, @NotNull SQLCondition... conditions);

    public abstract void delete(@NotNull String table, @NotNull SQLCondition... conditions);

    public abstract boolean contains(@NotNull String table, @NotNull SQLCondition... conditions);

    public abstract boolean contains(@NotNull String table, @NotNull List<SQLColumn> columns, @NotNull SQLCondition... conditions);

    @NotNull
    public abstract <T> Optional<T> load(@NotNull String table, @NotNull Function<Map<String, ?>, T> function,
                                         @NotNull List<SQLColumn> columns,
                                         @NotNull List<SQLCondition> conditions) throws SQLException;

    @NotNull
    public abstract <T> List<T> load(@NotNull String table, @NotNull Function<Map<String, ?>, T> dataFunction,
                                     @NotNull List<SQLColumn> columns,
                                     @NotNull List<SQLCondition> conditions,
                                     int amount) throws SQLException;
}
