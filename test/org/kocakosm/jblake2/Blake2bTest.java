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

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 * {@link Blake2b}'s unit tests.
 *
 * @author Osman Koçak
 */
public final class Blake2bTest
{
	private static final Random PRNG = new Random();
	private static final byte[] DATA = BaseEncoding.BASE_16.decode("a55242b138855bc1");
	private static final byte[] HASH = BaseEncoding.BASE_16.decode("5d24a4f6a40996d4");

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
		Executable toTest = () -> new Blake2b(null, 8);
		assertThrows(NullPointerException.class, toTest);
	}

	@Test
	public void testKeyedConstructorWithKeyLengthGreaterThan64()
	{
		Executable toTest = () -> new Blake2b(new byte[65], 8);
		assertThrows(IllegalArgumentException.class, toTest);
	}

	@Test
	public void testKeyedConstructorWithNegativeDigestLength()
	{
		Executable toTest = () -> new Blake2b(new byte[0], -1);
		assertThrows(IllegalArgumentException.class, toTest);
	}

	@Test
	public void testKeyedConstructorWithDigestLengthGreaterThan64()
	{
		Executable toTest = () -> new Blake2b(new byte[0], 65);
		assertThrows(IllegalArgumentException.class, toTest);
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

	@Test
	public void testUpdateWithArrayRangeWithNegativeOffset()
	{
		Blake2b blake2b = new Blake2b(HASH.length);
		Executable toTest = () -> blake2b.update(DATA, -1, DATA.length);
		assertThrows(IndexOutOfBoundsException.class, toTest);
	}

	@Test
	public void testUpdateWithArrayRangeWithNegativeLength()
	{
		Blake2b blake2b = new Blake2b(HASH.length);
		Executable toTest = () -> blake2b.update(DATA, 0, -1);
		assertThrows(IndexOutOfBoundsException.class, toTest);
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
		Resource resource = Resource.find("blake2b-test-vectors.json", getClass());
		for (TestVector testVector : TestVectors.read(resource.getURL())) {
			checkTestVector(testVector);
		}
	}

	private void checkTestVector(TestVector testVector)
	{
		byte[] key = testVector.getKey();
		byte[] input = testVector.getInput();
		byte[] expected = testVector.getOutput();
		Blake2b blake2b = new Blake2b(key, expected.length);
		assertArrayEquals(expected, blake2b.digest(input));
	}
}
