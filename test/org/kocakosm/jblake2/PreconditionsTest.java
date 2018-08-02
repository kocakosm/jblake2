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

import static org.kocakosm.jblake2.Preconditions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

/**
 * {@link Preconditions}'s unit tests.
 *
 * @author Osman Koçak
 */
public final class PreconditionsTest
{
	@Test
	public void testCheckArgument()
	{
		checkArgument(true);
		assertThrows(IllegalArgumentException.class, () -> checkArgument(false));
	}

	@Test
	public void testCheckState()
	{
		checkState(true);
		assertThrows(IllegalStateException.class, () -> checkState(false));
	}

	@Test
	public void testCheckBounds()
	{
		byte[] b = new byte[5];
		checkBounds(b, 1, 2);
		assertThrows(IndexOutOfBoundsException.class, () -> checkBounds(b, -1, 2));
		assertThrows(IndexOutOfBoundsException.class, () -> checkBounds(b, 0, -1));
		assertThrows(IndexOutOfBoundsException.class, () -> checkBounds(b, 5, 1));
		assertThrows(IndexOutOfBoundsException.class, () -> checkBounds(b, 2, 4));
	}

	@Test
	public void testConstructor() throws Throwable
	{
		Class<Preconditions> c = Preconditions.class;
		assertEquals(1, c.getDeclaredConstructors().length);
		Constructor<Preconditions> constructor = c.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		Executable toTest = () -> {
			try {
				constructor.newInstance();
			} catch (Exception e) {
				throw e.getCause();
			}
		};
		AssertionError error = assertThrows(AssertionError.class, toTest);
		assertEquals(error.getMessage(), "Not meant to be instantiated");
	}
}
