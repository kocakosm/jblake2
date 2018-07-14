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
 * Little-endian encoding and decoding.
 *
 * @author Osman Koçak
 */
final class LittleEndian
{
	/**
	 * Encodes the given {@code long} value using little-endian byte
	 * ordering convention.
	 *
	 * @param n the {@code long} value to encode.
	 *
	 * @return the encoded value.
	 */
	public static byte[] encode(long n)
	{
		byte[] out = new byte[8];
		encode(n, out, 0);
		return out;
	}

	/**
	 * Encodes the given {@code long} value using little-endian byte
	 * ordering convention into the given array, starting at the given
	 * offset.
	 *
	 * @param n the {@code long} value to encode.
	 * @param out the output buffer.
	 * @param off the output offset.
	 *
	 * @throws NullPointerException if {@code out} is {@code null}.
	 * @throws IndexOutOfBoundsException if {@code off} is negative or if
	 *	{@code out}'s length is lower than {@code off + 8}.
	 */
	public static void encode(long n, byte[] out, int off)
	{
		out[off] = (byte) n;
		out[off + 1] = (byte) (n >>> 8);
		out[off + 2] = (byte) (n >>> 16);
		out[off + 3] = (byte) (n >>> 24);
		out[off + 4] = (byte) (n >>> 32);
		out[off + 5] = (byte) (n >>> 40);
		out[off + 6] = (byte) (n >>> 48);
		out[off + 7] = (byte) (n >>> 56);
	}

	/**
	 * Decodes the first 8 bytes of the given array into a {@code long}
	 * value using little-endian byte ordering convention.
	 *
	 * @param in the encoded value.
	 *
	 * @return the decoded {@code long} value.
	 *
	 * @throws NullPointerException if {@code in} is {@code null}.
	 * @throws IndexOutOfBoundsException if {@code in}'s length is lower
	 *	than {@code 8}.
	 */
	public static long decodeLong(byte[] in)
	{
		return decodeLong(in, 0);
	}

	/**
	 * Decodes the first 8 bytes starting at {@code off} of the given array
	 * into a {@code long} value using little-endian byte ordering
	 * convention.
	 *
	 * @param in the encoded value.
	 * @param off the input offset.
	 *
	 * @return the decoded {@code long} value.
	 *
	 * @throws NullPointerException if {@code in} is {@code null}.
	 * @throws IndexOutOfBoundsException if {@code off} is negative or if
	 *	{@code in}'s length is lower than {@code off + 8}.
	 */
	public static long decodeLong(byte[] in, int off)
	{
		return (long) (in[off] & 0xFF)
			| ((long) (in[off + 1] & 0xFF) << 8)
			| ((long) (in[off + 2] & 0xFF) << 16)
			| ((long) (in[off + 3] & 0xFF) << 24)
			| ((long) (in[off + 4] & 0xFF) << 32)
			| ((long) (in[off + 5] & 0xFF) << 40)
			| ((long) (in[off + 6] & 0xFF) << 48)
			| ((long) (in[off + 7] & 0xFF) << 56);
	}

	private LittleEndian()
	{
		// See "Effective Java" (Item 4)
		throw new AssertionError("Not meant to be instantiated");
	}
}
