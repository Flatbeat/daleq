package de.brands4friends.daleq.internal.structure;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.dbunit.dataset.datatype.DataType;
import org.junit.Before;
import org.junit.Test;

import de.brands4friends.daleq.FieldDef;
import de.brands4friends.daleq.TableDef;

public class TableStructureFactoryTest {

    private TableStructureFactory factory;

    @TableDef("MY_TABLE")
    static class MyTable {
        public static final FieldDef ID = FieldDef.fd(DataType.INTEGER);
    }

    @Before
    public void setUp() throws Exception {
        factory = new TableStructureFactory();
    }

    @Test
    public void createOfMyTable_should_returnTableStructure() {

        final TableStructure tableStructure = factory.create(MyTable.class);
        final TableStructure expected = new TableStructure("MY_TABLE",
                new FieldStructure("ID", DataType.INTEGER, null, MyTable.ID));

        assertThat(tableStructure, is(expected));
    }

    static class WithoutAnnotation {
        public static final FieldDef ID = FieldDef.fd(DataType.INTEGER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithoutAnnotation_should_fail() {
        factory.create(WithoutAnnotation.class);
    }

    @TableDef("MY_TABLE")
    static class WithoutPropertyDefs {

    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithoutPropertyDefs_should_fail() {
        factory.create(WithoutPropertyDefs.class);
    }
}
