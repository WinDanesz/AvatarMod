package com.crowsofwar.gorecore.util;

import net.minecraft.util.math.Vec3d;

/**
 * A 3-dimensional vector.
 * 
 * @author CrowsOfWar
 */
public class Vector {
	
	private double cachedMagnitude;
	private double x, y, z;
	
	/**
	 * Creates a new vector at the origin point.
	 */
	public Vector() {
		this(0, 0, 0);
	}
	
	/**
	 * Creates using the coordinates (x, y, z).
	 * 
	 * @param x
	 *            X-position of the new vector
	 * @param y
	 *            Y-position of the new vector
	 * @param z
	 *            Z-position of the new vector
	 */
	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		recalcMagnitude();
	}
	
	/**
	 * Creates a copy of the given vector.
	 * 
	 * @param vec
	 *            Vector to copy
	 */
	public Vector(Vector vec) {
		this(vec.x, vec.y, vec.z);
		this.cachedMagnitude = vec.cachedMagnitude;
	}
	
	/**
	 * Creates a copy of the given Minecraft vector.
	 * 
	 * @param vec
	 *            Vector to copy
	 */
	public Vector(Vec3d vec) {
		this(vec.xCoord, vec.yCoord, vec.zCoord);
	}
	
	/**
	 * Get the x-coordinate of this vector.
	 */
	public double x() {
		return x;
	}
	
	public void setX(double x) {
		this.x = x;
		recalcMagnitude();
	}
	
	/**
	 * Get the y-coordinate of this vector.
	 */
	public double y() {
		return y;
	}
	
	public void setY(double y) {
		this.y = y;
		recalcMagnitude();
	}
	
	/**
	 * Get the z-coordinate of this vector.
	 */
	public double z() {
		return z;
	}
	
	public void setZ(double z) {
		this.z = z;
		recalcMagnitude();
	}
	
	/**
	 * Set this vector to the vector defined by (x, y, z).
	 * 
	 * @param x
	 *            X-coordinate to set to
	 * @param y
	 *            Y-coordinate to set to
	 * @param z
	 *            Z-coordinate to set to
	 * @return this
	 */
	public Vector set(double x, double y, double z) {
		setX(x);
		setY(y);
		setZ(z);
		return this;
	}
	
	/**
	 * Set this vector to the given vector.
	 * 
	 * @param vec
	 *            Vector to set to
	 * @return this
	 */
	public Vector set(Vector vec) {
		set(vec.x, vec.y, vec.z);
		return this;
	}
	
	/**
	 * Returns a new vector with the same coordinates as this one.
	 */
	public Vector createCopy() {
		return new Vector(this);
	}
	
	/**
	 * Add the given vector to this vector.
	 * 
	 * @param vec
	 *            The vector to add
	 * @return this
	 */
	public Vector add(Vector vec) {
		return add(vec.x, vec.y, vec.z);
	}
	
	/**
	 * Add the given vector defined by (x, y, z) to this vector.
	 * 
	 * @param x
	 *            X-coordinate to add
	 * @param y
	 *            Y-coordinate to add
	 * @param z
	 *            Z-coordinate to add
	 * @return this
	 */
	public Vector add(double x, double y, double z) {
		return set(this.x + x, this.y + y, this.z + z);
	}
	
	/**
	 * Creates a new vector from the sum of this vector and the given vector.
	 * 
	 * @param vec
	 *            Vector for sum
	 */
	public Vector plus(Vector vec) {
		return plus(vec.x, vec.y, vec.z);
	}
	
	/**
	 * Creates a new vector from the sub of this vector and the vector defined by (x, y, z).
	 * 
	 * @param x
	 *            X-coordinate of other vector
	 * @param y
	 *            Y-coordinate of other vector
	 * @param z
	 *            Z-coordinate of other vector
	 */
	public Vector plus(double x, double y, double z) {
		return new Vector(this).add(x, y, z);
	}
	
	/**
	 * Subtract the given vector from this vector.
	 * 
	 * @param vec
	 *            The reduction vector
	 * @return this
	 */
	public Vector subtract(Vector vec) {
		return subtract(vec.x, vec.y, vec.z);
	}
	
	/**
	 * Subtract the given vector defined by (x, y, z) from this vector.
	 * 
	 * @param x
	 *            X-coordinate to subtract
	 * @param y
	 *            Y-coordinate to subtract
	 * @param z
	 *            Z-coordinate to subtract
	 * @return this
	 */
	public Vector subtract(double x, double y, double z) {
		return set(this.x - x, this.y - y, this.z - z);
	}
	
	/**
	 * Creates a new vector from this vector minus the given vector.
	 * 
	 * @param vec
	 *            Other vector
	 */
	public Vector minus(Vector vec) {
		return minus(vec.x, vec.y, vec.z);
	}
	
	/**
	 * Creates a new vector from this vector minus the vector defined by (x,y,z).
	 * 
	 * @param x
	 *            X-coordinate to subtract
	 * @param y
	 *            Y-coordinate to subtract
	 * @param z
	 *            Z-coordinate to subtract
	 */
	public Vector minus(double x, double y, double z) {
		return new Vector(this).subtract(x, y, z);
	}
	
	/**
	 * Multiply this vector by the given scalar, and returns the result. Modifies the original
	 * vector.
	 * 
	 * @param scalar
	 *            The scalar to multiply this vector by
	 * @returns this
	 */
	public Vector mul(double scalar) {
		return set(x * scalar, y * scalar, z * scalar);
	}
	
	/**
	 * Creates a new vector from this vector times the scalar.
	 * 
	 * @param scalar
	 *            The scalar to multiply the new vector by
	 */
	public Vector times(double scalar) {
		return new Vector(this).mul(scalar);
	}
	
	/**
	 * Divide this vector by the given scalar, and returns the result. Modifies the original vector.
	 * 
	 * @param scalar
	 *            The scalar to divide this vector by
	 * @return this
	 */
	public Vector divide(double scalar) {
		return set(x / scalar, y / scalar, z / scalar);
	}
	
	/**
	 * Creates a new vector based on this vector divided by the other vector.
	 * 
	 * @param scalar
	 *            The scalar to divide the new vector by
	 */
	public Vector dividedBy(double scalar) {
		return new Vector(this).divide(scalar);
	}
	
	/**
	 * Get the length of this vector.
	 * <p>
	 * The result is cached since square-root is a performance-heavy operation.
	 */
	public double magnitude() {
		if (cachedMagnitude == -1) {
			cachedMagnitude = Math.sqrt(sqrMagnitude());
		}
		return cachedMagnitude;
	}
	
	/**
	 * Get the square magnitude of this vector.
	 */
	public double sqrMagnitude() {
		return x * x + y * y + z * z;
	}
	
	/**
	 * Mark cachedMagnitude so it needs to be recalculated.
	 */
	private void recalcMagnitude() {
		cachedMagnitude = -1;
	}
	
	/**
	 * Normalizes this vector so that it has a length of 1.
	 * 
	 * @return this
	 */
	public Vector normalize() {
		return divide(magnitude());
	}
	
	/**
	 * Get the square distance from the given vector.
	 * 
	 * @param vec
	 *            The other vector
	 */
	public double sqrDist(Vector vec) {
		return sqrDist(vec.x, vec.y, vec.z);
	}
	
	/**
	 * Get the square distance from the vector defined by (x, y, z).
	 * 
	 * @param x
	 *            The x-position of the other vector
	 * @param y
	 *            The y-position of the other vector
	 * @param z
	 *            The z-position of the other vector
	 */
	public double sqrDist(double x, double y, double z) {
		return (this.x - x) * (this.x - x) + (this.y - y) * (this.y - y) + (this.z - z) * (this.z - z);
	}
	
	/**
	 * Get the distance from the given vector.
	 * 
	 * @param vec
	 *            The other vector
	 */
	public double dist(Vector vec) {
		return Math.sqrt(sqrDist(vec));
	}
	
	/**
	 * Get the distance from the vector defined by (x, y, z).
	 * 
	 * @param x
	 *            The x-position of the other vector
	 * @param y
	 *            The y-position of the other vector
	 * @param z
	 *            The z-position of the other vector
	 */
	public double dist(double x, double y, double z) {
		return Math.sqrt(sqrDist(x, y, z));
	}
	
	/**
	 * Get the dot product with the given vector.
	 * 
	 * @param vec
	 *            The other vector
	 */
	public double dot(Vector vec) {
		return dot(vec.x, vec.y, vec.z);
	}
	
	/**
	 * Get the dot product with the vector defined by (x, y, z).
	 * 
	 * @param x
	 *            X-coordinate of the other vector
	 * @param y
	 *            Y-coordinate of the other vector
	 * @param z
	 *            Z-coordinate of the other vector
	 */
	public double dot(double x, double y, double z) {
		return this.x * x + this.y * y + this.z * z;
	}
	
	/**
	 * Returns the cross product of the given vector. This creates a new vector.
	 * 
	 * @param vec
	 *            The vector to cross with
	 */
	public Vector cross(Vector vec) {
		return cross(vec.x, vec.y, vec.z);
	}
	
	/**
	 * Returns the cross product with the vector defined by (x, y, z). This creates a new vector.
	 * 
	 * @param x
	 *            X-coordinate of other vector
	 * @param y
	 *            Y-coordinate of other vector
	 * @param z
	 *            Z-coordinate of other vector
	 */
	public Vector cross(double x, double y, double z) {
		return new Vector(this.y * z - this.z * y, this.z * x - this.x * z, this.x * y - this.y * x);
	}
	
	/**
	 * Returns the angle between the other vector, in radians. (result is ranged 0-PI).
	 * 
	 * @param vec
	 *            Other vector
	 */
	public double angle(Vector vec) {
		double dot = dot(vec);
		return Math.acos(dot / (this.magnitude() * vec.magnitude()));
	}
	
	/**
	 * Converts this vector into a minecraft vector.
	 */
	public Vec3d toMinecraft() {
		return new Vec3d(x, y, z);
	}
	
}
