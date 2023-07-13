package it.aretesoftware.jsonvdf;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author AreteS0ftware
 */
public class TestToVDF extends BaseTest {

    private final VDFParser parser = new VDFParser();
    private final VDFPreprocessor preprocessor = new VDFPreprocessor();
    private final String sample = getFileContents("resources/sample.txt");
    private final String sample_multimap = getFileContents("resources/sample_multimap.txt");
    private final String sample_types = getFileContents("resources/sample_types.txt");
    private final String sample_arrays = getFileContents("resources/sample_arrays.txt");

    @Test
    public void testSampleTypes() {
        test(sample_types);
    }

    @Test
    public void testSampleArrays() {
        test(sample_arrays);
    }

    @Test
    public void testSample() {
        test(sample);
    }

    @Test
    public void testSampleMultimap() {
        test(sample_multimap);
    }

    private void test(String vdfString) {
        VDFNode node = parser.parse(vdfString);
        String first = preprocessor.process(node.toVDF());
        String second = preprocessor.process(vdfString);
        Assert.assertEquals(first, second);
    }

}
