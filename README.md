JBlake2 [![Build][1]][2] [![Coverage][3]][4] [![Version][5]][6] [![Javadocs][7]][8] [![License][9]][10]
=======================================================================================================

A pure Java (8+) implementation of BLAKE2 ([RFC 7693][11]).


Getting started
---------------

JBlake2 binaries are available from Maven central repositories.
Download the [latest jar][12] or get it directly from your favorite build tool:

Maven
```xml
  <dependency>
    <groupId>org.kocakosm</groupId>
    <artifactId>jblake2</artifactId>
    <version>0.3</version>
  </dependency>
```

Gradle
```groovy
  compile 'org.kocakosm:jblake2:0.3'
```

*Note: module name for the Java Module System is `org.kocakosm.jblake2`.*


Usage
-----

Browse [API docs][13] for the most recent release.


Contributing
------------

If you would like to contribute code, fork the [repository][14] and send a pull
request. When submitting code, please make sure to follow existing conventions
and style in order to keep the code as readable as possible.

Reporting errors or possible improvements is also a great way to help. Be sure
to not duplicate an existing issue by first browsing the [issue tracker][15].


License
-------

This program is free software: you can redistribute it and/or modify it under
the terms of the GNU Lesser General Public License as published by the Free
Software Foundation, either version 3 of the License, or (at your option) any
later version.

This program is distributed in the hope that it will be useful, but _without any
warranty;_ without even the implied warranty of _merchantability_ or _fitness
for a particular purpose_.

See the [GNU Lesser General Public License][16] for more details.


Contact
-------

If you have any question, feel free to send me an e-mail at kocakosm[@]gmail[dot]com
or ping me on [twitter][17].


 [1]: https://img.shields.io/travis/kocakosm/jblake2.svg
 [2]: https://travis-ci.org/kocakosm/jblake2
 [3]: https://img.shields.io/coveralls/kocakosm/jblake2.svg
 [4]: https://coveralls.io/github/kocakosm/jblake2
 [5]: https://img.shields.io/maven-central/v/org.kocakosm/jblake2.svg
 [6]: https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.kocakosm%22%20AND%20a%3A%22jblake2%22
 [7]: https://javadoc.io/badge/org.kocakosm/jblake2.svg
 [8]: https://javadoc.io/doc/org.kocakosm/jblake2
 [9]: https://img.shields.io/badge/license-LGPL_v3-4383c3.svg
 [10]: https://www.gnu.org/licenses/lgpl.txt
 [11]: https://tools.ietf.org/html/rfc7693
 [12]: https://search.maven.org/remote_content?g=org.kocakosm&a=jblake2&v=LATEST
 [13]: http://www.javadoc.io/doc/org.kocakosm/jblake2
 [14]: https://bitbucket.org/kocakosm/jblake2
 [15]: https://bitbucket.org/kocakosm/jblake2/issues?status=new&status=open
 [16]: http://www.gnu.org/licenses/lgpl-3.0-standalone.html
 [17]: https://twitter.com/kocakosm
