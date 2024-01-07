package org.zerocraft.server.net;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ClassicDataInputStream extends FilterInputStream {
	protected ClassicDataInputStream(InputStream in) {
		super(in);
	}

	public byte readSByte() throws IOException {
		int data = this.in.read();
		if (data < 0) {
			throw new EOFException();
		}
		return (byte) data;
	}

	public short readShort() throws IOException {
		int data0 = this.in.read();
		int data1 = this.in.read();
		if ((data0 | data1) < 0) {
			throw new EOFException();
		}
		return (short) ((data0 << 8) + (data1 << 0));
	}

	public int readInt() throws IOException {
		int data0 = this.in.read();
		int data1 = this.in.read();
		int data2 = this.in.read();
		int data3 = this.in.read();
		if ((data0 | data1 | data2 | data3) < 0) {
			throw new EOFException();
		}
		return (data0 << 24) + (data1 << 16) + (data2 << 8) + (data3 << 0);
	}

	public String readString() throws IOException {
		byte[] buffer = new byte[64];
		this.readFully(buffer);
		return new String(buffer, StandardCharsets.US_ASCII).replaceAll("\\s++$", "");
	}

	public byte[] readBytes() throws IOException {
		byte[] buffer = new byte[1024];
		this.readFully(buffer);
		return buffer;
	}

	public void readFully(byte[] buffer) throws IOException {
		this.readFully(buffer, 0, buffer.length);
	}

	public void readFully(byte[] buffer, int offset, int length) throws IOException {
		int totalRead = 0;
//		Objects.checkFromIndexSize(offset, length, buffer.length);

		while (totalRead < length) {
			int count = this.in.read(buffer, offset + totalRead, length - totalRead);

			if (count < 0) {
				throw new EOFException();
			}

			totalRead += count;
		}
	}
}
