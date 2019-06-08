package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.BuffPowerModifier;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.Chi;
import com.crowsofwar.avatar.common.data.Vision;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

public class SlipstreamPowerModifier extends BuffPowerModifier {

	@Override
	public double get(BendingContext ctx) {

		BendingData data = ctx.getData();
		AbilityData abilityData = data.getAbilityData(new AbilitySlipstream());

		double modifier = 20 + 8 * abilityData.getLevel();
		if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
			modifier = 70;
		}

		return modifier;

	}

	@Override
	public boolean onUpdate(BendingContext ctx) {

		AbilityData data = ctx.getData().getAbilityData("slipstream");
		EntityLivingBase entity = ctx.getBenderEntity();
		BendingData bD = ctx.getData();
		Chi chi = bD.chi();

		if (data.getLevel() >= 2) {

			double invisibilityChance = 0.3;
			int invisiblityDuration = 30;

			if (data.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				invisibilityChance = 0.4;
				invisiblityDuration = 40;
			}

			// Intermittently grant invisibility
			if (ctx.getBenderEntity().ticksExisted % 20 == 0) {
				// 40% chance per second for invisibility
				if (Math.random() < invisibilityChance) {
					PotionEffect effect = new PotionEffect(MobEffects.INVISIBILITY, invisiblityDuration, 0, false, false);
					ctx.getBenderEntity().addPotionEffect(effect);
				}
			}

		}
		if (data.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
			if (chi.getTotalChi() > 0 && chi.getAvailableChi() > 0) {
				if (entity.world.getBlockState(entity.getPosition()).getBlock() == Blocks.AIR) {
					entity.setNoGravity(true);
				}
				else {
					entity.setNoGravity(false);
				}
				if (entity.ticksExisted % 5 == 0) {
					chi.setAvailableChi(chi.getAvailableChi() - 1);
				}
			}
		}




		return super.onUpdate(ctx);
	}

	@Override
	protected Vision[] getVisions() {
			return new Vision[]{Vision.SLIPSTREAM_WEAK, Vision.SLIPSTREAM_MEDIUM,
					Vision.SLIPSTREAM_POWERFUL};
	}

	@Override
	protected String getAbilityName() {
		return "slipstream";
	}

}

