package com.mrbysco.transprotwo.util;

public record Boost(long frequence, double speed, int stackSize) {
	public static final long defaultFrequence = 35L;
	public static final double defaultSpeed = .03;
	public static final int defaultStackSize = 1;

}
