package it.aretesoftware.jvdf;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author AreteS0ftware
 */
public class TestWriter {

    private final VDFParser parser = new VDFParser();

    // Maven surefire bug can't include resources when forking is disabled
    private static final String VDF_SAMPLE = "\"root_node\"\n" +
            "{\n" +
            "    \"first_sub_node\"\n" +
            "    {\n" +
            "        \"first\"     \"value1\"\n" +
            "        \"second\"    \"value2\"\n" +
            "    }\n" +
            "    \"second_sub_node\"\n" +
            "    {\n" +
            "        \"third_sub_node\"\n" +
            "        {\n" +
            "            \"fourth\"    \"value4\"\n" +
            "        }\n" +
            "        \"third\"     \"value3\"\n" +
            "    }\n" +
            "}";
    private static final String VDF_SAMPLE_MULTIMAP = "\"root_node\"\n" +
            "{\n" +
            "    \"sub_node\"\n" +
            "    {\n" +
            "        \"key\"       \"value1\"\n" +
            "        \"key\"       \"value2\"\n" +
            "    }\n" +
            "    \"sub_node\"\n" +
            "    {\n" +
            "        \"key\"       \"value3\"\n" +
            "        \"key\"       \"value4\"\n" +
            "    }\n" +
            "}";

    @Test
    public void testSample() {

    }

    @Test
    public void testSampleMultimap() {
        VDFNode node = parser.parse(VDF_SAMPLE);
        System.out.println(node);
    }

}
