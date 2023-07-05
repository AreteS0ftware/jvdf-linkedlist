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

package it.aretesoftware.jvdf;

import com.badlogic.gdx.utils.Null;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterable tree structure that represents a set of key-value pairs in a VDF document.
 * @author Brendan Heinonen
 */
public class VDFNode {

    private VDFNode.ValueType type;

    /** May be null. */
    private String stringValue;
    private double doubleValue;
    private long longValue;

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
    public VDFNode (@Null String value) {
        set(value);
    }

    public VDFNode (double value) {
        set(value, null);
    }

    public VDFNode (long value) {
        set(value, null);
    }

    public VDFNode (double value, String stringValue) {
        set(value, stringValue);
    }

    public VDFNode (long value, String stringValue) {
        set(value, stringValue);
    }

    public VDFNode (boolean value) {
        set(value);
    }


    /** Returns the child at the specified index. This requires walking the linked list to the specified entry, see
     * {@link VDFNode} for how to iterate efficiently.
     * @return May be null. */
    public @Null VDFNode get (int index) {
        VDFNode current = child;
        while (current != null && index > 0) {
            index--;
            current = current.next;
        }
        return current;
    }

    /** Returns the child with the specified name.
     * @return May be null. */
    public @Null VDFNode get (String name) {
        VDFNode current = child;
        while (current != null && (current.name == null || !current.name.equalsIgnoreCase(name)))
            current = current.next;
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
    public @Null VDFNode remove (int index) {
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
    public @Null VDFNode remove (String name) {
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



    /** Returns this value as a string.
     * @return May be null if this value is null.
     * @throws IllegalStateException if this an array or object. */
    public @Null String asString () {
        switch (type) {
            case stringValue:
                return stringValue;
            case doubleValue:
                return stringValue != null ? stringValue : Double.toString(doubleValue);
            case longValue:
                return stringValue != null ? stringValue : Long.toString(longValue);
            case booleanValue:
                return longValue != 0 ? "true" : "false";
            case nullValue:
                return null;
        }
        throw new IllegalStateException("Value cannot be converted to string: " + type);
    }

    /** Returns this value as a float.
     * @throws IllegalStateException if this an array or object. */
    public float asFloat () {
        switch (type) {
            case stringValue:
                return Float.parseFloat(stringValue);
            case doubleValue:
                return (float)doubleValue;
            case longValue:
                return longValue;
            case booleanValue:
                return longValue != 0 ? 1 : 0;
        }
        throw new IllegalStateException("Value cannot be converted to float: " + type);
    }

    /** Returns this value as a double.
     * @throws IllegalStateException if this an array or object. */
    public double asDouble () {
        switch (type) {
            case stringValue:
                return Double.parseDouble(stringValue);
            case doubleValue:
                return doubleValue;
            case longValue:
                return longValue;
            case booleanValue:
                return longValue != 0 ? 1 : 0;
        }
        throw new IllegalStateException("Value cannot be converted to double: " + type);
    }

    /** Returns this value as a long.
     * @throws IllegalStateException if this an array or object. */
    public long asLong () {
        switch (type) {
            case stringValue:
                return Long.parseLong(stringValue);
            case doubleValue:
                return (long)doubleValue;
            case longValue:
                return longValue;
            case booleanValue:
                return longValue != 0 ? 1 : 0;
        }
        throw new IllegalStateException("Value cannot be converted to long: " + type);
    }

    /** Returns this value as an int.
     * @throws IllegalStateException if this an array or object. */
    public int asInt () {
        switch (type) {
            case stringValue:
                return Integer.parseInt(stringValue);
            case doubleValue:
                return (int)doubleValue;
            case longValue:
                return (int)longValue;
            case booleanValue:
                return longValue != 0 ? 1 : 0;
        }
        throw new IllegalStateException("Value cannot be converted to int: " + type);
    }

    /** Returns this value as a boolean.
     * @throws IllegalStateException if this an array or object. */
    public boolean asBoolean () {
        switch (type) {
            case stringValue:
                return stringValue.equalsIgnoreCase("true");
            case doubleValue:
                return doubleValue != 0;
            case longValue:
                return longValue != 0;
            case booleanValue:
                return longValue != 0;
        }
        throw new IllegalStateException("Value cannot be converted to boolean: " + type);
    }

    /** Returns this value as a byte.
     * @throws IllegalStateException if this an array or object. */
    public byte asByte () {
        switch (type) {
            case stringValue:
                return Byte.parseByte(stringValue);
            case doubleValue:
                return (byte)doubleValue;
            case longValue:
                return (byte)longValue;
            case booleanValue:
                return longValue != 0 ? (byte)1 : 0;
        }
        throw new IllegalStateException("Value cannot be converted to byte: " + type);
    }

    /** Returns this value as a short.
     * @throws IllegalStateException if this an array or object. */
    public short asShort () {
        switch (type) {
            case stringValue:
                return Short.parseShort(stringValue);
            case doubleValue:
                return (short)doubleValue;
            case longValue:
                return (short)longValue;
            case booleanValue:
                return longValue != 0 ? (short)1 : 0;
        }
        throw new IllegalStateException("Value cannot be converted to short: " + type);
    }

    /** Returns this value as a char.
     * @throws IllegalStateException if this an array or object. */
    public char asChar () {
        switch (type) {
            case stringValue:
                return stringValue.length() == 0 ? 0 : stringValue.charAt(0);
            case doubleValue:
                return (char)doubleValue;
            case longValue:
                return (char)longValue;
            case booleanValue:
                return longValue != 0 ? (char)1 : 0;
        }
        throw new IllegalStateException("Value cannot be converted to char: " + type);
    }

    /** Returns the children of this value as a newly allocated String array.
     * @throws IllegalStateException if this is not an array. */
    public String[] asStringArray () {
        if (type != VDFNode.ValueType.array) throw new IllegalStateException("Value is not an array: " + type);
        String[] array = new String[size];
        int i = 0;
        for (VDFNode value = child; value != null; value = value.next, i++) {
            String v;
            switch (value.type) {
                case stringValue:
                    v = value.stringValue;
                    break;
                case doubleValue:
                    v = stringValue != null ? stringValue : Double.toString(value.doubleValue);
                    break;
                case longValue:
                    v = stringValue != null ? stringValue : Long.toString(value.longValue);
                    break;
                case booleanValue:
                    v = value.longValue != 0 ? "true" : "false";
                    break;
                case nullValue:
                    v = null;
                    break;
                default:
                    throw new IllegalStateException("Value cannot be converted to string: " + value.type);
            }
            array[i] = v;
        }
        return array;
    }

    /** Returns the children of this value as a newly allocated float array.
     * @throws IllegalStateException if this is not an array. */
    public float[] asFloatArray () {
        if (type != VDFNode.ValueType.array) throw new IllegalStateException("Value is not an array: " + type);
        float[] array = new float[size];
        int i = 0;
        for (VDFNode value = child; value != null; value = value.next, i++) {
            float v;
            switch (value.type) {
                case stringValue:
                    v = Float.parseFloat(value.stringValue);
                    break;
                case doubleValue:
                    v = (float)value.doubleValue;
                    break;
                case longValue:
                    v = value.longValue;
                    break;
                case booleanValue:
                    v = value.longValue != 0 ? 1 : 0;
                    break;
                default:
                    throw new IllegalStateException("Value cannot be converted to float: " + value.type);
            }
            array[i] = v;
        }
        return array;
    }

    /** Returns the children of this value as a newly allocated double array.
     * @throws IllegalStateException if this is not an array. */
    public double[] asDoubleArray () {
        if (type != VDFNode.ValueType.array) throw new IllegalStateException("Value is not an array: " + type);
        double[] array = new double[size];
        int i = 0;
        for (VDFNode value = child; value != null; value = value.next, i++) {
            double v;
            switch (value.type) {
                case stringValue:
                    v = Double.parseDouble(value.stringValue);
                    break;
                case doubleValue:
                    v = value.doubleValue;
                    break;
                case longValue:
                    v = value.longValue;
                    break;
                case booleanValue:
                    v = value.longValue != 0 ? 1 : 0;
                    break;
                default:
                    throw new IllegalStateException("Value cannot be converted to double: " + value.type);
            }
            array[i] = v;
        }
        return array;
    }

    /** Returns the children of this value as a newly allocated long array.
     * @throws IllegalStateException if this is not an array. */
    public long[] asLongArray () {
        if (type != VDFNode.ValueType.array) throw new IllegalStateException("Value is not an array: " + type);
        long[] array = new long[size];
        int i = 0;
        for (VDFNode value = child; value != null; value = value.next, i++) {
            long v;
            switch (value.type) {
                case stringValue:
                    v = Long.parseLong(value.stringValue);
                    break;
                case doubleValue:
                    v = (long)value.doubleValue;
                    break;
                case longValue:
                    v = value.longValue;
                    break;
                case booleanValue:
                    v = value.longValue != 0 ? 1 : 0;
                    break;
                default:
                    throw new IllegalStateException("Value cannot be converted to long: " + value.type);
            }
            array[i] = v;
        }
        return array;
    }

    /** Returns the children of this value as a newly allocated int array.
     * @throws IllegalStateException if this is not an array. */
    public int[] asIntArray () {
        if (type != VDFNode.ValueType.array) throw new IllegalStateException("Value is not an array: " + type);
        int[] array = new int[size];
        int i = 0;
        for (VDFNode value = child; value != null; value = value.next, i++) {
            int v;
            switch (value.type) {
                case stringValue:
                    v = Integer.parseInt(value.stringValue);
                    break;
                case doubleValue:
                    v = (int)value.doubleValue;
                    break;
                case longValue:
                    v = (int)value.longValue;
                    break;
                case booleanValue:
                    v = value.longValue != 0 ? 1 : 0;
                    break;
                default:
                    throw new IllegalStateException("Value cannot be converted to int: " + value.type);
            }
            array[i] = v;
        }
        return array;
    }

    /** Returns the children of this value as a newly allocated boolean array.
     * @throws IllegalStateException if this is not an array. */
    public boolean[] asBooleanArray () {
        if (type != VDFNode.ValueType.array) throw new IllegalStateException("Value is not an array: " + type);
        boolean[] array = new boolean[size];
        int i = 0;
        for (VDFNode value = child; value != null; value = value.next, i++) {
            boolean v;
            switch (value.type) {
                case stringValue:
                    v = Boolean.parseBoolean(value.stringValue);
                    break;
                case doubleValue:
                    v = value.doubleValue == 0;
                    break;
                case longValue:
                    v = value.longValue == 0;
                    break;
                case booleanValue:
                    v = value.longValue != 0;
                    break;
                default:
                    throw new IllegalStateException("Value cannot be converted to boolean: " + value.type);
            }
            array[i] = v;
        }
        return array;
    }

    /** Returns the children of this value as a newly allocated byte array.
     * @throws IllegalStateException if this is not an array. */
    public byte[] asByteArray () {
        if (type != VDFNode.ValueType.array) throw new IllegalStateException("Value is not an array: " + type);
        byte[] array = new byte[size];
        int i = 0;
        for (VDFNode value = child; value != null; value = value.next, i++) {
            byte v;
            switch (value.type) {
                case stringValue:
                    v = Byte.parseByte(value.stringValue);
                    break;
                case doubleValue:
                    v = (byte)value.doubleValue;
                    break;
                case longValue:
                    v = (byte)value.longValue;
                    break;
                case booleanValue:
                    v = value.longValue != 0 ? (byte)1 : 0;
                    break;
                default:
                    throw new IllegalStateException("Value cannot be converted to byte: " + value.type);
            }
            array[i] = v;
        }
        return array;
    }

    /** Returns the children of this value as a newly allocated short array.
     * @throws IllegalStateException if this is not an array. */
    public short[] asShortArray () {
        if (type != VDFNode.ValueType.array) throw new IllegalStateException("Value is not an array: " + type);
        short[] array = new short[size];
        int i = 0;
        for (VDFNode value = child; value != null; value = value.next, i++) {
            short v;
            switch (value.type) {
                case stringValue:
                    v = Short.parseShort(value.stringValue);
                    break;
                case doubleValue:
                    v = (short)value.doubleValue;
                    break;
                case longValue:
                    v = (short)value.longValue;
                    break;
                case booleanValue:
                    v = value.longValue != 0 ? (short)1 : 0;
                    break;
                default:
                    throw new IllegalStateException("Value cannot be converted to short: " + value.type);
            }
            array[i] = v;
        }
        return array;
    }

    /** Returns the children of this value as a newly allocated char array.
     * @throws IllegalStateException if this is not an array. */
    public char[] asCharArray () {
        if (type != VDFNode.ValueType.array) throw new IllegalStateException("Value is not an array: " + type);
        char[] array = new char[size];
        int i = 0;
        for (VDFNode value = child; value != null; value = value.next, i++) {
            char v;
            switch (value.type) {
                case stringValue:
                    v = value.stringValue.length() == 0 ? 0 : value.stringValue.charAt(0);
                    break;
                case doubleValue:
                    v = (char)value.doubleValue;
                    break;
                case longValue:
                    v = (char)value.longValue;
                    break;
                case booleanValue:
                    v = value.longValue != 0 ? (char)1 : 0;
                    break;
                default:
                    throw new IllegalStateException("Value cannot be converted to char: " + value.type);
            }
            array[i] = v;
        }
        return array;
    }

    /** Returns true if a child with the specified name exists and has a child. */
    public boolean hasChild (String name) {
        return getChild(name) != null;
    }

    /** Finds the child with the specified name and returns its first child.
     * @return May be null. */
    public @Null VDFNode getChild (String name) {
        VDFNode child = get(name);
        return child == null ? null : child.child;
    }

    /** Finds the child with the specified name and returns it as a string. Returns defaultValue if not found.
     * @param defaultValue May be null. */
    public String getString (String name, @Null String defaultValue) {
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

    public boolean isArray () {
        return type == VDFNode.ValueType.array;
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

    /** Returns true if this is not an array or object. */
    public boolean isValue () {
        switch (type) {
            case stringValue:
            case doubleValue:
            case longValue:
            case booleanValue:
            case nullValue:
                return true;
        }
        return false;
    }

    /** Returns the name for this object value.
     * @return May be null. */
    public @Null String name () {
        return name;
    }

    /** @param name May be null. */
    public void setName (@Null String name) {
        this.name = name;
    }

    /** Returns the parent for this value.
     * @return May be null. */
    public @Null VDFNode parent () {
        return parent;
    }

    /** Returns the first child for this object or array.
     * @return May be null. */
    public @Null VDFNode child () {
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
    public @Null VDFNode next () {
        return next;
    }

    /** Sets the next sibling of this value. Does not change the parent {@link #size()}.
     * @param next May be null. */
    public void setNext (@Null VDFNode next) {
        this.next = next;
    }

    /** Returns the previous sibling of this value.
     * @return May be null. */
    public @Null VDFNode prev () {
        return prev;
    }


    /** Sets the next sibling of this value. Does not change the parent {@link #size()}.
     * @param prev May be null. */
    public void setPrev (@Null VDFNode prev) {
        this.prev = prev;
    }

    /** @param value May be null. */
    public void set (@Null String value) {
        stringValue = value;
        type = value == null ? VDFNode.ValueType.nullValue : VDFNode.ValueType.stringValue;
    }

    /** @param stringValue May be null if the string representation is the string value of the double (eg, no leading zeros). */
    public void set (double value, @Null String stringValue) {
        doubleValue = value;
        longValue = (long)value;
        this.stringValue = stringValue;
        type = VDFNode.ValueType.doubleValue;
    }

    /** @param stringValue May be null if the string representation is the string value of the long (eg, no leading zeros). */
    public void set (long value, @Null String stringValue) {
        longValue = value;
        doubleValue = value;
        this.stringValue = stringValue;
        type = VDFNode.ValueType.longValue;
    }

    public void set (boolean value) {
        longValue = value ? 1 : 0;
        type = VDFNode.ValueType.booleanValue;
    }

    public VDFNode.VDFIterator iterator () {
        return new VDFNode.VDFIterator();
    }


    public class VDFIterator implements Iterator<VDFNode>, Iterable<VDFNode> {
        VDFNode entry = child;
        VDFNode  current;

        public boolean hasNext () {
            return entry != null;
        }

        public VDFNode  next () {
            current = entry;
            if (current == null) throw new NoSuchElementException();
            entry = current.next;
            return current;
        }

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

        public Iterator<VDFNode> iterator () {
            return this;
        }
    }

    public enum ValueType {
        object, array, stringValue, doubleValue, longValue, booleanValue, nullValue
    }

}
