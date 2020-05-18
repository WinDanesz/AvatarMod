/* 
  This file is part of AvatarMod.
    
  AvatarMod is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  AvatarMod is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with AvatarMod. If not, see <http://www.gnu.org/licenses/>.
*/
package com.crowsofwar.avatar.client.render;

import com.crowsofwar.avatar.common.entity.EntityFireball;
import com.crowsofwar.avatar.common.particle.ParticleBuilder;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.Random;

import static com.crowsofwar.avatar.client.render.RenderUtils.drawQuad;
import static net.minecraft.client.renderer.GlStateManager.*;
import static net.minecraft.util.math.MathHelper.cos;

/**
 * @author CrowsOfWar
 */
public class RenderFireball extends Render<EntityFireball> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("avatarmod",
			"textures/entity/fireball.png");
	private static final Random random = new Random();

	public RenderFireball(RenderManager renderManager) {
		super(renderManager);
	}

	// @formatter:off
	@Override
	public void doRender(EntityFireball entity, double xx, double yy, double zz, float entityYaw,
						 float partialTicks) {


		float x = (float) xx, y = (float) yy, z = (float) zz;

		Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);

		float ticks = entity.ticksExisted + partialTicks;

		float rotation = ticks / 3f;
		float size = .8f + cos(ticks / 5f) * .05f;
		size *= Math.sqrt(entity.getSize() / 30f);

		enableBlend();


		//   if (MinecraftForgeClient.getRenderPass() == 0) {
		disableLighting();

		//Where did crows get this number from?
		//Maybe try using 3932220 instead?
		int i = 3932220;
		//128 * 128 instead of 64^2
		int j = i % 16384;
		int k = i / 16384;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j, k);

		renderCube(x, y, z, //
				0, 8 / 256.0, 0, 8 / 256.0, //
				.5f, //
				ticks / 25F, ticks / 25f, ticks / 25F);

		//  } else {

		pushMatrix();
		renderCube(x, y, z, //
				8 / 256.0, 16 / 256.0, 0 / 256.0, 8 / 256.0, //
				size, //
				rotation * .2f, rotation, rotation * -.4f);
		popMatrix();

		//  }
		World world = entity.world;
		if (world.isRemote && entity.getOwner() != null) {
			for (double h = 0; h < entity.width; h += 0.3) {
				Random random = new Random();
				AxisAlignedBB boundingBox = entity.getEntityBoundingBox();
				double spawnX = boundingBox.minX + random.nextDouble() * (boundingBox.maxX - boundingBox.minX);
				double spawnY = boundingBox.minY + random.nextDouble() * (boundingBox.maxY - boundingBox.minY);
				double spawnZ = boundingBox.minZ + random.nextDouble() * (boundingBox.maxZ - boundingBox.minZ);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
						world.rand.nextGaussian() / 60).time(12).clr(255, 10, 5)
						.scale(entity.getSize() * 0.03125F).element(entity.getElement()).spawnEntity(entity.getOwner())
						.spawn(world);
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spawnX, spawnY, spawnZ).vel(world.rand.nextGaussian() / 60, world.rand.nextGaussian() / 60,
						world.rand.nextGaussian() / 60).time(12).clr(235 + AvatarUtils.getRandomNumberInRange(0, 20),
						20 + AvatarUtils.getRandomNumberInRange(0, 60), 10)
						.scale(entity.getSize() * 0.03125F).element(entity.getElement()).spawnEntity(entity.getOwner())
						.spawn(world);
			}

		}
		enableLighting();
		disableBlend();

	}
	// @formatter:on

	private void renderCube(float x, float y, float z, double u1, double u2, double v1, double v2, float size,
							float rotateX, float rotateY, float rotateZ) {
		Matrix4f mat = new Matrix4f();
		mat.translate(x, y + .4f, z);

		mat.rotate(rotateX, 1, 0, 0);
		mat.rotate(rotateY, 0, 1, 0);
		mat.rotate(rotateZ, 0, 0, 1);

		// @formatter:off
		// Can't use .mul(size) here because it would mul the w component
		Vector4f
				lbf = new Vector4f(-.5f * size, -.5f * size, -.5f * size, 1).mul(mat),
				rbf = new Vector4f(0.5f * size, -.5f * size, -.5f * size, 1).mul(mat),
				ltf = new Vector4f(-.5f * size, 0.5f * size, -.5f * size, 1).mul(mat),
				rtf = new Vector4f(0.5f * size, 0.5f * size, -.5f * size, 1).mul(mat),
				lbb = new Vector4f(-.5f * size, -.5f * size, 0.5f * size, 1).mul(mat),
				rbb = new Vector4f(0.5f * size, -.5f * size, 0.5f * size, 1).mul(mat),
				ltb = new Vector4f(-.5f * size, 0.5f * size, 0.5f * size, 1).mul(mat),
				rtb = new Vector4f(0.5f * size, 0.5f * size, 0.5f * size, 1).mul(mat);

		// @formatter:on

		drawQuad(2, ltb, lbb, lbf, ltf, u1, v1, u2, v2); // -x
		drawQuad(2, rtb, rbb, rbf, rtf, u1, v1, u2, v2); // +x
		drawQuad(2, rbb, rbf, lbf, lbb, u1, v1, u2, v2); // -y
		drawQuad(2, rtb, rtf, ltf, ltb, u1, v1, u2, v2); // +y
		drawQuad(2, rtf, rbf, lbf, ltf, u1, v1, u2, v2); // -z
		drawQuad(2, rtb, rbb, lbb, ltb, u1, v1, u2, v2); // +z
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFireball entity) {
		return TEXTURE;
	}

}
