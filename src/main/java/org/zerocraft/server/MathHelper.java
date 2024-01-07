package org.zerocraft.server;

public class MathHelper {
	private static float[] SINE_TABLE = new float[65536];

	public static float sin(float f) {
		return SINE_TABLE[(int) (f * 10430.378F) & 65535];
	}

	public static float cos(float f) {
		return SINE_TABLE[(int) (f * 10430.378F + 16384.0F) & 65535];
	}

	public static float sqrt(float f) {
		return (float) Math.sqrt(f);
	}

	public static float degToRad(float deg) {
		return (float) (deg * (Math.PI / 180));
	}

	static {
		for (int i = 0; i < 65536; ++i) {
			SINE_TABLE[i] = (float) Math.sin(i * Math.PI * 2.0D / 65536.0D);
		}
	}
}