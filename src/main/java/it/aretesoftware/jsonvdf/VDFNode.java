/*
Copyright 2017 Platinum Digital Group LLC

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An iterable tree structure that represents a set of key-value pairs in a VDF document.
 * @author Brendan Heinonen
 */
public class VDFNode {

    private VDFNode.ValueType type;

    /** May be null. */
    private String stringValue;

    public String name;
    /** May be null. */
    public VDFNode child, parent;
    /** May be null. When changing this field the parent {@link #size()} may need to be changed. */
    public VDFNode next, prev;
    public int size;

    public VDFNode (VDFNode.ValueType type) {
        this.type = type;
    }

    /** @param value May be null. */
    public VDFNode (String value) {
        set(value);
    }


    /** Returns the child at the specified index. This requires walking the linked list to the specified entry, see
     * {@link VDFNode} for how to iterate efficiently.
     * @return May be null. */
    public VDFNode get (int index) {
        VDFNode current = child;
        while (current != null && index > 0) {
            index--;
            current = current.next;
        }
        return current;
    }

    /** Returns the child with the specified name.
     * @return May be null. */
    public VDFNode get (String name) {
        VDFNode current = child;
        while (current != null && (current.name == null || !current.name.equalsIgnoreCase(name)))
            current = current.next;
        return current;
    }

    public VDFNode get (String name, int index) {
        VDFNode current = child;
        while (current != null && index >= 0) {
            if (current.name.equalsIgnoreCase(name)) {
                index--;
            }
            if (index >= 0) {
                current = current.next;
            }
        }
        return current;
    }

    /** Returns true if a child with the specified name exists. */
    public boolean has (String name) {
        return get(name) != null;
    }

    /** Returns an iterator for the child with the specified name, or an empty iterator if no child is found. */
    public VDFNode.VDFIterator iterator (String name) {
        VDFNode current = get(name);
        if (current == null) {
            VDFNode.VDFIterator iter = new VDFNode.VDFIterator();
            iter.entry = null;
            return iter;
        }
        return current.iterator();
    }

    /** Returns the child at the specified index. This requires walking the linked list to the specified entry, see
     * {@link VDFNode} for how to iterate efficiently.
     * @throws IllegalArgumentException if the child was not found. */
    public VDFNode require (int index) {
        VDFNode current = get(index);
        if (current == null) throw new IllegalArgumentException("Child not found with index: " + index);
        return current;
    }

    /** Returns the child with the specified name.
     * @throws IllegalArgumentException if the child was not found. */
    public VDFNode require (String name) {
        VDFNode current = get(name);
        if (current == null) throw new IllegalArgumentException("Child not found with name: " + name);
        return current;
    }

    /** Removes the child with the specified index. This requires walking the linked list to the specified entry, see
     * {@link VDFNode} for how to iterate efficiently.
     * @return May be null. */
    public VDFNode remove (int index) {
        VDFNode child = get(index);
        if (child == null) return null;
        if (child.prev == null) {
            this.child = child.next;
            if (this.child != null) this.child.prev = null;
        } else {
            child.prev.next = child.next;
            if (child.next != null) child.next.prev = child.prev;
        }
        size--;
        return child;
    }

    /** Removes the child with the specified name.
     * @return May be null. */
    public VDFNode remove (String name) {
        VDFNode child = get(name);
        if (child == null) return null;
        if (child.prev == null) {
            this.child = child.next;
            if (this.child != null) this.child.prev = null;
        } else {
            child.prev.next = child.next;
            if (child.next != null) child.next.prev = child.prev;
        }
        size--;
        return child;
    }

    /** Removes this value from its parent. */
    public void remove () {
        if (parent == null) throw new IllegalStateException();
        if (prev == null) {
            parent.child = next;
            if (parent.child != null) parent.child.prev = null;
        } else {
            prev.next = next;
            if (next != null) next.prev = prev;
        }
        parent.size--;
    }

    /** Returns true if there are one or more children in the array or object. */
    public boolean notEmpty () {
        return size > 0;
    }

    /** Returns true if there are not children in the array or object. */
    public boolean isEmpty () {
        return size == 0;
    }

