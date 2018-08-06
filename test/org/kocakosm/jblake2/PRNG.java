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

import java.util.Random;

/**
 * Utility class to generate random data for tests.
 *
 * @author Osman Koçak
 */
final class PRNG
{
	private static final Random PRNG = new Random();

	static byte[] nextBytes(int count)
	{
		if (count < 0) {
			throw new IllegalArgumentException();
		}
		byte[] bytes = new byte[count];
		PRNG.nextBytes(bytes);
		return bytes;
	}

	static int nextInt(int startInclusive, int endExclusive)
	{
		if (startInclusive < 0 || endExclusive <= startInclusive) {
			throw new IllegalArgumentException();
		}
		return startInclusive + PRNG.nextInt(endExclusive - startInclusive);
	}

	private PRNG()
	{
		// See "Effective Java" (Item 4)
		throw new AssertionError("Not meant to be instantiated");
	}
}
