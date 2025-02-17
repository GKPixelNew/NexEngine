package su.nexmedia.engine.api.data.sql.executor;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.connection.AbstractDataConnector;
import su.nexmedia.engine.api.data.sql.*;
import su.nexmedia.engine.api.data.sql.column.ColumnType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class SelectQueryExecutor<T> extends SQLExecutor<List<T>> {

    private final List<SQLColumn> columns;
    private final List<SQLCondition> wheres;
    private final Function<Map<String, ?>, T> dataFunction;
    private int amount;

    private SelectQueryExecutor(@NotNull String table, @NotNull Function<Map<String, ?>, T> dataFunction) {
        super(table);
        this.columns = new ArrayList<>();
        this.wheres = new ArrayList<>();
        this.dataFunction = dataFunction;
        this.amount = -1;
    }

    @NotNull
    public static <T> SelectQueryExecutor<T> builder(@NotNull String table, @NotNull Function<Map<String, ?>, T> dataFunction) {
        return new SelectQueryExecutor<>(table, dataFunction);
    }

    @NotNull
    public SelectQueryExecutor<T> all() {
        return this.columns(new SQLColumn("*", ColumnType.STRING, -1));
    }

    @NotNull
    public SelectQueryExecutor<T> columns(@NotNull SQLColumn... columns) {
        return this.columns(Arrays.asList(columns));
    }

    @NotNull
    public SelectQueryExecutor<T> columns(@NotNull List<SQLColumn> columns) {
        if (columns.isEmpty()) return this.all();

        this.columns.clear();
        this.columns.addAll(columns);
        return this;
    }

    @NotNull
    public SelectQueryExecutor<T> where(@NotNull SQLCondition... wheres) {
        return this.where(Arrays.asList(wheres));
    }

    @NotNull
    public SelectQueryExecutor<T> where(@NotNull List<SQLCondition> wheres) {
        this.wheres.clear();
        this.wheres.addAll(wheres);
        return this;
    }

    @NotNull
    public SelectQueryExecutor<T> amount(int amount) {
        this.amount = amount;
        return this;
    }

    @Override
    @NotNull
    public List<T> execute(@NotNull AbstractDataConnector connector) {
        if (this.columns.isEmpty()) return Collections.emptyList();

        String columns = this.columns.stream().map(SQLColumn::getNameEscaped).collect(Collectors.joining(","));
        String wheres = this.wheres.stream().map(where -> where.getValue().getColumn().getNameEscaped() + " " + where.getType().getOperator() + " ?")
            .collect(Collectors.joining(" AND "));
        String sql = "SELECT " + columns + " FROM " + this.table + (wheres.isEmpty() ? "" : " WHERE " + wheres);

        // List<String> values2 = this.columns.stream().map(SQLColumn::getName).toList();
        List<String> whers2 = this.wheres.stream().map(SQLCondition::getValue).map(SQLValue::getValue).toList();

        return SQLQueries.executeQuery(connector, sql, whers2, this.dataFunction, this.amount);
    }
}