    /** @deprecated Use {@link #size} instead. Returns this number of children in the array or object. */
    @Deprecated
    public int size () {
        return size;
    }

    public int sizeOf(String name) {
        VDFNode current = child;
        int count = 0;
        while (current != null) {
            if (current.name.equalsIgnoreCase(name)) {
                count++;
            }
            current = current.next;
        }
        return count;
    }

    /** Returns this value as a string.
     * @return May be null if this value is null.
     * @throws IllegalStateException if this an array or object. */
    public String asString () {
        return stringValue;
    }

    public String asString (String defaultValue) {
        return stringValue != null ? stringValue : defaultValue;
    }

    /** Returns this value as a float.
     * @throws IllegalStateException if this an array or object. */
    public float asFloat () {
        return Float.parseFloat(stringValue);
    }

    public float asFloat(float defaultValue) {
        try {
            return asFloat();
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /** Returns this value as a double.
     * @throws IllegalStateException if this an array or object. */
    public double asDouble () {
        return Double.parseDouble(stringValue);
    }

    public double asDouble(double defaultValue) {
        try {
            return asDouble();
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /** Returns this value as a long.
     * @throws IllegalStateException if this an array or object. */
    public long asLong () {
        return Long.parseLong(stringValue);
    }

    public long asLong(long defaultValue) {
        try {
            return asLong();
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /** Returns this value as an int.
     * @throws IllegalStateException if this an array or object. */
    public int asInt () {
        return Integer.parseInt(stringValue);
    }

    public long asInt(int defaultValue) {
        try {
            return asInt();
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /** Returns this value as a boolean.
     * @throws IllegalStateException if this an array or object. */
    public boolean asBoolean () {
        return Boolean.parseBoolean(stringValue);
    }

    public boolean asBoolean(boolean defaultValue) {
        try {
            return asBoolean();
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /** Returns this value as a byte.
     * @throws IllegalStateException if this an array or object. */
    public byte asByte () {
        return Byte.parseByte(stringValue);
    }

    public byte asByte(byte defaultValue) {
        try {
            return asByte();
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /** Returns this value as a short.
     * @throws IllegalStateException if this an array or object. */
    public short asShort () {
        return Short.parseShort(stringValue);
    }

    public short asShort(short defaultValue) {
        try {
            return asShort();
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /** Returns this value as a char.
     * @throws IllegalStateException if this an array or object. */
    public char asChar () {
        try {
            long value = asLong();
            return (char) value;
        }
        catch (NumberFormatException e) {
            if (stringValue.length() > 1) throw new IllegalStateException("String has more than one character.");
            return stringValue.charAt(0);
        }
    }

    public char asShort(char defaultValue) {
        try {
            return asChar();
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public VDFNode[] asArray(String key) {
        List<VDFNode> list = new ArrayList<>();
        int i = 0;
        for (VDFNode value = child; value != null; value = value.next, i++) {
            if (key.equals(value.name)) {
                list.add(value);
            }
        }
        return list.toArray(new VDFNode[list.size()]);
    }

    /** Returns the children of this value as a newly allocated String array.
     * @throws IllegalStateException if this is not an array. */
    public String[] asStringArray (String key) {
        List<String> list = new ArrayList<>();
        int i = 0;
        for (VDFNode value = child; value != null; value = value.next, i++) {
            if (key.equals(value.name)) {
                list.add(value.stringValue);
            }
        }
        return list.toArray(new String[list.size()]);
    }

    /** Returns the children of this value as a newly allocated float array.
     * @throws IllegalStateException if this is not an array. */
    public Float[] asFloatArray (String key) {
        List<Float> list = new ArrayList<>();
        int i = 0;
        for (VDFNode value = child; value != null; value = value.next, i++) {
            if (key.equals(value.name)) {
                list.add(value.asFloat());
            }
        }
        return list.toArray(new Float[list.size()]);
    }

    /** Returns the children of this value as a newly allocated double array.
     * @throws IllegalStateException if this is not an array. */
    public Double[] asDoubleArray (String key) {
        List<Double> list = new ArrayList<>();
        int i = 0;
        for (VDFNode value = child; value != null; value = value.next, i++) {
            if (key.equals(value.name)) {
                list.add(value.asDouble());
            }
        }
        return list.toArray(new Double[list.size()]);
    }

    /** Returns the children of this value as a newly allocated long array.
     * @throws IllegalStateException if this is not an array. */
    public Long[] asLongArray (String key) {
        List<Long> list = new ArrayList<>();
        int i = 0;
        for (VDFNode value = child; value != null; value = value.next, i++) {
            if (key.equals(value.name)) {
                list.add(value.asLong());
            }
        }
        return list.toArray(new Long[list.size()]);
    }

    /** Returns the children of this value as a newly allocated int array.
     * @throws IllegalStateException if this is not an array. */
    public Integer[] asIntArray (String key) {
        List<Integer> list = new ArrayList<>();
        int i = 0;
        for (VDFNode value = child; value != null; value = value.next, i++) {
            if (key.equals(value.name)) {
                list.add(value.asInt());
            }
        }
        return list.toArray(new Integer[list.size()]);
    }

    /** Returns the children of this value as a newly allocated boolean array.
     * @throws IllegalStateException if this is not an array. */
    public Boolean[] asBooleanArray (String key) {
        List<Boolean> list = new ArrayList<>();
        int i = 0;
        for (VDFNode value = child; value != null; value = value.next, i++) {
            if (key.equals(value.name)) {
                list.add(value.asBoolean());
            }
        }
        return list.toArray(new Boolean[list.size()]);
    }

    /** Returns the children of this value as a newly allocated byte array.
     * @throws IllegalStateException if this is not an array. */
    public Byte[] asByteArray (String key) {
        List<Byte> list = new ArrayList<>();
        int i = 0;
        for (VDFNode value = child; value != null; value = value.next, i++) {
            if (key.equals(value.name)) {
                list.add(value.asByte());
            }
        }
        return list.toArray(new Byte[list.size()]);
    }

    /** Returns the children of this value as a newly allocated short array.
     * @throws IllegalStateException if this is not an array. */
    public Short[] asShortArray (String key) {
        List<Short> list = new ArrayList<>();
        int i = 0;
        for (VDFNode value = child; value != null; value = value.next, i++) {
            if (key.equals(value.name)) {
                list.add(value.asShort());
            }
        }
        return list.toArray(new Short[list.size()]);
    }

    /** Returns the children of this value as a newly allocated char array.
     * @throws IllegalStateException if this is not an array. */
    public Character[] asCharArray (String key) {
        List<Character> list = new ArrayList<>();
        int i = 0;
        for (VDFNode value = child; value != null; value = value.next, i++) {
            if (key.equals(value.name)) {
                list.add(value.asChar());
            }
        }
        return list.toArray(new Character[list.size()]);
    }

    /** Returns true if a child with the specified name exists and has a child. */
    public boolean hasChild (String name) {
        return getChild(name) != null;
    }

    /** Finds the child with the specified name and returns its first child.
     * @return May be null. */
    public VDFNode getChild (String name) {
        VDFNode child = get(name);
        return child == null ? null : child.child;
    }

    /** Finds the child with the specified name and returns it as a string. Returns defaultValue if not found.
     * @param defaultValue May be null. */
    public String getString (String name, String defaultValue) {
        VDFNode child = get(name);
        return (child == null || !child.isValue() || child.isNull()) ? defaultValue : child.asString();
    }

    /** Finds the child with the specified name and returns it as a float. Returns defaultValue if not found. */
    public float getFloat (String name, float defaultValue) {
        VDFNode child = get(name);
        return (child == null || !child.isValue() || child.isNull()) ? defaultValue : child.asFloat();
    }

    /** Finds the child with the specified name and returns it as a double. Returns defaultValue if not found. */
    public double getDouble (String name, double defaultValue) {
        VDFNode child = get(name);
        return (child == null || !child.isValue() || child.isNull()) ? defaultValue : child.asDouble();
    }

    /** Finds the child with the specified name and returns it as a long. Returns defaultValue if not found. */
    public long getLong (String name, long defaultValue) {
        VDFNode child = get(name);
        return (child == null || !child.isValue() || child.isNull()) ? defaultValue : child.asLong();
    }

    /** Finds the child with the specified name and returns it as an int. Returns defaultValue if not found. */
    public int getInt (String name, int defaultValue) {
        VDFNode child = get(name);
        return (child == null || !child.isValue() || child.isNull()) ? defaultValue : child.asInt();
    }

    /** Finds the child with the specified name and returns it as a boolean. Returns defaultValue if not found. */
    public boolean getBoolean (String name, boolean defaultValue) {
        VDFNode child = get(name);
        return (child == null || !child.isValue() || child.isNull()) ? defaultValue : child.asBoolean();
    }

    /** Finds the child with the specified name and returns it as a byte. Returns defaultValue if not found. */
    public byte getByte (String name, byte defaultValue) {
        VDFNode child = get(name);
        return (child == null || !child.isValue() || child.isNull()) ? defaultValue : child.asByte();
    }

    /** Finds the child with the specified name and returns it as a short. Returns defaultValue if not found. */
    public short getShort (String name, short defaultValue) {
        VDFNode child = get(name);
        return (child == null || !child.isValue() || child.isNull()) ? defaultValue : child.asShort();
    }

    /** Finds the child with the specified name and returns it as a char. Returns defaultValue if not found. */
    public char getChar (String name, char defaultValue) {
        VDFNode child = get(name);
        return (child == null || !child.isValue() || child.isNull()) ? defaultValue : child.asChar();
    }

    /** Finds the child with the specified name and returns it as a string.
     * @throws IllegalArgumentException if the child was not found. */
    public String getString (String name) {
        VDFNode child = get(name);
        if (child == null) throw new IllegalArgumentException("Named value not found: " + name);
        return child.asString();
    }

    /** Finds the child with the specified name and returns it as a float.
     * @throws IllegalArgumentException if the child was not found. */
    public float getFloat (String name) {
        VDFNode child = get(name);
        if (child == null) throw new IllegalArgumentException("Named value not found: " + name);
        return child.asFloat();
    }

    /** Finds the child with the specified name and returns it as a double.
     * @throws IllegalArgumentException if the child was not found. */
    public double getDouble (String name) {
        VDFNode child = get(name);
        if (child == null) throw new IllegalArgumentException("Named value not found: " + name);
        return child.asDouble();
    }

    /** Finds the child with the specified name and returns it as a long.
     * @throws IllegalArgumentException if the child was not found. */
    public long getLong (String name) {
        VDFNode child = get(name);
        if (child == null) throw new IllegalArgumentException("Named value not found: " + name);
        return child.asLong();
    }

    /** Finds the child with the specified name and returns it as an int.
     * @throws IllegalArgumentException if the child was not found. */
    public int getInt (String name) {
        VDFNode child = get(name);
        if (child == null) throw new IllegalArgumentException("Named value not found: " + name);
        return child.asInt();
    }

    /** Finds the child with the specified name and returns it as a boolean.
     * @throws IllegalArgumentException if the child was not found. */
    public boolean getBoolean (String name) {
        VDFNode child = get(name);
        if (child == null) throw new IllegalArgumentException("Named value not found: " + name);
        return child.asBoolean();
    }

    /** Finds the child with the specified name and returns it as a byte.
     * @throws IllegalArgumentException if the child was not found. */
    public byte getByte (String name) {
        VDFNode child = get(name);
        if (child == null) throw new IllegalArgumentException("Named value not found: " + name);
        return child.asByte();
    }

    /** Finds the child with the specified name and returns it as a short.
     * @throws IllegalArgumentException if the child was not found. */
    public short getShort (String name) {
        VDFNode child = get(name);
        if (child == null) throw new IllegalArgumentException("Named value not found: " + name);
        return child.asShort();
    }

    /** Finds the child with the specified name and returns it as a char.
     * @throws IllegalArgumentException if the child was not found. */
    public char getChar (String name) {
        VDFNode child = get(name);
        if (child == null) throw new IllegalArgumentException("Named value not found: " + name);
        return child.asChar();
    }

    /** Finds the child with the specified index and returns it as a string.
     * @throws IllegalArgumentException if the child was not found. */
    public String getString (int index) {
        VDFNode child = get(index);
        if (child == null) throw new IllegalArgumentException("Indexed value not found: " + name);
        return child.asString();
    }

    /** Finds the child with the specified index and returns it as a float.
     * @throws IllegalArgumentException if the child was not found. */
    public float getFloat (int index) {
        VDFNode child = get(index);
        if (child == null) throw new IllegalArgumentException("Indexed value not found: " + name);
        return child.asFloat();
    }

    /** Finds the child with the specified index and returns it as a double.
     * @throws IllegalArgumentException if the child was not found. */
    public double getDouble (int index) {
        VDFNode child = get(index);
        if (child == null) throw new IllegalArgumentException("Indexed value not found: " + name);
        return child.asDouble();
    }

    /** Finds the child with the specified index and returns it as a long.
     * @throws IllegalArgumentException if the child was not found. */
    public long getLong (int index) {
        VDFNode child = get(index);
        if (child == null) throw new IllegalArgumentException("Indexed value not found: " + name);
        return child.asLong();
    }

    /** Finds the child with the specified index and returns it as an int.
     * @throws IllegalArgumentException if the child was not found. */
    public int getInt (int index) {
        VDFNode child = get(index);
        if (child == null) throw new IllegalArgumentException("Indexed value not found: " + name);
        return child.asInt();
    }

    /** Finds the child with the specified index and returns it as a boolean.
     * @throws IllegalArgumentException if the child was not found. */
    public boolean getBoolean (int index) {
        VDFNode child = get(index);
        if (child == null) throw new IllegalArgumentException("Indexed value not found: " + name);
        return child.asBoolean();
    }

    /** Finds the child with the specified index and returns it as a byte.
     * @throws IllegalArgumentException if the child was not found. */
    public byte getByte (int index) {
        VDFNode child = get(index);
        if (child == null) throw new IllegalArgumentException("Indexed value not found: " + name);
        return child.asByte();
    }

    /** Finds the child with the specified index and returns it as a short.
     * @throws IllegalArgumentException if the child was not found. */
    public short getShort (int index) {
        VDFNode child = get(index);
        if (child == null) throw new IllegalArgumentException("Indexed value not found: " + name);
        return child.asShort();
    }

    /** Finds the child with the specified index and returns it as a char.
     * @throws IllegalArgumentException if the child was not found. */
    public char getChar (int index) {
        VDFNode child = get(index);
        if (child == null) throw new IllegalArgumentException("Indexed value not found: " + name);
        return child.asChar();
    }

    public VDFNode.ValueType type () {
        return type;
    }

    public void setType (VDFNode.ValueType type) {
        if (type == null) throw new IllegalArgumentException("type cannot be null.");
        this.type = type;
    }

    public boolean isObject () {
        return type == VDFNode.ValueType.object;
    }

    public boolean isString () {
        return type == VDFNode.ValueType.stringValue;
    }

    /** Returns true if this is a double or long value. */
    public boolean isNumber () {
        return type == VDFNode.ValueType.doubleValue || type == VDFNode.ValueType.longValue;
    }

    public boolean isDouble () {
        return type == VDFNode.ValueType.doubleValue;
    }

    public boolean isLong () {
        return type == VDFNode.ValueType.longValue;
    }

    public boolean isBoolean () {
        return type == VDFNode.ValueType.booleanValue;
    }

    public boolean isNull () {
        return type == VDFNode.ValueType.nullValue;
    }

    /** Returns true if this is not an object. */
    public boolean isValue () {
        return type != ValueType.object;
    }

    /** Returns the name for this object value.
     * @return May be null. */
    public String name () {
        return name;
    }

    /** @param name May be null. */
    public void setName (String name) {
        this.name = name;
    }

    /** Returns the parent for this value.
     * @return May be null. */
    public VDFNode parent () {
        return parent;
    }

    /** Returns the first child for this object or array.
     * @return May be null. */
    public VDFNode child () {
        return child;
    }

    /** Sets the name of the specified value and adds it after the last child. */
    public void addChild (String name, VDFNode value) {
        if (name == null) throw new IllegalArgumentException("name cannot be null.");
        value.name = name;
        addChild(value);
    }

    /** Adds the specified value after the last child.
     * @throws IllegalStateException if this is an object and the specified child's name is null. */
    public void addChild (VDFNode value) {
        if (type == VDFNode.ValueType.object && value.name == null)
            throw new IllegalStateException("An object child requires a name: " + value);
        value.parent = this;
        size++;
        VDFNode current = child;
        if (current == null)
            child = value;
        else {
            while (true) {
                if (current.next == null) {
                    current.next = value;
                    value.prev = current;
                    return;
                }
                current = current.next;
            }
        }
    }

    /** Returns the next sibling of this value.
     * @return May be null. */
    public VDFNode next () {
        return next;
    }

    /** Sets the next sibling of this value. Does not change the parent {@link #size()}.
     * @param next May be null. */
    public void setNext (VDFNode next) {
        this.next = next;
    }

    /** Returns the previous sibling of this value.
     * @return May be null. */
    public VDFNode prev () {
        return prev;
    }


    /** Sets the next sibling of this value. Does not change the parent {@link #size()}.
     * @param prev May be null. */
    public void setPrev (VDFNode prev) {
        this.prev = prev;
    }

    /** @param value May be null. */
    public void set (String value) {
        stringValue = value;
        if (value == null) {
            type = VDFNode.ValueType.nullValue;
            return;
        }
        else {
            type = ValueType.stringValue;
        }

        if (value.equals("true") || value.equals("false")) {
            type = ValueType.booleanValue;
            return;
        }

        try {       //TODO find a better way
            Long.parseLong(value);
            type = ValueType.longValue;
        }
        catch (NumberFormatException e) {
            try {
                Double.parseDouble(value);
                type = ValueType.doubleValue;
            }
            catch (NumberFormatException ignored) {

            }
        }
    }

    public void set (double value) {
        this.stringValue = String.valueOf(value);
        type = ValueType.doubleValue;
    }

    public void set (long value) {
        this.stringValue = String.valueOf(value);
        type = ValueType.longValue;
    }

    public void set (boolean value) {
        this.stringValue = String.valueOf(value);
        type = ValueType.booleanValue;
    }

    public VDFNode.VDFIterator iterator () {
        return new VDFNode.VDFIterator();
    }

    public String toVDF() {
        return toVDF(this, new StringBuilder(), new StringBuilder(), true);
    }

    private String toVDF(VDFNode root, StringBuilder whitespace, StringBuilder builder, boolean newLineOnNode) {
        VDFNode current = root.parent != null ? root : root.child;
        while (current != null) {
            builder.append(whitespace);
            builder.append("\"").append(current.name).append("\"");
            builder.append(" ");
            if (current.isValue()) {
                builder.append("\"").append(current.stringValue).append("\"");
            }
            else {
                if (newLineOnNode) {
                    builder.append("\n");
                    builder.append(whitespace);
                }
                builder.append("{");
                if (current.child != null) {
                    builder.append("\n");
                    whitespace.append("    ");
                }
                toVDF(current.child, whitespace, builder, newLineOnNode);
                if (current.child != null) {
                    whitespace.setLength(whitespace.length() - 4);
                    builder.append(whitespace);
                }
                builder.append("}");
            }
            builder.append("\n");
            current = current.next;
        }
        return builder.toString();
    }


    public class VDFIterator implements Iterator<VDFNode>, Iterable<VDFNode> {
        VDFNode entry = child;
        VDFNode current;

        @Override
        public boolean hasNext () {
            return entry != null;
        }

        @Override
        public VDFNode  next () {
            current = entry;
            if (current == null) throw new NoSuchElementException();
            entry = current.next;
            return current;
        }

        @Override
        public void remove () {
            if (current.prev == null) {
                child = current.next;
                if (child != null) child.prev = null;
            } else {
                current.prev.next = current.next;
                if (current.next != null) current.next.prev = current.prev;
            }
            size--;
        }

        @Override
        public Iterator<VDFNode> iterator () {
            return this;
        }
    }

    public enum ValueType {
        object, stringValue, doubleValue, longValue, booleanValue, nullValue
    }

}
