JBlake2 [![CI][1]][2] [![Coverage][3]][4] [![Reproducible Builds][5]][6] [![Version][7]][8] [![Javadocs][9]][10] [![License][11]][12]
=====================================================================================================================================

A pure Java (8+) implementation of BLAKE2 ([RFC 7693][13]).


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

Browse [API docs][10] for the most recent release.


Reproducibility
---------------

JBlake2 builds are reproducible since version 0.4. Released artifacts are
verified by the [Reproducible Central][14] project, the Java part of the
[Reproducible Builds][15] effort.


License
-------

This program is free software: you can redistribute it and/or modify it under
the terms of the GNU Lesser General Public License as published by the Free
Software Foundation, either version 3 of the License, or (at your option) any
later version.

This program is distributed in the hope that it will be useful, but _without any
warranty;_ without even the implied warranty of _merchantability_ or _fitness
for a particular purpose_.

See the [GNU Lesser General Public License][12] for more details.


Contact
-------

kocakosm[@]gmail[dot]com


 [1]: https://github.com/kocakosm/jblake2/actions/workflows/ci.yml/badge.svg
 [2]: https://github.com/kocakosm/jblake2/actions/workflows/ci.yml
 [3]: https://img.shields.io/coveralls/kocakosm/jblake2.svg
 [4]: https://coveralls.io/github/kocakosm/jblake2
 [5]: https://img.shields.io/badge/reproducible_builds-ok-success?labelColor=1e5b96
 [6]: https://github.com/jvm-repo-rebuild/reproducible-central#org.kocakosm:jblake2
 [7]: https://img.shields.io/maven-central/v/org.kocakosm/jblake2.svg
 [8]: https://central.sonatype.com/search?q=g%3Aorg.kocakosm+a%3Ajblake2
 [9]: https://javadoc.io/badge/org.kocakosm/jblake2.svg
 [10]: https://javadoc.io/doc/org.kocakosm/jblake2
 [11]: https://img.shields.io/badge/license-LGPL_v3-4383c3.svg
 [12]: https://www.gnu.org/licenses/lgpl-3.0.txt
 [13]: https://tools.ietf.org/html/rfc7693
 [14]: https://github.com/jvm-repo-rebuild/reproducible-central
 [15]: https://reproducible-builds.org
