/*----------------------------------------------------------------------------*
 * This file is part of JBlake2.                                              *
 * Copyright (C) Osman Koçak <kocakosm@gmail.com>                             *
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

/**
 * {@link Preconditions}'s unit tests.
 *
 * @author Osman Koçak
 */
public final class PreconditionsTest
{
	@Test
	public void testCheckArgumentSuccess()
	{
		checkArgument(true);
	}

	@Test
	public void testCheckArgumentFailure()
	{
		assertThrows(IllegalArgumentException.class, () -> checkArgument(false));
	}

	@Test
	public void testCheckStateSuccess()
	{
		checkState(true);
	}

	@Test
	public void testCheckStateFailure()
	{
		assertThrows(IllegalStateException.class, () -> checkState(false));
	}

	@Test
	public void testCheckBoundsSuccess()
	{
		checkBounds(new byte[5], 1, 2);
	}

	@Test
	public void testCheckBoundsFailureOnInvalidRange()
	{
		byte[] b = new byte[5];
		assertThrows(IllegalArgumentException.class, () -> checkBounds(b, -1, 2));
		assertThrows(IllegalArgumentException.class, () -> checkBounds(b, 0, -1));
	}

	@Test
	public void testCheckBoundsFailureOnOutOfBoundsRange()
	{
		byte[] b = new byte[5];
		assertThrows(IndexOutOfBoundsException.class, () -> checkBounds(b, 5, 1));
		assertThrows(IndexOutOfBoundsException.class, () -> checkBounds(b, 2, 4));
	}

	@Test
	public void testConstructor() throws Exception
	{
		Class<Preconditions> c = Preconditions.class;
		assertEquals(1, c.getDeclaredConstructors().length);
		Constructor<Preconditions> constructor = c.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		assertThrows(AssertionError.class, () -> invoke(constructor));
	}

	private void invoke(Constructor<?> constructor) throws Throwable
	{
		try {
			constructor.newInstance();
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}
}