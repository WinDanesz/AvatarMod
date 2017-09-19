package com.crowsofwar.avatar.common.entity;

import com.crowsofwar.avatar.common.AvatarDamageSource;
import com.crowsofwar.avatar.common.data.Bender;
import com.crowsofwar.avatar.common.data.BendingData;
import com.crowsofwar.avatar.common.util.AvatarUtils;
import com.crowsofwar.gorecore.util.Vector;
import com.google.common.base.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static com.crowsofwar.avatar.common.config.ConfigSkills.SKILLS_CONFIG;
import static com.crowsofwar.avatar.common.config.ConfigStats.STATS_CONFIG;

public class EntityLightningSpear extends AvatarEntity {
    private float damage;

    /**
     * Hardness threshold to chop blocks. For example, setting to 1.5 will allow
     * the airblade to chop stone.
     * <p>
     * Note: Threshold of 0 means that the airblade can chop grass and similar
     * blocks. Set to > 0 to avoid chopping blocks at all.
     */
    private float chopBlocksThreshold;
    private boolean chainAttack;
    private boolean pierceArmor;

    public EntityLightningSpear(World world) {
        super(world);
        setSize(1.5f, .2f);
        this.chopBlocksThreshold = -1;
    }

    @Override
    public void onUpdate() {

        super.onUpdate();

        setVelocity(velocity().times(0.96));
        if (!world.isRemote && velocity().sqrMagnitude() <= .9) {
            setDead();
        }
        if (!world.isRemote && inWater) {
            setDead();
        }

        if (!world.isRemote && chopBlocksThreshold >= 0) {
            breakCollidingBlocks();
        }

        if (!isDead && !world.isRemote) {
            List<EntityLivingBase> collidedList = world.getEntitiesWithinAABB(EntityLivingBase.class,
                    getEntityBoundingBox());

            if (!collidedList.isEmpty()) {

                EntityLivingBase collided = collidedList.get(0);

                DamageSource source = AvatarDamageSource.causeAirbladeDamage(collided, getOwner());
                if (pierceArmor) {
                    source.setDamageBypassesArmor();
                }
                boolean successfulHit = collided.attackEntityFrom(source, damage);

                Vector motion = velocity();
                motion = motion.times(STATS_CONFIG.airbladeSettings.push).withY(0.08);
                collided.addVelocity(motion.x(), motion.y(), motion.z());

                if (getOwner() != null) {
                    BendingData data = getOwnerBender().getData();
                    data.getAbilityData("lightning_spear").addXp(SKILLS_CONFIG.airbladeHit);
                }

                if (chainAttack) {
                    if (successfulHit) {

                        AxisAlignedBB aabb = getEntityBoundingBox().grow(10);
                        Predicate<EntityLivingBase> notFriendly =//
                                entity -> entity != collided && entity != getOwner();

                        List<EntityLivingBase> nextTargets = world.getEntitiesWithinAABB
                                (EntityLivingBase.class, aabb, notFriendly);

                        nextTargets.sort(AvatarUtils.getSortByDistanceComparator
                                (this::getDistanceToEntity));

                        if (!nextTargets.isEmpty()) {
                            EntityLivingBase nextTarget = nextTargets.get(0);
                            Vector direction = Vector.getEntityPos(nextTarget).minus(this.position());
                            setVelocity(direction.normalize().times(velocity().magnitude() *
                                    0.5));
                        }

                    }
                } else if (!world.isRemote) {
                    setDead();
                }

            }
        }

    }

    /**
     * When the airblade can break blocks, checks any blocks that the airblade
     * collides with and tries to break them
     */
    private void breakCollidingBlocks() {
        // Hitbox expansion (in each direction) to destroy blocks before the
        // airblade collides with them
        double expansion = 0.1;
        AxisAlignedBB hitbox = getEntityBoundingBox().grow(expansion, expansion, expansion);

        for (int ix = 0; ix <= 1; ix++) {
            for (int iz = 0; iz <= 1; iz++) {

                double x = ix == 0 ? hitbox.minX : hitbox.maxX;
                double y = hitbox.minY;
                double z = iz == 0 ? hitbox.minZ : hitbox.maxZ;
                BlockPos pos = new BlockPos(x, y, z);

                tryBreakBlock(world.getBlockState(pos), pos);

            }
        }
    }

    /**
     * Assuming the airblade can break blocks, tries to break the block.
     */
    private void tryBreakBlock(IBlockState state, BlockPos pos) {
        if (state.getBlock() == Blocks.AIR) {
            return;
        }

        float hardness = state.getBlockHardness(world, pos);
        if (hardness <= chopBlocksThreshold) {
            breakBlock(pos);
            setVelocity(velocity().times(0.5));
        }
    }

    @Override
    public void setDead() {
        super.setDead();
    }

    public Bender getOwnerBender() {
        return Bender.get(getOwner());
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getChopBlocksThreshold() {
        return chopBlocksThreshold;
    }

    public void setChopBlocksThreshold(float chopBlocksThreshold) {
        this.chopBlocksThreshold = chopBlocksThreshold;
    }

    public boolean getPierceArmor() {
        return pierceArmor;
    }

    public void setPierceArmor(boolean piercing) {
        this.pierceArmor = piercing;
    }

    public boolean isChainAttack() {
        return chainAttack;
    }

    public void setChainAttack(boolean chainAttack) {
        this.chainAttack = chainAttack;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        damage = nbt.getFloat("Damage");
        chopBlocksThreshold = nbt.getFloat("ChopBlocksThreshold");
        pierceArmor = nbt.getBoolean("Piercing");
        chainAttack = nbt.getBoolean("ChainAttack");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setFloat("Damage", damage);
        nbt.setFloat("ChopBlocksThreshold", chopBlocksThreshold);
        nbt.setBoolean("Piercing", pierceArmor);
        nbt.setBoolean("ChainAttack", chainAttack);
    }

}

