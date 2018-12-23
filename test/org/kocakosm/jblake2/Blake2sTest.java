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
 * {@link Blake2s}'s unit tests.
 *
 * @author Osman Koçak
 */
public final class Blake2sTest
{
	private static final byte[] DATA = BaseEncoding.BASE_16.decode("a5b1");
	private static final byte[] HASH = BaseEncoding.BASE_16.decode("df55");

	@Test
	public void testUnkeyedConstructorWithNegativeDigestLength()
	{
		Executable toTest = () -> new Blake2s(-1);
		assertThrows(IllegalArgumentException.class, toTest);
	}

	@Test
	public void testUnkeyedConstructorWithDigestLengthGreaterThan32()
	{
		Executable toTest = () -> new Blake2s(33);
		assertThrows(IllegalArgumentException.class, toTest);
	}

	@Test
	public void testKeyedConstructorWithNullKey()
	{
		Executable toTest = () -> new Blake2s(8, null);
		assertThrows(NullPointerException.class, toTest);
	}

	@Test
	public void testKeyedConstructorWithKeyLengthGreaterThan32()
	{
		Executable toTest = () -> new Blake2s(8, new byte[33]);
		assertThrows(IllegalArgumentException.class, toTest);
	}

	@Test
	public void testKeyedConstructorWithNegativeDigestLength()
	{
		Executable toTest = () -> new Blake2s(-1, new byte[0]);
		assertThrows(IllegalArgumentException.class, toTest);
	}

	@Test
	public void testKeyedConstructorWithDigestLengthGreaterThan32()
	{
		Executable toTest = () -> new Blake2s(33, new byte[0]);
		assertThrows(IllegalArgumentException.class, toTest);
	}

	@Test
	public void testKeyedConstructorWithEmptyKey()
	{
		Blake2s unkeyed = new Blake2s(HASH.length);
		Blake2s keyed = new Blake2s(HASH.length, new byte[0]);
		assertArrayEquals(unkeyed.digest(DATA), keyed.digest(DATA));
	}

	@Test
	public void testAlgorithm()
	{
		assertEquals("BLAKE2s", new Blake2s(HASH.length).algorithm());
	}

	@Test
	public void testLength()
	{
		int length = PRNG.nextInt(1, 33);
		assertEquals(length, new Blake2s(length).length());
	}

	@Test
	public void testCopy()
	{
		Blake2s blake2s = new Blake2s(HASH.length);
		blake2s.update(DATA);
		Blake2s copy = blake2s.copy();
		assertNotSame(copy, blake2s);
		blake2s.update(DATA);
		copy.update(DATA);
		assertArrayEquals(blake2s.digest(), copy.digest());
	}

	@Test
	public void testReset()
	{
		Blake2s blake2s = new Blake2s(HASH.length);
		blake2s.update(DATA).reset();
		assertArrayEquals(HASH, blake2s.digest(DATA));
	}

	@Test
	public void testResetReturnsSameInstance()
	{
		Blake2s blake2s = new Blake2s(HASH.length);
		assertSame(blake2s, blake2s.reset());
	}

	@Test
	public void testBurn()
	{
		Blake2s blake2s = new Blake2s(HASH.length, PRNG.nextBytes(32));
		blake2s.update(DATA);
		blake2s.burn();
		assertArrayEquals(HASH, blake2s.digest(DATA));
	}

	@Test
	public void testBurnReturnsSameInstance()
	{
		Blake2s blake2s = new Blake2s(HASH.length);
		assertSame(blake2s, blake2s.burn());
	}

	@Test
	public void testUpdateWithArrayRangeWithNegativeOffset()
	{
		Blake2s blake2s = new Blake2s(HASH.length);
		Executable toTest = () -> blake2s.update(DATA, -1, DATA.length);
		assertThrows(IllegalArgumentException.class, toTest);
	}

	@Test
	public void testUpdateWithArrayRangeWithNegativeLength()
	{
		Blake2s blake2s = new Blake2s(HASH.length);
		Executable toTest = () -> blake2s.update(DATA, 0, -1);
		assertThrows(IllegalArgumentException.class, toTest);
	}

	@Test
	public void testUpdateWithArrayRangeWithEmptyArrayAndPositiveLength()
	{
		Blake2s blake2s = new Blake2s(HASH.length);
		Executable toTest = () -> blake2s.update(new byte[0], 0, 1);
		assertThrows(IndexOutOfBoundsException.class, toTest);
	}

	@Test
	public void testUpdateReturnsSameInstance()
	{
		Blake2s blake2s = new Blake2s(HASH.length);
		assertSame(blake2s, blake2s.update(DATA));
		assertSame(blake2s, blake2s.update(DATA[0]));
		assertSame(blake2s, blake2s.update(DATA, 1, 1));
	}

	@Test
	public void testUpdateAndDigest()
	{
		Blake2s blake2s = new Blake2s(HASH.length);
		for (byte b : DATA) {
			blake2s.update(b);
		}
		assertArrayEquals(HASH, blake2s.digest());
		blake2s.update(DATA);
		assertArrayEquals(HASH, blake2s.digest());
		byte[] random = PRNG.nextBytes(65);
		for (byte b : random) {
			blake2s.update(b);
		}
		assertArrayEquals(blake2s.digest(), blake2s.digest(random));
	}

	@Test
	public void testDirectDigest()
	{
		Blake2s blake2s = new Blake2s(HASH.length);
		assertArrayEquals(HASH, blake2s.digest(DATA));
		assertArrayEquals(HASH, blake2s.digest(DATA));
	}

	@Test
	public void checkTestVectors()
	{
		Resource resource = Resource.find("blake2s-test-vectors.json", getClass());
		TestVectors.read(resource.getURL()).forEach(vector -> {
			byte[] key = vector.getKey();
			byte[] input = vector.getInput();
			byte[] expected = vector.getOutput();
			Blake2s blake2s = new Blake2s(expected.length, key);
			assertArrayEquals(expected, blake2s.digest(input));
		});
	}
}
