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
package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.data.AbilityData;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

/**
 * An AvatarEntity that acts as a shield for further attacks. It has a certain amount of health
 * and absorbs damage until the health is removed. The shield remains attached to the player and
 * follows them wherever they go.
 *
 * @author CrowsOfWar
 */
public abstract class EntityShield extends AvatarEntity {

	public static final DataParameter<Float> SYNC_HEALTH = EntityDataManager.createKey(EntityShield.class,
			DataSerializers.FLOAT);
	public static final DataParameter<Float> SYNC_MAX_HEALTH = EntityDataManager
			.createKey(EntityShield.class, DataSerializers.FLOAT);

	/**
	 * Shields do not protect against some types of damage, such as falling.
	 */
	public static final List<String> UNPROTECTED_DAMAGE = Arrays.asList("fall", "magic", "poison",
			"wither", "indirectMagic");

	public EntityShield(World world) {
		super(world);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(SYNC_HEALTH, 20f);
		dataManager.register(SYNC_MAX_HEALTH, 20f);
	}

	@Override
	public EntityLivingBase getController() {
		return getOwner();
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		EntityLivingBase owner = getOwner();
		if (owner == null) {
			setDead();
			return;
		}

		if (owner.isBurning()) {
			owner.extinguish();
		}

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setHealth(nbt.getFloat("Health"));
		setMaxHealth(nbt.getFloat("MaxHealth"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setFloat("Health", getHealth());
		nbt.setFloat("MaxHealth", getMaxHealth());
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {

		EntityLivingBase owner = getOwner();
		if (owner != null) {

			if (!world.isRemote) {

				if (!UNPROTECTED_DAMAGE.contains(source.getDamageType())) {
					if (!owner.isEntityInvulnerable(source)) {

						Bender bender = Bender.get(owner);
						BendingData data = bender.getData();
						if (bender.consumeChi(getChiDamageCost() * amount)) {

							AbilityData aData = data.getAbilityData(getAbilityName());
							aData.addXp(getProtectionXp());
							setHealth(getHealth() - amount);
							return true;

						} else {
							return true;
						}

					}
				}
			}

		} else {
			return true;
		}
		return false;

	}

	/**
	 * Returns the amount of chi to take per unit of damage taken (per half heart).
	 */
	protected abstract float getChiDamageCost();

	/**
	 * Returns the amount of XP to add when an attack was defended.
	 */
	protected abstract float getProtectionXp();

	/**
	 * Gets the name of the corresponding ability
	 */
	protected abstract String getAbilityName();

	/**
	 * Called when the health reaches zero.
	 */
	protected abstract void onDeath();

	@Override
	public boolean isShield() {
		return true;
	}

	public float getHealth() {
		return dataManager.get(SYNC_HEALTH);
	}

	public void setHealth(float health) {
		dataManager.set(SYNC_HEALTH, health);
		if (health <= 0) onDeath();
		if (health > getMaxHealth()) health = getMaxHealth();
	}

	public float getMaxHealth() {
		return dataManager.get(SYNC_MAX_HEALTH);
	}

	public void setMaxHealth(float health) {
		dataManager.set(SYNC_MAX_HEALTH, health);
	}

}
