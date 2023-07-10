package it.aretesoftware.jsonvdf;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * @author AreteS0ftware
 */
public class TestNode {

    private final VDFParser parser = new VDFParser();
    private final String sample_types = getFileContents("resources/sample_types.txt");
    private final String sample_arrays = getFileContents("resources/sample_arrays.txt");
    private final String sample = getFileContents("resources/sample.txt");
    private final String sample_multimap = getFileContents("resources/sample_multimap.txt");

    @Test
    public void testSampleTypes() {
        VDFNode node = parser.parse(sample_types).get("root_node");
        Assert.assertEquals(123456, node.getLong("long"));
        Assert.assertEquals(123456, node.getInt("long"));
        Assert.assertEquals(100, node.getInt("int"));
        Assert.assertEquals(100, node.getLong("int"));
        Assert.assertEquals(100, node.getShort("int"));
        Assert.assertEquals(100, node.getByte("int"));
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
        VDFNode root = parser.parse(sample_arrays).get("root_node");
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
        VDFNode root = parser.parse(sample).get("root_node");
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
        VDFNode root = parser.parse(sample_multimap).get("root_node");
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

    @Test
    public void testDefaultValue() {
        VDFNode node = new VDFNode("");
        Assert.assertEquals("defaultValue", node.getString("", "defaultValue"));
        Assert.assertEquals(10f, node.getFloat("", 10f), 0);
        Assert.assertEquals(10d, node.getDouble("", 10d), 0);
        Assert.assertEquals(10, node.getLong("", 10), 0);
        Assert.assertEquals(10, node.getInt("", 10), 0);
        Assert.assertTrue(node.getBoolean("", true));
        Assert.assertEquals(10, node.getByte("", (byte) 10), 0);
        Assert.assertEquals(10, node.getShort("", (short) 10), 0);
        Assert.assertEquals('a', node.getChar("", 'a'));

        Assert.assertEquals("defaultValue", node.getStringOfIndex("", 1, "defaultValue"));
        Assert.assertEquals(10f, node.getFloatOfIndex("", 1, 10f), 0);
        Assert.assertEquals(10d, node.getDoubleOfIndex("",  1, 10d), 0);
        Assert.assertEquals(10L, node.getLongOfIndex("", 1, 10L), 0);
        Assert.assertEquals(10, node.getIntOfIndex("", 1, 10), 0);
        Assert.assertTrue(node.getBooleanOfIndex("", 1, true));
        Assert.assertEquals(10, node.getByteOfIndex("", 1, (byte) 10), 0);
        Assert.assertEquals(10, node.getShortOfIndex("", 1, (short) 10), 0);
        Assert.assertEquals('a', node.getCharOfIndex("", 1, 'a'));
    }

    private String getFileContents(String filePath) {
        try {
            FileReader in = new FileReader(filePath);
            BufferedReader br = new BufferedReader(in);

            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            in.close();

            return builder.toString();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
