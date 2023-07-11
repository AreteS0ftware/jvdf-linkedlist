package it.aretesoftware.jsonvdf;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author AreteS0ftware
 */
public class TestToVDF {

    private final VDFParser parser = new VDFParser();
    private final VDFPreprocessor preprocessor = new VDFPreprocessor();

    // Maven surefire bug can't include resources when forking is disabled
    private static final String VDF_SAMPLE_TYPES = "\"root_node\"\n" +
            "{\n" +
            "    \"long\"     \"123456\"\n" +
            "    \"double\"    \"10E2\"\n" +
            "    \"float\"    \"123.456\"\n" +
            "    \"boolean\"    \"true\"\n" +
            "    \"string\"    \"Test!\"\n" +
            "    \"char\"    \"a\"\n" +
            "}";
    private static final String VDF_SAMPLE_ARRAYS = "\"root_node\"\n" +
            "{\n" +
            "    \"vdfValues\"     \"0.1\"\n" +
            "    \"vdfValues\"    \"true\"\n" +
            "    \"vdfValues\"    \"Test!\"\n" +
            "    \"vdfValues\"    \"1\"\n" +
            "    \"doubleValues\"    \"10E2\"\n" +
            "    \"doubleValues\"    \"0.1\"\n" +
            "    \"doubleValues\"    \"-10\"\n" +
            "    \"longValues\"    \"1\"\n" +
            "    \"longValues\"    \"+10\"\n" +
            "    \"longValues\"    \"-100\"\n" +
            "    \"charValues\"    \"a\"\n" +
            "    \"charValues\"    \"b\"\n" +
            "    \"charValues\"    \"c\"\n" +
            "    \"booleanValues\"    \"true\"\n" +
            "    \"booleanValues\"    \"false\"\n" +
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
    public void testSampleTypes() {
        test(VDF_SAMPLE_TYPES);
    }

    @Test
    public void testSampleArrays() {
        test(VDF_SAMPLE_ARRAYS);
    }

    @Test
    public void testSample() {
        test(VDF_SAMPLE);
    }

    @Test
    public void testSampleMultimap() {
        test(VDF_SAMPLE_MULTIMAP);
    }

    private void test(String vdfString) {
        VDFNode node = parser.parse(vdfString);
        String first = preprocessor.process(node.toVDF());
        String second = preprocessor.process(vdfString);
        Assert.assertEquals(first, second);
    }

}
