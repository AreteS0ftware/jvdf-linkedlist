# json-vdf

A parser for Valve Software's KeyValues ("VDF") format, commonly used by Source engine games, written entirely in Java. 

Forked from <a href="https://github.com/PlatinumDigitalGroup/JVDF">PlatinumDigitalGroup's JVDF</a>, the main difference is that VDF nodes work in a very similar fashion to <a href="https://github.com/libgdx/libgdx">libGDX's</a> <a href="https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/utils/JsonValue.java">JSON</a> implementation. <b>Note that this library has no libGDX dependency!</b>

## Features

* Zero dependencies.
* Decently fast. The 6.3MB, 227k line CS:GO item schema was parsed on my machine in about 150ms when HotSpot was warmed up.
* Multi-threaded preprocessor and binder.
* Memory efficient.  The resultant tree structures are often a tenth (1/10) the size of the input text.
* Standalone preprocessor can convert human-readable VDF documents into much smaller, valid VDF documents with whitespace and comments stripped.
* Fully compliant with the VDF format, as documented on the [Valve Developer Wiki](https://developer.valvesoftware.com/wiki/KeyValues).
* Exposed API is thoroughly unit tested.  The parser was also validated against CS:GO's item schema.

## Example

*VDF document*
```
"key1"      "value1"
"key2"      "value2"
"root_node"
{
    "key3"      "value3"
    ...
    "child_node"
    {
        "child1"        "child value 1"
        "child2"        "child value 2"
    }
}
```

### Using the VDFParser
```
VDFNode node = new VDFParser().parse(...);

node.getString("key1")              => value1
node.getString("key2")              => value2

node.get("root_node")
    .getString("key3")              => value3

node.get("root_node")
    .get("child_node")
    .getString("child2")            => child value 2
```

## License

MIT License

Copyright (c) 2023 Arete

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


# Special thanks to <a href="https://github.com/staticinvocation">staticinvocation</a>
