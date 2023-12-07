package com.mrbysco.transprotwo.util;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * Modified version of ParticleColor: https://github.com/baileyholl/Ars-Nouveau/blob/1.16.2/src/main/java/com/hollingsworth/arsnouveau/client/particle/ParticleColor.java
 */
public class Color {
	private final float r;
	private final float g;
	private final float b;
	private final int color;

	public Color(int r, int g, int b) {
		this.r = r / 255F;
		this.g = g / 255F;
		this.b = b / 255F;
		this.color = (r << 16) | (g << 8) | b;
	}

	public static Color makeRandomColor(int r, int g, int b, Random random) {
		return new Color(random.nextInt(r), random.nextInt(g), random.nextInt(b));
	}

	public static Color getHSBColor(float h, float s, float b) {
		return fromInt(HSBtoRGB(h, s, b));
	}

	public static int HSBtoRGB(float hue, float saturation, float brightness) {
		int r = 0, g = 0, b = 0;
		if (saturation == 0) {
			r = g = b = (int) (brightness * 255.0f + 0.5f);
		} else {
			float h = (hue - (float) Math.floor(hue)) * 6.0f;
			float f = h - (float) java.lang.Math.floor(h);
			float p = brightness * (1.0f - saturation);
			float q = brightness * (1.0f - saturation * f);
			float t = brightness * (1.0f - (saturation * (1.0f - f)));
			switch ((int) h) {
				case 0:
					r = (int) (brightness * 255.0f + 0.5f);
					g = (int) (t * 255.0f + 0.5f);
					b = (int) (p * 255.0f + 0.5f);
					break;
				case 1:
					r = (int) (q * 255.0f + 0.5f);
					g = (int) (brightness * 255.0f + 0.5f);
					b = (int) (p * 255.0f + 0.5f);
					break;
				case 2:
					r = (int) (p * 255.0f + 0.5f);
					g = (int) (brightness * 255.0f + 0.5f);
					b = (int) (t * 255.0f + 0.5f);
					break;
				case 3:
					r = (int) (p * 255.0f + 0.5f);
					g = (int) (q * 255.0f + 0.5f);
					b = (int) (brightness * 255.0f + 0.5f);
					break;
				case 4:
					r = (int) (t * 255.0f + 0.5f);
					g = (int) (p * 255.0f + 0.5f);
					b = (int) (brightness * 255.0f + 0.5f);
					break;
				case 5:
					r = (int) (brightness * 255.0f + 0.5f);
					g = (int) (p * 255.0f + 0.5f);
					b = (int) (q * 255.0f + 0.5f);
					break;
			}
		}
		return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
	}

	public Color(float r, float g, float b) {
		this((int) r, (int) g, (int) b);
	}

	public Color(double r, double g, double b) {
		this((int) r, (int) g, (int) b);
	}

	public static Color fromInt(int color) {
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color) & 0xFF;
		return new Color(r, g, b);
	}

	public float getRed() {
		return r;
	}

	public float getGreen() {
		return g;
	}

	public float getBlue() {
		return b;
	}

	public int getAlpha() {
		return (getColor() >> 24) & 0xff;
	}

	public int getColor() {
		return color;
	}

	public String serialize() {
		return "" + this.r + "," + this.g + "," + this.b;
	}

	public IntWrapper toWrapper() {
		return new IntWrapper(this);
	}

	public static Color deserialize(String string) {
		String[] arr = string.split(",");
		return new Color(Integer.parseInt(arr[0].trim()), Integer.parseInt(arr[1].trim()), Integer.parseInt(arr[2].trim()));
	}

	public static class IntWrapper {

		public int r;
		public int g;
		public int b;

		public IntWrapper(int r, int g, int b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}

		public IntWrapper(Color color) {
			this.r = (int) (color.getRed() * 255.0);
			this.g = (int) (color.getGreen() * 255.0);
			this.b = (int) (color.getBlue() * 255.0);
		}

		public Color toParticleColor() {
			return new Color(r, g, b);
		}

		public String serialize() {
			return "" + this.r + "," + this.g + "," + this.b;
		}

		public void makeVisible() {
			if (r + g + b < 20) {
				b += 10;
				g += 10;
				r += 10;
			}
		}

		public static @NotNull
		Color.IntWrapper deserialize(String string) {
			Color.IntWrapper color = new Color.IntWrapper(255, 25, 180);
			try {
				String[] arr = string.split(",");
				color = new Color.IntWrapper(Integer.parseInt(arr[0].trim()), Integer.parseInt(arr[1].trim()), Integer.parseInt(arr[2].trim()));
				return color;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return color;
		}
	}
}
