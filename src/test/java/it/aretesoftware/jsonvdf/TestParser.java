package it.aretesoftware.jsonvdf;

import org.junit.Assert;
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

        Assert.assertEquals(123456, node.getLong("long"));
        Assert.assertEquals(123456, node.getInt("long"));
        Assert.assertEquals(10E2, node.getDouble("double"), 0f);
        Assert.assertEquals(1000, node.getFloat("double"), 0f);
        Assert.assertEquals(123.456d, node.getDouble("float"), 0f);
        Assert.assertEquals(123.456f, node.getFloat("float"), 0f);
        Assert.assertTrue(node.getBoolean("boolean"));
        Assert.assertEquals("true", node.getString("boolean"));
        Assert.assertEquals("Test!", node.getString("string"));
        Assert.assertEquals('a', node.getChar("char"));
        Assert.assertEquals("a", node.getString("char"));
    }

    @Test
    public void testSampleArrays() {
        VDFNode node = parser.parse(VDF_SAMPLE_ARRAYS);

        VDFNode[] vdfValues = node.asArray("vdfValues");
        Assert.assertEquals(4, vdfValues.length);
        Assert.assertEquals(0.1f, vdfValues[0].asFloat(), 0f);
        Assert.assertTrue(vdfValues[1].asBoolean());
        Assert.assertEquals("Test!", vdfValues[2].asString());
        Assert.assertEquals(1, vdfValues[3].asLong());

        String[] stringValues = node.asStringArray("vdfValues");
        Assert.assertEquals(4, stringValues.length);
        Assert.assertEquals("0.1", stringValues[0]);
        Assert.assertEquals("true", stringValues[1]);
        Assert.assertEquals("Test!", stringValues[2]);
        Assert.assertEquals("1", stringValues[3]);

        Double[] doubleValues = node.asDoubleArray("doubleValues");
        Assert.assertEquals(3, doubleValues.length);
        Assert.assertEquals(1000d, doubleValues[0], 0);
        Assert.assertEquals(0.1d, doubleValues[1], 0);
        Assert.assertEquals(-10d, doubleValues[2], 0);

        Float[] floatValues = node.asFloatArray("doubleValues");
        Assert.assertEquals(3, floatValues.length);
        Assert.assertEquals(1000f, floatValues[0], 0);
        Assert.assertEquals(0.1f, floatValues[1], 0.001f);
        Assert.assertEquals(-10f, floatValues[2], 0);

        Long[] longValues = node.asLongArray("longValues");
        Assert.assertEquals(3, longValues.length);
        Assert.assertEquals(1L, longValues[0], 0);
        Assert.assertEquals(+10L, longValues[1], 0);
        Assert.assertEquals(-100L, longValues[2], 0);

        Integer[] intValues = node.asIntArray("longValues");
        Assert.assertEquals(3, intValues.length);
        Assert.assertEquals(1L, intValues[0], 0);
        Assert.assertEquals(+10L, intValues[1], 0);
        Assert.assertEquals(-100L, intValues[2], 0);

        Short[] shortValues = node.asShortArray("longValues");
        Assert.assertEquals(3, shortValues.length);
        Assert.assertEquals(1, shortValues[0], 0);
        Assert.assertEquals(+10, shortValues[1], 0);
        Assert.assertEquals(-100, shortValues[2], 0);

        Byte[] byteValues = node.asByteArray("longValues");
        Assert.assertEquals(3, byteValues.length);
        Assert.assertEquals(1, byteValues[0], 0);
        Assert.assertEquals(+10, byteValues[1], 0);
        Assert.assertEquals(-100, byteValues[2], 0);

        Character[] charValues = node.asCharArray("charValues");
        Assert.assertEquals(3, charValues.length);
        Assert.assertEquals('a', charValues[0], 0);
        Assert.assertEquals('b', charValues[1], 0);
        Assert.assertEquals('c', charValues[2], 0);

        charValues = node.asCharArray("longValues");
        Assert.assertEquals(3, charValues.length);
        Assert.assertEquals(1, charValues[0], 0);
        Assert.assertEquals(10, charValues[1], 0);
        Assert.assertEquals(65436, charValues[2], 0);
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
