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

import java.util.Arrays;

/**
 * The BLAKE2b digest algorithm. Instances of this class are not thread safe.
 * This class implements the BLAKE2b algorithm as specified in RFC 7693. The
 * <a href="https://blake2.net/blake2.pdf">original paper</a> defines some
 * additional variants with features such as salting, personalization and tree
 * hashing. These features are considered optional and not covered by the RFC.
 * BLAKE2b can be directly keyed, making it functionally equivalent to a Message
 * Authentication Code (it does not require a special construction like HMAC).
 *
 * @see <a href="https://blake2.net">blake2.net</a>
 * @see <a href="https://tools.ietf.org/html/rfc7693">RFC 7693</a>
 *
 * @author Osman Koçak
 */
public final class Blake2b
{
	private static final int BLOCK_LENGTH = 128;
	private static final long IV[] = {
		0X6A09E667F3BCC908L, 0XBB67AE8584CAA73BL, 0X3C6EF372FE94F82BL,
		0XA54FF53A5F1D36F1L, 0X510E527FADE682D1L, 0X9B05688C2B3E6C1FL,
		0X1F83D9ABFB41BD6BL, 0X5BE0CD19137E2179L
	};
	private static final int[][] SIGMA = {
		{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
		{14, 10, 4, 8, 9, 15, 13, 6, 1, 12, 0, 2, 11, 7, 5, 3},
		{11, 8, 12, 0, 5, 2, 15, 13, 10, 14, 3, 6, 7, 1, 9, 4},
		{7, 9, 3, 1, 13, 12, 11, 14, 2, 6, 5, 10, 4, 0, 15, 8},
		{9, 0, 5, 7, 2, 4, 10, 15, 14, 1, 11, 12, 6, 8, 3, 13},
		{2, 12, 6, 10, 0, 11, 8, 3, 4, 13, 7, 5, 15, 14, 1, 9},
		{12, 5, 1, 15, 14, 13, 4, 10, 0, 7, 6, 3, 9, 2, 8, 11},
		{13, 11, 7, 14, 12, 1, 3, 9, 5, 0, 15, 4, 8, 6, 2, 10},
		{6, 15, 14, 9, 11, 3, 0, 8, 12, 2, 13, 7, 1, 4, 10, 5},
		{10, 2, 8, 4, 7, 6, 1, 5, 15, 11, 9, 14, 3, 12, 13, 0},
		{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
		{14, 10, 4, 8, 9, 15, 13, 6, 1, 12, 0, 2, 11, 7, 5, 3}
	};

	private final int digestLength;
	private final byte[] buffer;
	private byte[] key;
	private long[] h; // internal state
	private long t0; // counter's least significant bits
	private long t1; // counter's most significant bits
	private int c; // number of bytes in the buffer

	/**
	 * Creates a new unkeyed {@code Blake2b} instance.
	 *
	 * @param digestLength the desired digest's length (in bytes).
	 *
	 * @throws IllegalArgumentException if {@code digestLength} is not in
	 *	the {@code [1, 64]} range.
	 */
	public Blake2b(int digestLength)
	{
		this(new byte[0], digestLength);
	}

	/**
	 * Creates a new keyed {@code Blake2b} instance. If the key is empty,
	 * then the created instance is equivalent to an unkeyed digest. The
	 * given key can be safely erased from memory after this constructor has
	 * been called.
	 *
	 * @param key the key to use.
	 * @param digestLength the desired digest's length (in bytes).
	 *
	 * @throws NullPointerException if {@code key} is {@code null}.
	 * @throws IllegalArgumentException if {@code key}'s length is greater
	 *	than {@code 64} or if {@code digestLength} is not in the
	 *	{@code [1, 64]} range.
	 */
	public Blake2b(byte[] key, int digestLength)
	{
		if (key.length > 64 || digestLength < 1 || digestLength > 64) {
			throw new IllegalArgumentException();
		}
		this.buffer = new byte[BLOCK_LENGTH];
		this.key = Arrays.copyOf(key, key.length);
		this.digestLength = digestLength;
		reset();
	}

	private Blake2b(Blake2b digest)
	{
		this.c = digest.c;
		this.buffer = Arrays.copyOf(digest.buffer, digest.buffer.length);
		this.key = Arrays.copyOf(digest.key, digest.key.length);
		this.digestLength = digest.digestLength;
		this.h = Arrays.copyOf(digest.h, digest.h.length);
		this.t0 = digest.t0;
		this.t1 = digest.t1;
	}

	/**
	 * Erases the key and resets the digest, making this instance
	 * functionally equivalent to a newly created unkeyed digest. If this
	 * instance is already unkeyed, calling this method has no other effect
	 * than resetting the digest.
	 */
	public void burn()
	{
		Arrays.fill(key, (byte) 0);
		Arrays.fill(buffer, (byte) 0); // buffer may contain the key...
		key = new byte[0];
		reset();
	}

	/**
	 * Returns a copy of this object.
	 *
	 * @return a copy of this object.
	 */
	public Blake2b copy()
	{
		return new Blake2b(this);
	}

	/**
	 * Returns the digest's length (in bytes).
	 *
	 * @return the digest's length (in bytes).
	 */
	public int length()
	{
		return digestLength;
	}

	/**
	 * Resets the digest. Note that this does not erase the key.
	 *
	 * @return this object.
	 */
	public Blake2b reset()
	{
		t0 = 0L;
		t1 = 0L;
		h = Arrays.copyOf(IV, IV.length);
		h[0] ^= digestLength | (key.length << 8) | 0x01010000;
		if (key.length > 0) {
			System.arraycopy(key, 0, buffer, 0, key.length);
			Arrays.fill(buffer, key.length, BLOCK_LENGTH, (byte) 0);
			c = BLOCK_LENGTH;
		} else {
			c = 0;
		}
		return this;
	}

	/**
	 * Updates the digest using the given byte.
	 *
	 * @param input the byte with which to update the digest.
	 *
	 * @return this object.
	 */
	public Blake2b update(byte input)
	{
		if (c == BLOCK_LENGTH) {
			processBuffer(false);
		}
		buffer[c++] = input;
		return this;
	}

	/**
	 * Updates the digest using the given array of bytes.
	 *
	 * @param input the array of bytes with which to update the digest.
	 *
	 * @return this object.
	 *
	 * @throws NullPointerException if {@code input} is {@code null}.
	 */
	public Blake2b update(byte... input)
	{
		return update(input, 0, input.length);
	}

	/**
	 * Updates the digest using the specified number of bytes from the given
	 * array, starting at the specified offset.
	 *
	 * @param input the array of bytes.
	 * @param off the offset to start from in the array, inclusive.
	 * @param len the number of bytes to use, starting at {@code off}.
	 *
	 * @return this object.
	 *
	 * @throws NullPointerException if {@code input} is {@code null}.
	 * @throws IndexOutOfBoundsException if {@code off} is negative or if
	 *	{@code len} is negative or if {@code off + len} is greater than
	 *	{@code input}'s length.
	 */
	public Blake2b update(byte[] input, int off, int len)
	{
		if (off < 0 || len < 0 || off + len > input.length) {
			throw new IndexOutOfBoundsException();
		}
		int index = off;
		int remaining = len;
		while (remaining > 0) {
			if (c == BLOCK_LENGTH) {
				processBuffer(false);
			}
			int cpLen = Math.min(BLOCK_LENGTH - c, remaining);
			System.arraycopy(input, index, buffer, c, cpLen);
			remaining -= cpLen;
			index += cpLen;
			c += cpLen;
		}
		return this;
	}

	/**
	 * Performs a final update on the digest using the specified array of
	 * bytes, then completes the digest computation. That is, this method
	 * first calls {@link #update(byte...)}, passing the input array to the
	 * update method, then calls {@link #digest()}. Note that the digest is
	 * reset after this call is made.
	 *
	 * @param input the array of bytes with which to update the digest
	 *	before completing its computation.
	 *
	 * @return the resulting digest.
	 *
	 * @throws NullPointerException if {@code input} is {@code null}.
	 */
	public byte[] digest(byte... input)
	{
		return digest(input, 0, input.length);
	}

	/**
	 * Performs a final update on the digest using the specified data bytes,
	 * then completes the digest computation. That is, this method first
	 * calls {@link #update(byte[], int, int)}, passing the input array to
	 * the update method, then calls {@link #digest()}. Note that the digest
	 * is reset after this call is made.
	 *
	 * @param input the array of bytes.
	 * @param off the offset to start from in the array, inclusive.
	 * @param len the number of bytes to use, starting at {@code off}.
	 *
	 * @return the resulting digest.
	 *
	 * @throws NullPointerException if {@code input} is {@code null}.
	 * @throws IndexOutOfBoundsException if {@code off} is negative or if
	 *	{@code len} is negative or if {@code off + len} is greater than
	 *	{@code input}'s length.
	 */
	public byte[] digest(byte[] input, int off, int len)
	{
		return update(input, off, len).digest();
	}

	/**
	 * Completes the hash computation. Note that the digest is reset after
	 * this call is made.
	 *
	 * @return the resulting digest.
	 */
	public byte[] digest()
	{
		Arrays.fill(buffer, c, BLOCK_LENGTH, (byte) 0);
		processBuffer(true);
		byte[] out = new byte[digestLength];
		int i = 0;
		while (i < h.length && i * 8 < digestLength - 8) {
			LittleEndian.encode(h[i], out, i * 8);
			i++;
		}
		byte[] last = LittleEndian.encode(h[i]);
		System.arraycopy(last, 0, out, i * 8, digestLength - (i * 8));
		reset();
		return out;
	}

	private void processBuffer(boolean lastBlock)
	{
		t0 += c;
		if (t0 == 0L && c > 0) { // bitwise overflow
			t1++;
		}
		c = 0;
		F(buffer, lastBlock);
	}

	private void F(byte[] input, boolean lastBlock)
	{
		long[] v = new long[16];
		System.arraycopy(h, 0, v, 0, h.length);
		System.arraycopy(IV, 0, v, h.length, IV.length);
		v[12] ^= t0;
		v[13] ^= t1;
		if (lastBlock) {
			v[14] = ~v[14];
		}
		long[] m = new long[16];
		for (int j = 0; j < 16; j++) {
			m[j] = LittleEndian.decodeLong(input, j * 8);
		}
		for (int i = 0; i < 12; i++) {
			G(v, 0, 4,  8, 12, m[SIGMA[i][ 0]], m[SIGMA[i][ 1]]);
			G(v, 1, 5,  9, 13, m[SIGMA[i][ 2]], m[SIGMA[i][ 3]]);
			G(v, 2, 6, 10, 14, m[SIGMA[i][ 4]], m[SIGMA[i][ 5]]);
			G(v, 3, 7, 11, 15, m[SIGMA[i][ 6]], m[SIGMA[i][ 7]]);
			G(v, 0, 5, 10, 15, m[SIGMA[i][ 8]], m[SIGMA[i][ 9]]);
			G(v, 1, 6, 11, 12, m[SIGMA[i][10]], m[SIGMA[i][11]]);
			G(v, 2, 7,  8, 13, m[SIGMA[i][12]], m[SIGMA[i][13]]);
			G(v, 3, 4,  9, 14, m[SIGMA[i][14]], m[SIGMA[i][15]]);
		}
		for (int i = 0; i < h.length; i++) {
			h[i] ^= v[i] ^ v[i + 8];
		}
	}

	private void G(long[] v, int a, int b, int c, int d, long x, long y)
	{
		v[a] += v[b] + x;
		v[d] = Long.rotateRight(v[d] ^ v[a], 32);
		v[c] += v[d];
		v[b] = Long.rotateRight(v[b] ^ v[c], 24);
		v[a] += v[b] + y;
		v[d] = Long.rotateRight(v[d] ^ v[a], 16);
		v[c] += v[d];
		v[b] = Long.rotateRight(v[b] ^ v[c], 63);
	}
}
