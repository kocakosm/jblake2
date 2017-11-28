/*----------------------------------------------------------------------------*
 * This file is part of JBlake2.                                              *
 * Copyright (C) 2017 Osman Koçak <kocakosm@gmail.com>                        *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify it    *
 * under the terms of the GNU Lesser General Public License as published by   *
 * the Free Software Foundation, either version 3 of the License, or (at your *
 * option) any later version.                                                 *
 * This program is distributed in the hope that it will be useful, but        *
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY *
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public     *
 * License for more details.                                                  *
 * You should have received a copy of the GNU Lesser General Public License   *
 * along with this program. If not, see <http://www.gnu.org/licenses/>.       *
 *----------------------------------------------------------------------------*/

package org.kocakosm.jblake2;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.Test;

/**
 * {@link Blake2b}'s unit tests.
 *
 * @author Osman Koçak
 */
public final class Blake2bTest
{
	private static final Random PRNG = new Random();
	private static final byte[] DATA = Base16.decode("a55242b138855bc1");
	private static final byte[] HASH = Base16.decode("5d24a4f6a40996d4");

	@Test(expected = IllegalArgumentException.class)
	public void testUnkeyedConstructorWithNegativeDigestLength()
	{
		new Blake2b(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnkeyedConstructorWithDigestLengthGreaterThan64()
	{
		new Blake2b(65);
	}

	@Test(expected = NullPointerException.class)
	public void testKeyedConstructorWithNullKey()
	{
		new Blake2b(null, 8);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testKeyedConstructorWithKeyLengthGreaterThan64()
	{
		new Blake2b(new byte[65], 8);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testKeyedConstructorWithNegativeDigestLength()
	{
		new Blake2b(new byte[0], -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testKeyedConstructorWithDigestLengthGreaterThan64()
	{
		new Blake2b(new byte[0], 65);
	}

	@Test
	public void testKeyedConstructorWithEmptyKey()
	{
		Blake2b unkeyed = new Blake2b(HASH.length);
		Blake2b keyed = new Blake2b(new byte[0], HASH.length);
		assertArrayEquals(unkeyed.digest(DATA), keyed.digest(DATA));
	}

	@Test
	public void testBurn()
	{
		byte[] key = new byte[32];
		PRNG.nextBytes(key);
		Blake2b blake2b = new Blake2b(key, HASH.length);
		blake2b.update(DATA);
		blake2b.burn();
		assertArrayEquals(HASH, blake2b.digest(DATA));
	}

	@Test
	public void testCopy()
	{
		Blake2b blake2b = new Blake2b(HASH.length);
		blake2b.update(DATA);
		Blake2b copy = blake2b.copy();
		assertFalse(blake2b == copy);
		blake2b.update(DATA);
		copy.update(DATA);
		assertArrayEquals(blake2b.digest(), copy.digest());
	}

	@Test
	public void testLength()
	{
		int length = PRNG.nextInt(63) + 1;
		assertEquals(length, new Blake2b(length).length());
	}

	@Test
	public void testReset()
	{
		Blake2b blake2b = new Blake2b(HASH.length);
		blake2b.update(DATA).reset();
		assertArrayEquals(HASH, blake2b.digest(DATA));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testUpdateWithArrayRangeWithNegativeOffset()
	{
		Blake2b blake2b = new Blake2b(HASH.length);
		blake2b.update(DATA, -1, DATA.length);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testUpdateWithArrayRangeWithNegativeLength()
	{
		Blake2b blake2b = new Blake2b(HASH.length);
		blake2b.update(DATA, 0, -1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testUpdateWithArrayRangeWithEmptyArrayAndPositiveLength()
	{
		Blake2b blake2b = new Blake2b(HASH.length);
		blake2b.update(new byte[0], 0, 1);
	}

	@Test
	public void testUpdateAndDigest()
	{
		Blake2b blake2b = new Blake2b(HASH.length);
		for (byte b : DATA) {
			blake2b.update(b);
		}
		assertArrayEquals(HASH, blake2b.digest());
		blake2b.update(DATA);
		assertArrayEquals(HASH, blake2b.digest());
	}

	@Test
	public void testDirectDigest()
	{
		Blake2b blake2b = new Blake2b(HASH.length);
		assertArrayEquals(HASH, blake2b.digest(DATA));
		assertArrayEquals(HASH, blake2b.digest(DATA));
	}

	@Test
	public void checkTestVectors() throws IOException
	{
		// test vectors from https://blake2.net and RFC 7693
		InputStream file = getResource("test-vectors.txt");
		List<String> lines = readLines(file, StandardCharsets.UTF_8);
		for (int i = 0; i < lines.size(); i += 4) {
			byte[] in = readLineValue(lines.get(i));
			byte[] key = readLineValue(lines.get(i + 1));
			byte[] hash = readLineValue(lines.get(i + 2));
			Blake2b blake2b = new Blake2b(key, hash.length);
			assertArrayEquals(hash, blake2b.digest(in));
		}
	}

	private InputStream getResource(String name)
	{
		return getClass().getResourceAsStream(name);
	}

	private List<String> readLines(InputStream in, Charset charset)
		throws IOException
	{
		try (BufferedReader reader = getReader(in, charset)) {
			List<String> lines = new ArrayList<>();
			String line = reader.readLine();
			while (line != null) {
				lines.add(line);
				line = reader.readLine();
			}
			return Collections.unmodifiableList(lines);
		}
	}

	private BufferedReader getReader(InputStream in, Charset charset)
	{
		return new BufferedReader(new InputStreamReader(in, charset));
	}

	private byte[] readLineValue(String line)
	{
		return Base16.decode(line.split(":")[1].trim());
	}
}
