package org.zerocraft.server.net;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.zerocraft.server.Utils;

public class ClassicDataOutputStream extends FilterOutputStream {
	public ClassicDataOutputStream(OutputStream out) {
		super(out);
	}

	public void writeSByte(byte b) throws IOException {
		this.out.write(b);
	}

	public void writeShort(short s) throws IOException {
		this.out.write(s >>> 8 & 0xFF);
		this.out.write(s >>> 0 & 0xFF);
	}

	public void writeInt(int v) throws IOException {
		this.out.write(v >>> 24 & 0xFF);
		this.out.write(v >>> 16 & 0xFF);
		this.out.write(v >>> 8 & 0xFF);
		this.out.write(v >>> 0 & 0xFF);
	}

	public void writeString(String str) throws IOException {
		if (str == null) {
			throw new IllegalArgumentException("The specified String was null");
		}
		String fStr = Utils.rightPad(str, 64, ' ');
		if (fStr.length() > 64) {
			fStr = fStr.substring(0, 64);
		}
		this.out.write(fStr.getBytes(StandardCharsets.US_ASCII));
	}

	public void writeBytes(byte[] bytes) throws IOException {
		byte[] fBytes = new byte[1024];
		System.arraycopy(bytes, 0, fBytes, 0, bytes.length <= 1024 ? bytes.length : 1024);
		this.out.write(fBytes);
	}
}
