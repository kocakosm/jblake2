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

import static org.kocakosm.jblake2.LittleEndian.*;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

/**
 * {@link LittleEndian}'s unit tests.
 *
 * @author Osman Koçak
 */
public final class LittleEndianTest
{
	@Test
	public void testEncodeInt()
	{
		byte[] encoded = new byte[] {
			(byte) 0x33, (byte) 0x12, (byte) 0x74, (byte) 0x25
		};
		assertArrayEquals(encoded, encode(628363827));
	}

	@Test
	public void testDecodeInt()
	{
		byte[] encoded = new byte[] {
			(byte) 0x33, (byte) 0x12, (byte) 0x74, (byte) 0x25
		};
		assertEquals(628363827, decodeInt(encoded));
	}

	@Test
	public void testEncodeAndDecodeInt()
	{
		for (int i = 0; i < 10000; i++) {
			int n = PRNG.nextInt();
			assertEquals(n, decodeInt(encode(n)));
		}
	}

	@Test
	public void testEncodeLong()
	{
		byte[] encoded = {
			(byte) 0x90, (byte) 0xB1, (byte) 0x5C, (byte) 0x1D,
			(byte) 0x76, (byte) 0x7F, (byte) 0x5E, (byte) 0x1A
		};
		assertArrayEquals(encoded, encode(1900096238072410512L));
	}

	@Test
	public void testDecodeLong()
	{
		byte[] encoded = {
			(byte) 0x90, (byte) 0xB1, (byte) 0x5C, (byte) 0x1D,
			(byte) 0x76, (byte) 0x7F, (byte) 0x5E, (byte) 0x1A
		};
		assertEquals(1900096238072410512L, decodeLong(encoded));
	}

	@Test
	public void testEncodeAndDecodeLong()
	{
		for (int i = 0; i < 10000; i++) {
			long n = PRNG.nextLong();
			assertEquals(n, decodeLong(encode(n)));
		}
	}

	@Test
	public void testConstructor() throws Exception
	{
		Class<LittleEndian> c = LittleEndian.class;
		assertEquals(1, c.getDeclaredConstructors().length);
		Constructor<LittleEndian> constructor = c.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		assertThrows(AssertionError.class, () -> invoke(constructor));
	}

	private void invoke(Constructor constructor) throws Throwable
	{
		try {
			constructor.newInstance();
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}
}
