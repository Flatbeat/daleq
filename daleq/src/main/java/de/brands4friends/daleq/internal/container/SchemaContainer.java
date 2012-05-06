package de.brands4friends.daleq.internal.container;

import java.util.List;

import com.google.common.base.Objects;

public class SchemaContainer {

    private final List<TableContainer> tables;

    public SchemaContainer(final List<TableContainer> tables) {
        this.tables = tables;
    }

    public List<TableContainer> getTables() {
        return tables;
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof SchemaContainer) {
            final SchemaContainer that = (SchemaContainer) obj;

            return Objects.equal(tables, that.tables);
        }

        return false;
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(tables);
    }
}