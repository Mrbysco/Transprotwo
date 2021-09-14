package com.mrbysco.transprotwo.client.particles;

import com.mrbysco.transprotwo.client.particles.factory.ParticleRenderTypes;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;

import java.util.Random;

public class SquareParticle extends SpriteTexturedParticle {

	protected SquareParticle(ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, float r, float g, float b, IAnimatedSprite sprite) {
		super(world, x, y, z, 0, 0, 0);
		float colorR = r;
		float colorG = g;
		float colorB = b;
		if (colorR > 1.0) {
			colorR = (float)(colorR / 255.0);
		}
		if (colorG > 1.0) {
			colorG = (float)(colorG / 255.0);
		}
		if (colorB > 1.0) {
			colorB = (float)(colorB / 255.0);
		}

		this.setColor(colorR, colorG, colorB);
		this.maxAge = new Random().nextInt(10) + 5;
		this.motionX = xSpeed;
		this.motionY = ySpeed;
		this.motionZ = zSpeed;
		this.particleScale = 0.12F;
		this.selectSpriteRandomly(sprite);
	}

	@Override
	public void tick() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		this.motionY -= 0.04D * this.particleGravity;
		if (this.onGround) {
			this.motionX *= 0.699999988079071D;
			this.motionZ *= 0.699999988079071D;
		}
		this.move(this.motionX, this.motionY, this.motionZ);

		if (this.age++ >= this.maxAge) {
			this.setExpired();
		}
	}

	@Override
	public IParticleRenderType getRenderType() {
		return ParticleRenderTypes.SQUARE_RENDER;
	}
}
