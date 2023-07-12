package it.aretesoftware.jsonvdf;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;

public class TestItemSchema {

    @Test
    public void testItemsGame() {
        String contents = getFileContents("resources/items_game.txt");
        VDFParser parser = new VDFParser();

        long start, end;
        VDFNode root = null;
        for (int counter = 1; counter <= 10; counter++) {
            start = System.nanoTime();
            root = parser.parse(contents);
            end = System.nanoTime();
            System.out.println(counter + ") Time to parse: " + ((end - start) / 1000000f) + " milliseconds");
        }

        VDFPreprocessor preprocessor = new VDFPreprocessor();
        String processed1 = preprocessor.process(contents);
        String processed2 = preprocessor.process(root.toVDF());
        Assert.assertEquals(processed1, processed2);
    }

    private String getFileContents(String filePath) {
        try {
            FileReader in = new FileReader(filePath);
            BufferedReader br = new BufferedReader(in);

            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
            in.close();

            return builder.toString();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
