package it.aretesoftware.jsonvdf;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author AreteS0ftware
 */
public class TestNode {

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
        VDFNode node = parser.parse(VDF_SAMPLE_TYPES).get("root_node");
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
        VDFNode root = parser.parse(VDF_SAMPLE_ARRAYS).get("root_node");
        // VDFNode
        VDFNode[] vdfValues = root.asArray("vdfValues");
        Assert.assertEquals(4, vdfValues.length);
        Assert.assertEquals(0.1f, vdfValues[0].asFloat(), 0f);
        Assert.assertTrue(vdfValues[1].asBoolean());
        Assert.assertEquals("Test!", vdfValues[2].asString());
        Assert.assertEquals(1, vdfValues[3].asLong());
        // String
        String[] stringValues = root.asStringArray("vdfValues");
        Assert.assertEquals(4, stringValues.length);
        Assert.assertEquals("0.1", stringValues[0]);
        Assert.assertEquals("true", stringValues[1]);
        Assert.assertEquals("Test!", stringValues[2]);
        Assert.assertEquals("1", stringValues[3]);
        // Double
        Double[] doubleValues = root.asDoubleArray("doubleValues");
        Assert.assertEquals(3, doubleValues.length);
        Assert.assertEquals(1000d, doubleValues[0], 0);
        Assert.assertEquals(0.1d, doubleValues[1], 0);
        Assert.assertEquals(-10d, doubleValues[2], 0);
        // Float
        Float[] floatValues = root.asFloatArray("doubleValues");
        Assert.assertEquals(3, floatValues.length);
        Assert.assertEquals(1000f, floatValues[0], 0);
        Assert.assertEquals(0.1f, floatValues[1], 0.001f);
        Assert.assertEquals(-10f, floatValues[2], 0);
        // Long
        Long[] longValues = root.asLongArray("longValues");
        Assert.assertEquals(3, longValues.length);
        Assert.assertEquals(1L, longValues[0], 0);
        Assert.assertEquals(+10L, longValues[1], 0);
        Assert.assertEquals(-100L, longValues[2], 0);
        // Int
        Integer[] intValues = root.asIntArray("longValues");
        Assert.assertEquals(3, intValues.length);
        Assert.assertEquals(1L, intValues[0], 0);
        Assert.assertEquals(+10L, intValues[1], 0);
        Assert.assertEquals(-100L, intValues[2], 0);
        // Short
        Short[] shortValues = root.asShortArray("longValues");
        Assert.assertEquals(3, shortValues.length);
        Assert.assertEquals(1, shortValues[0], 0);
        Assert.assertEquals(+10, shortValues[1], 0);
        Assert.assertEquals(-100, shortValues[2], 0);
        // Byte
        Byte[] byteValues = root.asByteArray("longValues");
        Assert.assertEquals(3, byteValues.length);
        Assert.assertEquals(1, byteValues[0], 0);
        Assert.assertEquals(+10, byteValues[1], 0);
        Assert.assertEquals(-100, byteValues[2], 0);
        // Char
        Character[] charValues = root.asCharArray("charValues");
        Assert.assertEquals(3, charValues.length);
        Assert.assertEquals('a', charValues[0], 0);
        Assert.assertEquals('b', charValues[1], 0);
        Assert.assertEquals('c', charValues[2], 0);
        // Long -> Char
        charValues = root.asCharArray("longValues");
        Assert.assertEquals(3, charValues.length);
        Assert.assertEquals(1, charValues[0], 0);
        Assert.assertEquals(10, charValues[1], 0);
        Assert.assertEquals(65436, charValues[2], 0);
        // Boolean
        Boolean[] booleanValues = root.asBooleanArray("booleanValues");
        Assert.assertTrue(booleanValues[0]);
        Assert.assertFalse(booleanValues[1]);
        // Boolean -> String
        String[] booleanToStringValues = root.asStringArray("booleanValues");
        Assert.assertEquals("true", booleanToStringValues[0]);
        Assert.assertEquals("false", booleanToStringValues[1]);
    }

    @Test
    public void testSample() {
        VDFNode root = parser.parse(VDF_SAMPLE).get("root_node");
        // first_sub_node
        VDFNode subNode = root.get("first_sub_node");
        VDFNode node = subNode.get("first");
        Assert.assertEquals("first", node.name());
        Assert.assertEquals("value1", node.asString());
        node = subNode.get(1);
        Assert.assertEquals("second", node.name());
        Assert.assertEquals("value2", node.asString());
        // second_sub_node
        subNode = root.get("second_sub_node");
        node = subNode.get(1);
        Assert.assertEquals("third", node.name());
        Assert.assertEquals("value3", node.asString());
        // third_sub_node
        node = subNode.getChild("third_sub_node");
        Assert.assertEquals("fourth", node.name());
        Assert.assertEquals("value4", node.asString());
    }

    @Test
    public void testSampleMultimap() {
        VDFNode root = parser.parse(VDF_SAMPLE_MULTIMAP).get("root_node");
        // sub_node
        VDFNode[] nodes = root.asArray("sub_node");
        Assert.assertEquals(2, nodes.length);
        // first
        VDFNode subNode = nodes[0];
        VDFNode node = subNode.get("key");
        Assert.assertEquals("key", node.name());
        Assert.assertEquals("value1", node.asString());
        node = node.next();
        Assert.assertEquals("key", node.name());
        Assert.assertEquals("value2", node.asString());
        // second
        subNode = nodes[1];
        node = subNode.get(0);
        Assert.assertEquals("key", node.name());
        Assert.assertEquals("value3", node.asString());
        node = subNode.get("key", 1);
        Assert.assertEquals("value4", node.asString());
    }

}
