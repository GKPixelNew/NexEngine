package su.nexmedia.engine.api.data;

import org.jetbrains.annotations.NotNull;

public enum StorageType {

    MYSQL("MySQL"), SQLITE("SQLite"), MONGODB("MongoDB")
    ;

    private final String name;

    StorageType(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public String getName() {
        return this.name;
    }
}
