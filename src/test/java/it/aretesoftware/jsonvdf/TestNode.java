package it.aretesoftware.jsonvdf;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author AreteS0ftware
 */
public class TestNode extends BaseTest {

    private final VDFParser parser = new VDFParser();
    private final VDFPreprocessor preprocessor = new VDFPreprocessor();
    private final String sample = getFileContents("resources/sample.txt");
    private final String sample_multimap = getFileContents("resources/sample_multimap.txt");
    private final String sample_types = getFileContents("resources/sample_types.txt");
    private final String sample_arrays = getFileContents("resources/sample_arrays.txt");

    @Test
    public void testSize() {
        VDFNode node = parser.parse(sample).get("root_node");
        Assert.assertTrue(node.notEmpty());
        Assert.assertFalse(node.isEmpty());
        Assert.assertEquals(2, node.size);
        Assert.assertEquals(1, node.sizeOf("first_sub_node"));

        node = new VDFNode();
        Assert.assertTrue(node.isEmpty());
        Assert.assertFalse(node.notEmpty());
        Assert.assertEquals(0, node.size);
        Assert.assertEquals(0, node.sizeOf(""));
    }

    @Test
    public void testHas() {
        VDFNode node = parser.parse(sample);
        Assert.assertTrue(node.isNull());
        Assert.assertFalse(node.hasParent());
        Assert.assertNotNull(node.child());

        node = node.get("root_node");
        Assert.assertTrue(node.isNull());
        Assert.assertTrue(node.hasParent());
        Assert.assertTrue(node.hasChild("first_sub_node"));

        node = node.get("first_sub_node").child();
        Assert.assertFalse(node.isNull());
        Assert.assertNotNull(node.parent());
        Assert.assertNotNull(node.next());
        Assert.assertNull(node.prev());
        Assert.assertNull(node.child());

        node = node.next();
        Assert.assertFalse(node.isNull());
        Assert.assertNotNull(node.parent());
        Assert.assertNotNull(node.prev());
        Assert.assertNull(node.next());
        Assert.assertNull(node.child());
    }

    @Test
    public void testRequire() {
        VDFNode node = parser.parse(sample).get("root_node");
        Assert.assertEquals("first_sub_node", node.require("first_sub_node").name());
        Assert.assertEquals("first_sub_node", node.require(0).name());
        Assert.assertTrue("second_sub_node", node.has("second_sub_node"));
        Assert.assertFalse("", node.has(""));
    }

    @Test
    public void testAddChild() {
        VDFNode node = parser.parse(sample).get("root_node");

        VDFNode firstSubNode = node.get("first_sub_node");
        VDFNode child = new VDFNode("value5");
        child.name = "fifth";
        firstSubNode.addChild(child);
        Assert.assertEquals(3, firstSubNode.size);
        Assert.assertEquals(firstSubNode.get("fifth").prev(), firstSubNode.get("second"));

        node.addChild("fourth_sub_node", new VDFNode());
        Assert.assertEquals(3, node.size);
    }

    @Test
    public void testRemove() {
        VDFNode node = parser.parse(sample).get("root_node");
        VDFNode firstSubNode = node.get("first_sub_node");
        Assert.assertEquals("first", firstSubNode.remove(0).name());
        Assert.assertEquals(1, firstSubNode.size);
        Assert.assertEquals("second", firstSubNode.remove("second").name());
        Assert.assertEquals(0, firstSubNode.size);

        VDFNode secondSubNode = node.get("second_sub_node");
        secondSubNode.remove();
        Assert.assertEquals(1, node.size);
    }

    @Test
    public void testSet() {
        VDFNode node = parser.parse(sample).get("root_node");
        node.set("value");
        Assert.assertEquals("value", node.asString());
        Assert.assertFalse(node.isNull());

        VDFNode firstSubNode = node.get("first_sub_node");
        firstSubNode.get("first").set("value");
        Assert.assertEquals("value", firstSubNode.get("first").asString());
    }

    @Test
    public void testIterator() {
        VDFNode node = parser.parse(sample).get("root_node");
        testIterator(node.iterator(), "first_sub_node", "second_sub_node");
        testIterator(node.iterator("first_sub_node"), "first", "second");
        testIterator(node.iterator("second_sub_node"), "third_sub_node", "third");
        testIterator(node.get("second_sub_node").iterator("third_sub_node"), "fourth", "");
    }

    private void testIterator(VDFNode.VDFIterator iterator, String firstName, String secondName) {
        int index = 0;
        while (iterator.hasNext()) {
            VDFNode child = iterator.next();
            switch (index) {
                case 0:
                    Assert.assertEquals(firstName, child.name());
                    break;
                case 1:
                    Assert.assertEquals(secondName, child.name());
            }
            index++;
        }
    }

    @Test
    public void testToVDF() {
        testToVDF(sample_types);
        testToVDF(sample_arrays);
        testToVDF(sample);
        testToVDF(sample_multimap);
    }

    private void testToVDF(String vdfString) {
        VDFNode node = parser.parse(vdfString);
        String first = preprocessor.process(node.toVDF());
        String second = preprocessor.process(vdfString);
        Assert.assertEquals(first, second);
    }

}
