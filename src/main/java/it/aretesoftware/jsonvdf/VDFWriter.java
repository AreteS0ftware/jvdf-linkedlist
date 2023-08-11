/*
Copyright 2023 Arete

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package it.aretesoftware.jsonvdf;

public class VDFWriter {

    private static final String SPACE = " ";
    private static final String WHITESPACE = "    ";
    private static final String NEWLINE = "\n";
    private static final String QUOTES = "\"";
    private static final String NODE_START = "{";
    private static final String NODE_END = "}";

    private final StringBuilder builder, whitespace;

    public VDFWriter() {
        this.builder = new StringBuilder();
        this.whitespace = new StringBuilder();
    }


    public VDFWriter writeNodeStart(String name) {
        builder.append(whitespace)
                .append(QUOTES).append(name).append(QUOTES)
                .append(NEWLINE)
                .append(whitespace).append(NODE_START).append(NEWLINE);
        whitespace.append(WHITESPACE);
        return this;
    }

    public VDFWriter writeNodeEnd() {
        if (whitespace.length() >= 4) {
            whitespace.setLength(whitespace.length() - 4);
        }
        builder.append(whitespace).append(NODE_END).append(NEWLINE);
        return this;
    }

    public VDFWriter writeValue(String key, String value) {
        builder.append(whitespace)
                .append(QUOTES).append(key).append(QUOTES)
                .append(SPACE)
                .append(QUOTES).append(value).append(QUOTES)
                .append(NEWLINE);
        return this;
    }

    public VDFWriter writeValue(String key, float value) {
        return this.writeValue(key, String.valueOf(value));
    }

    public VDFWriter writeValue(String key, double value) {
        return this.writeValue(key, String.valueOf(value));
    }

    public VDFWriter writeValue(String key, long value) {
        return this.writeValue(key, String.valueOf(value));
    }

    public VDFWriter writeValue(String key, int value) {
        return this.writeValue(key, String.valueOf(value));
    }

    public VDFWriter writeValue(String key, boolean value) {
        return this.writeValue(key, String.valueOf(value));
    }

    public VDFWriter writeValue(String key, byte value) {
        return this.writeValue(key, String.valueOf(value));
    }

    public VDFWriter writeValue(String key, short value) {
        return this.writeValue(key, String.valueOf(value));
    }

    public VDFWriter writeValue(String key, char value) {
        return this.writeValue(key, String.valueOf(value));
    }

    public VDFWriter writeValue(String key, String... values) {
        for (String value : values) {
            writeValue(key, value);
        }
        return this;
    }

    public VDFWriter writeValue(String key, float... values) {
        for (float value : values) {
            writeValue(key, value);
        }
        return this;
    }

    public VDFWriter writeValue(String key, double... values) {
        for (double value : values) {
            writeValue(key, value);
        }
        return this;
    }

    public VDFWriter writeValue(String key, long... values) {
        for (long value : values) {
            writeValue(key, value);
        }
        return this;
    }

    public VDFWriter writeValue(String key, int... values) {
        for (int value : values) {
            writeValue(key, value);
        }
        return this;
    }

    public VDFWriter writeValue(String key, boolean... values) {
        for (boolean value : values) {
            writeValue(key, value);
        }
        return this;
    }

    public VDFWriter writeValue(String key, byte... values) {
        for (byte value : values) {
            writeValue(key, value);
        }
        return this;
    }

    public VDFWriter writeValue(String key, short... values) {
        for (short value : values) {
            writeValue(key, value);
        }
        return this;
    }

    public VDFWriter writeValue(String key, char... values) {
        for (char value : values) {
            writeValue(key, value);
        }
        return this;
    }

    public String toVDF() {
        return builder.toString();
    }

}
