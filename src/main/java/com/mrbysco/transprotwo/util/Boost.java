package com.mrbysco.transprotwo.util;

public class Boost {
	public static final long defaultFrequence = 35L;
	public static final double defaultSpeed = .03;
	public static final int defaultStackSize = 1;

	public final long frequence;
	public final double speed;
	public final int stackSize;

	public Boost(long frequence, double speed, int stackSize) {
		this.frequence = frequence;
		this.speed = speed;
		this.stackSize = stackSize;
	}
}
