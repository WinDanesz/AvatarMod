package com.crowsofwar.avatar.common.bending.air;

import com.crowsofwar.avatar.common.bending.Ability;
import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.data.ctx.AbilityContext;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class AbilitySlipstream extends Ability {

	public AbilitySlipstream() {
		super(Airbending.ID, "slipstream");
	}

	@Override
	public boolean isBuff() {
		return true;
	}

	@Override
	public void execute(AbilityContext ctx) {

		BendingData data = ctx.getData();
		AbilityData abilityData = data.getAbilityData(this);
		EntityLivingBase entity = ctx.getBenderEntity();
		Bender bender = ctx.getBender();

		float chi = STATS_CONFIG.chiBuff;
		if (abilityData.getLevel() == 1) {
			chi = STATS_CONFIG.chiBuffLvl2;
		} else if (abilityData.getLevel() == 2) {
			chi = STATS_CONFIG.chiBuffLvl3;
		} else if (abilityData.getLevel() == 3) {
			chi = STATS_CONFIG.chiBuffLvl4;
		}

		if (bender.consumeChi(chi)) {
			float xp = SKILLS_CONFIG.buffUsed;

			// 4s base + 1s per level
			int duration = 80 + 20 * abilityData.getLevel();

			int effectLevel = abilityData.getLevel() >= 2 ? 1 : 0;
			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.SECOND)) {
				effectLevel = 2;
			}

			entity.addPotionEffect(new PotionEffect(MobEffects.SPEED, duration, effectLevel));
			data.getAbilityData("slipstream").addXp(xp);

			if (abilityData.getLevel() >= 1) {
				entity.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, duration, effectLevel));
			}

			if (abilityData.isMasterPath(AbilityData.AbilityTreePath.FIRST)) {
				entity.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, duration));
			}

			SlipstreamPowerModifier modifier = new SlipstreamPowerModifier();
			modifier.setTicks(duration);
			data.getPowerRatingManager(getBendingId()).addModifier(modifier, ctx);

		}

	}

	@Override
	public int getCooldown(AbilityContext ctx) {
		EntityLivingBase entity = ctx.getBenderEntity();
		int coolDown = 160;

		if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) {
			coolDown = 0;
		}

		if (ctx.getLevel() == 1) {
			coolDown = 140;
		}
		if (ctx.getLevel() == 2) {
			coolDown = 120;
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.FIRST)) {
			coolDown = 100;
		}
		if (ctx.isMasterLevel(AbilityData.AbilityTreePath.SECOND)) {
			coolDown = 100;
		}
		return coolDown;
	}
}



