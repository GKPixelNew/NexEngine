package su.nexmedia.engine.api.data;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCommandException;
import com.mongodb.MongoNamespace;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.NexPlugin;
import su.nexmedia.engine.api.data.config.DataConfig;
import su.nexmedia.engine.api.data.connection.AbstractDataConnector;
import su.nexmedia.engine.api.data.sql.SQLColumn;
import su.nexmedia.engine.api.data.sql.SQLCondition;
import su.nexmedia.engine.api.data.sql.SQLValue;
import su.nexmedia.engine.api.data.task.DataSaveTask;
import su.nexmedia.engine.api.data.task.DataSynchronizationTask;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public abstract class AbstractMongoDBDataHandler<P extends NexPlugin<P>> extends AbstractEmptyDataHandler<P> {
    protected final DataConfig config;
    private MongoClient client;
    private MongoDatabase database;

    private DataSynchronizationTask<P> synchronizationTask;
    private DataSaveTask<P> saveTask;

    protected AbstractMongoDBDataHandler(@NotNull P plugin, @NotNull DataConfig config) {
        super(plugin);

        this.config = config;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        if (this.config != null) {
            if (this.getConfig().purgeEnabled && this.getConfig().purgePeriod > 0) {
                this.onPurge();
            }

            ConnectionString connString = new ConnectionString(this.config.mongoConnectionString);
            CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).build()));
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(connString)
                    .retryWrites(true)
                    .codecRegistry(pojoCodecRegistry)
                    .build();
            this.client = MongoClients.create(settings);
            this.database = client.getDatabase(this.config.mongoDatabaseName);
        }
    }

    @Override
    protected void onShutdown() {
        super.onShutdown();
        if (this.client != null) {
            this.client.close();
        }
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
    public AbstractDataConnector getConnector() {
        return null;
    }

    @Nullable
    protected final Connection getConnection() {
        return null;
    }

    public void createTable(@NotNull String table, @NotNull List<SQLColumn> columns) {
        try {
            database.createCollection(table);
        } catch (MongoCommandException ignored) {
        } // When there are duplicate tables
    }

    public void renameTable(@NotNull String from, @NotNull String to) {
        database.getCollection(from).renameCollection(new MongoNamespace(database.getName(), to));
    }

    public void addColumn(@NotNull String table, @NotNull SQLValue... columns) {
        // We don't need this in MongoDB because it's schemaless
    }

    public void renameColumn(@NotNull String table, @NotNull SQLValue... columns) {
        // We don't need this in MongoDB because it's schemaless
    }

    public void dropColumn(@NotNull String table, @NotNull SQLColumn... columns) {
        // We don't need this in MongoDB because it's schemaless
    }

    public boolean hasColumn(@NotNull String table, @NotNull SQLColumn column) {
        // We don't need this in MongoDB because it's schemaless
        return true;
    }

    public void insert(@NotNull String table, @NotNull List<SQLValue> values) {
        Document document = new Document();
        for (SQLValue value : values) {
            document.append(value.getColumn().getName(), value.getConvertedValue());
        }
        database.getCollection(table).insertOne(document);
    }

    private Bson generateFilters(SQLCondition... conditions) {
        List<Bson> filters = new ArrayList<>();
        for (SQLCondition condition : conditions) {
            filters.add(switch (condition.getType()) {
                case EQUAL ->
                        Filters.eq(condition.getValue().getColumn().getName(), condition.getValue().getConvertedValue());
                case NOT_EQUAL ->
                        Filters.ne(condition.getValue().getColumn().getName(), condition.getValue().getConvertedValue());
                case GREATER ->
                        Filters.gt(condition.getValue().getColumn().getName(), condition.getValue().getConvertedValue());
                case SMALLER ->
                        Filters.lt(condition.getValue().getColumn().getName(), condition.getValue().getConvertedValue());
            });
        }
        return Filters.and(filters);
    }

    public void update(@NotNull String table, @NotNull List<SQLValue> values, @NotNull SQLCondition... conditions) {
        List<Bson> updates = new ArrayList<>();
        for (SQLValue value : values) {
            updates.add(Updates.set(value.getColumn().getName(), value.getConvertedValue()));
        }
        database.getCollection(table).updateMany(generateFilters(conditions), Updates.combine(updates));
    }

    public void delete(@NotNull String table, @NotNull SQLCondition... conditions) {
        database.getCollection(table).deleteMany(generateFilters(conditions));
    }

    public boolean contains(@NotNull String table, @NotNull SQLCondition... conditions) {
        return database.getCollection(table).countDocuments(generateFilters(conditions)) > 0;
    }

    public boolean contains(@NotNull String table, @NotNull List<SQLColumn> columns, @NotNull SQLCondition... conditions) {
        return database.getCollection(table).countDocuments(generateFilters(conditions)) > 0;
    }

    @NotNull
    public <T> Optional<T> load(@NotNull String table, @NotNull Function<Map<String, ?>, T> function,
                                @NotNull List<SQLColumn> columns,
                                @NotNull List<SQLCondition> conditions) throws SQLException {
        List<T> list = this.load(table, function, columns, conditions, 1);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @NotNull
    public <T> List<T> load(@NotNull String table, @NotNull Function<Map<String, ?>, T> dataFunction,
                            @NotNull List<SQLColumn> columns,
                            @NotNull List<SQLCondition> conditions,
                            int amount) throws SQLException {
        List<T> list = new ArrayList<>();
        FindIterable<Document> documents = database.getCollection(table).find(generateFilters(conditions.toArray(new SQLCondition[0])));
        for (Document document : documents) {
            list.add(dataFunction.apply(document));
        }
        return list;
    }
}
