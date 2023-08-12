# jvdf-linkedlist

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

Apache-2.0 license

Copyright (c) 2023 Arete

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.


# Special thanks to <a href="https://github.com/staticinvocation">staticinvocation</a>
