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

import com.crowsofwar.avatar.common.data.AvatarWorldData;
import com.crowsofwar.gorecore.util.BackedVector;
import com.crowsofwar.gorecore.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

/**
 * 
 * 
 * @author CrowsOfWar
 */
public abstract class AvatarEntity extends Entity {
	
	private final Vector internalVelocity;
	private final Vector internalPosition;
	private static final DataParameter<Integer> SYNC_ID = EntityDataManager.createKey(EntityWaterBubble.class,
			DataSerializers.VARINT);
	
	/**
	 * @param world
	 */
	public AvatarEntity(World world) {
		super(world);
		this.internalVelocity = createInternalVelocity();
		this.internalPosition = new BackedVector(x -> this.posX = x, y -> this.posY = y, z -> this.posZ = z,
				() -> posX, () -> posY, () -> posZ);
	}
	
	@Override
	protected void entityInit() {
		dataManager.register(SYNC_ID,
				worldObj.isRemote ? -1 : AvatarWorldData.getDataFromWorld(worldObj).nextEntityId());
	}
	
	/**
	 * Get the velocity of this entity in m/s. Changes to this vector will be
	 * reflected in the entity's actual velocity.
	 */
	public Vector velocity() {
		return internalVelocity;
	}
	
	/**
	 * Get the position of this entity. Changes to this vector will be reflected
	 * in the entity's actual position.
	 */
	public Vector position() {
		return internalPosition;
	}
	
	public int getAvId() {
		return dataManager.get(SYNC_ID);
	}
	
	private void setAvId(int id) {
		dataManager.set(SYNC_ID, id);
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		setAvId(nbt.getInteger("AvId"));
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setInteger("AvId", getAvId());
	}
	
	//@formatter:off
	protected Vector createInternalVelocity() {
		return new BackedVector(
				x -> this.motionX = x / 20,
				y -> this.motionY = y / 20,
				z -> this.motionZ = z / 20,
				() -> this.motionX * 20,
				() -> this.motionY * 20,
				() -> this.motionZ * 20);
	}
	
}
