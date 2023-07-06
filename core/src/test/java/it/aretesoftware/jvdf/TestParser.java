package it.aretesoftware.jvdf;

import org.junit.Test;

import java.util.Arrays;

/**
 * @author AreteS0ftware
 */
public class TestParser {

    private final VDFParser parser = new VDFParser();

    // Maven surefire bug can't include resources when forking is disabled
    private static final String VDF_SAMPLE_TYPES = "\"root_node\"\n" +
            "{\n" +
            "    \"long\"     \"123456\"\n" +
            "    \"double\"    \"10E2\"\n" +
            "    \"float\"    \"987.654\"\n" +
            "}";
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
            "}" +
            "\"sneed\" \n" +
            "{\n" +
            "   \"feed\"    \"seed\"" +
            "}";
    private static final String VDF_SAMPLE_MULTIMAP = "\"root_node\"\n" +
            "{\n" +
            "    \"sub_node\"\n" +
            "    {\n" +
            "        \"key\"       \"value1\"\n" +
            "        \"key\"       \"value2\"\n" +
            "        \"long\"       \"12345\"\n" +
            "    }\n" +
            "    \"sub_node\"\n" +
            "    {\n" +
            "        \"key\"       \"value3\"\n" +
            "        \"key\"       \"value4\"\n" +
            "    }\n" +
            "}";
    
    @Test
    public void testSampleTypes() {
        VDFNode node = parser.parse(VDF_SAMPLE_TYPES);
        System.out.println(node);
    }

    @Test
    public void testSample() {
        VDFNode node = parser.parse(VDF_SAMPLE);
        System.out.println(node);
    }

    @Test
    public void testSampleMultimap() {
        VDFNode node = parser.parse(VDF_SAMPLE_MULTIMAP);
        System.out.println(Arrays.toString(node.asArray("sub_node")));
    }

}
