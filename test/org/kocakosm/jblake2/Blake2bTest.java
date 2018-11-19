/*----------------------------------------------------------------------------*
 * This file is part of JBlake2.                                              *
 * Copyright (C) 2017-2018 Osman Koçak <kocakosm@gmail.com>                   *
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

import static org.junit.jupiter.api.Assertions.*;

import org.kocakosm.pitaya.io.Resource;
import org.kocakosm.pitaya.util.BaseEncoding;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 * {@link Blake2b}'s unit tests.
 *
 * @author Osman Koçak
 */
public final class Blake2bTest
{
	private static final byte[] DATA = BaseEncoding.BASE_16.decode("a5b1");
	private static final byte[] HASH = BaseEncoding.BASE_16.decode("80b6");

	@Test
	public void testUnkeyedConstructorWithNegativeDigestLength()
	{
		Executable toTest = () -> new Blake2b(-1);
		assertThrows(IllegalArgumentException.class, toTest);
	}

	@Test
	public void testUnkeyedConstructorWithDigestLengthGreaterThan64()
	{
		Executable toTest = () -> new Blake2b(65);
		assertThrows(IllegalArgumentException.class, toTest);
	}

	@Test
	public void testKeyedConstructorWithNullKey()
	{
		Executable toTest = () -> new Blake2b(8, null);
		assertThrows(NullPointerException.class, toTest);
	}

	@Test
	public void testKeyedConstructorWithKeyLengthGreaterThan64()
	{
		Executable toTest = () -> new Blake2b(8, new byte[65]);
		assertThrows(IllegalArgumentException.class, toTest);
	}

	@Test
	public void testKeyedConstructorWithNegativeDigestLength()
	{
		Executable toTest = () -> new Blake2b(-1, new byte[0]);
		assertThrows(IllegalArgumentException.class, toTest);
	}

	@Test
	public void testKeyedConstructorWithDigestLengthGreaterThan64()
	{
		Executable toTest = () -> new Blake2b(65, new byte[0]);
		assertThrows(IllegalArgumentException.class, toTest);
	}

	@Test
	public void testKeyedConstructorWithEmptyKey()
	{
		Blake2b unkeyed = new Blake2b(HASH.length);
		Blake2b keyed = new Blake2b(HASH.length, new byte[0]);
		assertArrayEquals(unkeyed.digest(DATA), keyed.digest(DATA));
	}

	@Test
	public void testAlgorithm()
	{
		assertEquals("BLAKE2b", new Blake2b(HASH.length).algorithm());
	}

	@Test
	public void testBurn()
	{
		Blake2b blake2b = new Blake2b(HASH.length, PRNG.nextBytes(64));
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
		int length = PRNG.nextInt(1, 65);
		assertEquals(length, new Blake2b(length).length());
	}

	@Test
	public void testReset()
	{
		Blake2b blake2b = new Blake2b(HASH.length);
		blake2b.update(DATA).reset();
		assertArrayEquals(HASH, blake2b.digest(DATA));
	}

	@Test
	public void testUpdateWithArrayRangeWithNegativeOffset()
	{
		Blake2b blake2b = new Blake2b(HASH.length);
		Executable toTest = () -> blake2b.update(DATA, -1, DATA.length);
		assertThrows(IllegalArgumentException.class, toTest);
	}

	@Test
	public void testUpdateWithArrayRangeWithNegativeLength()
	{
		Blake2b blake2b = new Blake2b(HASH.length);
		Executable toTest = () -> blake2b.update(DATA, 0, -1);
		assertThrows(IllegalArgumentException.class, toTest);
	}

	@Test
	public void testUpdateWithArrayRangeWithEmptyArrayAndPositiveLength()
	{
		Blake2b blake2b = new Blake2b(HASH.length);
		Executable toTest = () -> blake2b.update(new byte[0], 0, 1);
		assertThrows(IndexOutOfBoundsException.class, toTest);
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
		byte[] random = PRNG.nextBytes(129);
		for (byte b : random) {
			blake2b.update(b);
		}
		assertArrayEquals(blake2b.digest(), blake2b.digest(random));
	}

	@Test
	public void testDirectDigest()
	{
		Blake2b blake2b = new Blake2b(HASH.length);
		assertArrayEquals(HASH, blake2b.digest(DATA));
		assertArrayEquals(HASH, blake2b.digest(DATA));
	}

	@Test
	public void checkTestVectors()
	{
		Resource resource = Resource.find("blake2b-test-vectors.json", getClass());
		TestVectors.read(resource.getURL()).forEach(vector -> {
			byte[] key = vector.getKey();
			byte[] input = vector.getInput();
			byte[] expected = vector.getOutput();
			Blake2b blake2b = new Blake2b(expected.length, key);
			assertArrayEquals(expected, blake2b.digest(input));
		});
	}
}
