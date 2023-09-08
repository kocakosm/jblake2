JBlake2 [![CI][1]][2] [![Coverage][3]][4] [![Version][5]][6] [![Javadocs][7]][8] [![License][9]][10]
====================================================================================================

A pure Java (8+) implementation of BLAKE2 ([RFC 7693][11]).


Getting started
---------------

Maven
```xml
  <dependency>
    <groupId>org.kocakosm</groupId>
    <artifactId>jblake2</artifactId>
    <version>0.5</version>
  </dependency>
```

Gradle
```groovy
  compile 'org.kocakosm:jblake2:0.5'
```

*Note: module name for the Java Module System is `org.kocakosm.jblake2`.*


Usage
-----

Browse [API docs][12] for the most recent release.


Reproducing/verifying a release build
-------------------------------------

JBlake2's release builds are reproducible/verifiable since version 0.4. This
means that you can reproduce the released jar by building JBlake2 from source.
See [reproducible-builds.org][13] for more information.

To verify a particular release build, you'll need an x86_64 machine with GNU
Bash (4.1+), Docker (17.05+), and Git installed.
First, clone the [repository][14] and checkout the desired version tag. Then,
run the `verify-build.sh` script located at the project's root. This script
recreates the build environment used to release the version, builds the project
from source, downloads the released jar from Maven Central and checks that the
build output matches (*bit for bit*) the downloaded artifact.


License
-------

This program is free software: you can redistribute it and/or modify it under
the terms of the GNU Lesser General Public License as published by the Free
Software Foundation, either version 3 of the License, or (at your option) any
later version.

This program is distributed in the hope that it will be useful, but _without any
warranty;_ without even the implied warranty of _merchantability_ or _fitness
for a particular purpose_.

See the [GNU Lesser General Public License][15] for more details.


Contact
-------

kocakosm[@]gmail[dot]com


 [1]: https://github.com/kocakosm/jblake2/actions/workflows/ci.yml/badge.svg
 [2]: https://github.com/kocakosm/jblake2/actions/workflows/ci.yml
 [3]: https://img.shields.io/coveralls/kocakosm/jblake2.svg
 [4]: https://coveralls.io/github/kocakosm/jblake2
 [5]: https://img.shields.io/maven-central/v/org.kocakosm/jblake2.svg
 [6]: https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.kocakosm%22%20AND%20a%3A%22jblake2%22
 [7]: https://javadoc.io/badge/org.kocakosm/jblake2.svg
 [8]: https://javadoc.io/doc/org.kocakosm/jblake2
 [9]: https://img.shields.io/badge/license-LGPL_v3-4383c3.svg
 [10]: https://www.gnu.org/licenses/lgpl.txt
 [11]: https://tools.ietf.org/html/rfc7693
 [12]: https://www.javadoc.io/doc/org.kocakosm/jblake2
 [13]: https://reproducible-builds.org
 [14]: https://github.com/kocakosm/jblake2
 [15]: https://www.gnu.org/licenses/lgpl-3.0-standalone.html
