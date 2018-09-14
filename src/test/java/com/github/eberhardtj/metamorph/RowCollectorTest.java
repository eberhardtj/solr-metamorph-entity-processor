package com.github.eberhardtj.metamorph;

import org.junit.Test;
import org.metafacture.framework.MetafactureException;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RowCollectorTest {

    @Test
    public void singleValue() {
        RowCollector collector = new RowCollector();
        collector.startRecord("1");
        collector.literal("name", "name");
        collector.literal("otherName", "otherName");
        collector.endRecord();

        Map<String,Object> row = collector.getRow();

        assertThat(row, hasEntry("name", "name"));
        assertThat(row, hasEntry("otherName", "otherName"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void valueList() {
        RowCollector collector = new RowCollector();
        collector.startRecord("1");
        collector.literal("name", "name1");
        collector.literal("name", "name2");
        collector.endRecord();

        Map<String,Object> row = collector.getRow();

        assertThat(row, hasKey("name"));
        assertThat(row.get("name"), is(instanceOf(List.class)));

        List<Object> names = (List<Object>) row.get("name");
        assertThat(names, hasItems("name1", "name2"));
    }

    @Test
    public void overwriteRowWhenNewRecordStarts() {
        RowCollector collector = new RowCollector();
        collector.startRecord("1");
        collector.literal("name","a");
        collector.literal("otherName", "a");
        collector.endRecord();

        collector.startRecord("2");
        collector.literal("name", "b");
        collector.endRecord();

        Map<String,Object> row = collector.getRow();

        assertThat(row.size(), equalTo(1));
        assertThat(row.get("name"), equalTo("b"));
    }

    @Test(expected = MetafactureException.class)
    public void complainAboutEntity() {
        RowCollector collector = new RowCollector();
        collector.startRecord("1");
        collector.startEntity("entity");
        collector.literal("name", "name1");
        collector.literal("name", "name2");
        collector.endEntity();
        collector.endRecord();
    }
}