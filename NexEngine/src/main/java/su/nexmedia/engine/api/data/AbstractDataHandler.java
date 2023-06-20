package su.nexmedia.engine.api.data;

import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.NexPlugin;
import su.nexmedia.engine.api.data.config.DataConfig;
import su.nexmedia.engine.api.data.connection.AbstractDataConnector;
import su.nexmedia.engine.api.data.sql.SQLColumn;
import su.nexmedia.engine.api.data.sql.SQLCondition;
import su.nexmedia.engine.api.data.sql.SQLValue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class AbstractDataHandler<P extends NexPlugin<P>> extends AbstractEmptyDataHandler<P> {
    private final AbstractEmptyDataHandler<P> handler;

    protected AbstractDataHandler(@NotNull P plugin) {
        this(plugin, new DataConfig(plugin.getConfig()));
    }

    protected AbstractDataHandler(@NotNull P plugin, @NotNull DataConfig config) {
        super(plugin);
        AbstractDataHandler<P> root = this;
        this.handler = switch (config.storageType) {
            case MONGODB -> new AbstractMongoDBDataHandler<P>(plugin, config) {
                @Override
                public void reload() {
                    root.reload();
                }

                @Override
                public void onSynchronize() {
                    root.onSynchronize();
                }

                @Override
                public void onSave() {
                    root.onSave();
                }

                @Override
                public void onPurge() {
                    root.onPurge();
                }
            };
            case MYSQL, SQLITE -> new AbstractSQLDataHandler<>(plugin, config) {
                @Override
                public void reload() {
                    root.reload();
                }

                @Override
                public void onSynchronize() {
                    root.onSynchronize();
                }

                @Override
                public void onSave() {
                    root.onSave();
                }

                @Override
                public void onPurge() {
                    root.onPurge();
                }
            };
        };
    }

    @Override
    protected void onLoad() {
        this.handler.onLoad();
    }

    @Override
    protected void onShutdown() {
        this.handler.onShutdown();
    }

    @NotNull
    public AbstractDataConnector getConnector() {
        return this.handler.getConnector();
    }

    @NotNull
    protected GsonBuilder registerAdapters(@NotNull GsonBuilder builder) {
        return this.handler.registerAdapters(builder);
    }

    @NotNull
    protected final Connection getConnection() throws SQLException {
        return this.handler.getConnection();
    }

    public void createTable(@NotNull String table, @NotNull List<SQLColumn> columns) {
        this.handler.createTable(table, columns);
    }

    public void renameTable(@NotNull String from, @NotNull String to) {
        this.handler.renameTable(from, to);
    }

    public void addColumn(@NotNull String table, @NotNull SQLValue... columns) {
        this.handler.addColumn(table, columns);
    }

    public void renameColumn(@NotNull String table, @NotNull SQLValue... columns) {
        this.handler.renameColumn(table, columns);
    }

    public void dropColumn(@NotNull String table, @NotNull SQLColumn... columns) {
        this.handler.dropColumn(table, columns);
    }

    public boolean hasColumn(@NotNull String table, @NotNull SQLColumn column) {
        return this.handler.hasColumn(table, column);
    }

    public void insert(@NotNull String table, @NotNull List<SQLValue> values) {
        this.handler.insert(table, values);
    }

    public void update(@NotNull String table, @NotNull List<SQLValue> values, @NotNull SQLCondition... conditions) {
        this.handler.update(table, values, conditions);
    }

    public void delete(@NotNull String table, @NotNull SQLCondition... conditions) {
        this.handler.delete(table, conditions);
    }

    public boolean contains(@NotNull String table, @NotNull SQLCondition... conditions) {
        return this.handler.contains(table, conditions);
    }

    public boolean contains(@NotNull String table, @NotNull List<SQLColumn> columns, @NotNull SQLCondition... conditions) {
        return this.handler.contains(table, columns, conditions);
    }

    @NotNull
    public <T> Optional<T> load(@NotNull String table, @NotNull Function<List<?>, T> function,
        @NotNull List<SQLColumn> columns,
        @NotNull List<SQLCondition> conditions) throws SQLException {
        return this.handler.load(table, function, columns, conditions);
    }

    @NotNull
    public <T> List<T> load(@NotNull String table, @NotNull Function<List<?>, T> dataFunction,
        @NotNull List<SQLColumn> columns,
        @NotNull List<SQLCondition> conditions,
        int amount) throws SQLException {
        return this.handler.load(table, dataFunction, columns, conditions, amount);
    }
}
