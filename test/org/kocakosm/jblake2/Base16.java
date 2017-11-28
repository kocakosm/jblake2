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

/**
 * Base16 (RFC 4648) decoding.
 *
 * @author Osman Koçak
 */
final class Base16
{
	public static byte[] decode(String hex)
	{
		int len = hex.length();
		if (len % 2 != 0) {
			throw new IllegalArgumentException();
		}
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			int val = Integer.parseInt(hex.substring(i, i + 2), 16);
			data[i / 2] = (byte) (val & 0xFF);
		}
		return data;
	}

	private Base16()
	{
		/* ... */
	}
}
