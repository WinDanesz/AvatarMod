package com.crowsofwar.avatar.common.bending.lightning;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import com.crowsofwar.avatar.common.entity.EntityLightningSpawner;
import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;
import static com.crowsofwar.gorecore.util.Vector.getEyePos;
import static com.crowsofwar.gorecore.util.Vector.getLookRectangular;


public class AbilityLightningRaze extends Ability {
	public AbilityLightningRaze() {
		super(Lightningbending.ID, "lightning_raze");
	}



	@Override
	public void execute(AbilityContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		World world = ctx.getWorld();
		Bender bender = ctx.getBender();

		int ticks = 20;
		//How long the spawner stays alive.
		double speed = STATS_CONFIG.lightningRazeSettings.speed;
		//How quickly the cloud of lightning for Level 4 Path Two moves.
		//Default 5
		float chi = STATS_CONFIG.chiLightningRaze;
		//Default 5
		float frequency = 5;
		//How many ticks pass before each lightning bolt strikes.
		int bolts = 1;
		//The number of  bolts the lightning spawner spawns per ticks
		//Ticks determined by frequency
		float accuracy = 1;
		/*0 accuracy is the most accurate; each number represents how far away from the spawn position
		it will be.**/




		if (ctx.getLevel() >= 1) {
			ticks = 40;
			frequency = 4;
			chi = 6;
			speed = 7;
			bolts = 2;

		}
		if (ctx.getLevel() >= 2) {
			speed = 9;
			frequency = 3;
			chi = 7;
			bolts = 3;
			accuracy = 0.75F;
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
			frequency = 2;
			bolts = 1;
			ticks = 40;
			speed = 20;
			chi = 7;
			accuracy = 0;
			//Super-fast line of lightning that lights up the ground
			//Zeus' Wrath
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
			frequency = 4;
			ticks = 60;
			speed = 3;
			chi = 8;
			bolts = 5;
			accuracy = 2;
			//Thor's wrath
			//Cloud of lightning that follows your cursor
		}

		if (bender.consumeChi(chi)) {
			Raytrace.Result hit = Raytrace.getTargetBlock(entity, 6);

			if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
				hit = Raytrace.getTargetBlock(entity, 8);
			}
			if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
				hit = Raytrace.getTargetBlock(entity, 20);
			}

			Vector look = Vector.toRectangular(Math.toRadians(entity.rotationYaw), 0);
			Vector playerPos = getEyePos(entity);
			Vector lookPos = playerPos.plus(getLookRectangular(entity).times(1.3));
			Vector hitAt = hit.getPosPrecise();

			EntityLightningSpawner boltSpawner = new EntityLightningSpawner(world);
			boltSpawner.setOwner(entity);
			if (hit.hitSomething()) {
				boltSpawner.setPosition(hitAt.x(), hitAt.y(), hitAt.z());
			} else {
				boltSpawner.setPosition(lookPos.withY(entity.posY));
			}
			boltSpawner.setVelocity(look.times(speed));
			boltSpawner.setSpeed(speed);
			//This is so that the player can control the entity; otherwise unnecessary.
			boltSpawner.setAbility(this);
			boltSpawner.setDuration(ticks);
			boltSpawner.setLightningFrequency(frequency);
			boltSpawner.setPlayerControl(ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND));
			boltSpawner.setAmountofBolts(bolts);
			boltSpawner.setAccuracy(accuracy);
			world.spawnEntity(boltSpawner);


		}

	}
}

