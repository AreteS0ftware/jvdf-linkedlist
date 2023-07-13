package it.aretesoftware.jsonvdf;

import org.junit.Assert;
import org.junit.Test;

public class TestWriter extends BaseTest {

    private final VDFWriter writer = new VDFWriter();
    private final VDFPreprocessor preprocessor = new VDFPreprocessor();
    private final String sample = getFileContents("resources/sample.txt");
    private final String sample_multimap = getFileContents("resources/sample_multimap.txt");
    private final String sample_types = getFileContents("resources/sample_types.txt");
    private final String sample_arrays = getFileContents("resources/sample_arrays.txt");

    @Test
    public void testSample() {
        writer.writeNodeStart("root_node");
            writer.writeNodeStart("first_sub_node");
                writer.writeValue("first", "value1");
                writer.writeValue("second", "value2");
            writer.writeNodeEnd();
            writer.writeNodeStart("second_sub_node");
                writer.writeNodeStart("third_sub_node");
                    writer.writeValue("fourth", "value4");
                writer.writeNodeEnd();
                writer.writeValue("third", "value3");
            writer.writeNodeEnd();
        writer.writeNodeEnd();
        String first = preprocessor.process(writer.toVDF());
        String second = preprocessor.process(sample);
        Assert.assertEquals(first, second);
    }

}
