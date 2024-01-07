package org.zerocraft.server.entity;

import org.zerocraft.server.MathHelper;

public class Vector3 {
	public float x;
	public float y;
	public float z;

	public Vector3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3 subtract(Vector3 vector) {
		return new Vector3(this.x - vector.x, this.y - vector.y, this.z - vector.z);
	}

	public Vector3 normalize() {
		float f1 = (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		return new Vector3(this.x / f1, this.y / f1, this.z / f1);
	}

	public Vector3 add(float x, float y, float z) {
		return new Vector3(this.x + x, this.y + y, this.z + z);
	}

	public float distanceTo(Vector3 t) {
		float f2 = t.x - this.x;
		float f3 = t.y - this.y;
		float f4 = t.z - this.z;
		return MathHelper.sqrt(f2 * f2 + f3 * f3 + f4 * f4);
	}

	public float distanceToSqr(Vector3 t) {
		float f2 = t.x - this.x;
		float f3 = t.y - this.y;
		float f4 = t.z - this.z;
		return f2 * f2 + f3 * f3 + f4 * f4;
	}

	public Vector3 clipX(Vector3 t, float xa) {
		float f3 = t.x - this.x;
		float f4 = t.y - this.y;
		float t1 = t.z - this.z;
		return f3 * f3 < 1.0E-7F ? null
				: (xa = (xa - this.x) / f3) >= 0.0F && xa <= 1.0F
						? new Vector3(this.x + f3 * xa, this.y + f4 * xa, this.z + t1 * xa)
						: null;
	}

	public Vector3 clipY(Vector3 t, float ya) {
		float f3 = t.x - this.x;
		float f4 = t.y - this.y;
		float t1 = t.z - this.z;
		return f4 * f4 < 1.0E-7F ? null
				: (ya = (ya - this.y) / f4) >= 0.0F && ya <= 1.0F
						? new Vector3(this.x + f3 * ya, this.y + f4 * ya, this.z + t1 * ya)
						: null;
	}

	public Vector3 clipZ(Vector3 t, float za) {
		float f3 = t.x - this.x;
		float f4 = t.y - this.y;
		float t1;
		return (t1 = t.z - this.z) * t1 < 1.0E-7F ? null
				: (za = (za - this.z) / t1) >= 0.0F && za <= 1.0F
						? new Vector3(this.x + f3 * za, this.y + f4 * za, this.z + t1 * za)
						: null;
	}

	@Override
	public String toString() {
		return String.format("%f,%f,%f", this.x, this.y, this.z);
	}
}