package com.mrbysco.transprotwo.util;

import net.minecraft.core.Vec3i;

public class DistanceHelper {
	public static double getDistance(Vec3i from, Vec3i to) {
		double d0 = (double) (from.getX() - to.getX());
		double d1 = (double) (from.getY() - to.getY());
		double d2 = (double) (from.getZ() - to.getZ());
		return Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
	}
}
