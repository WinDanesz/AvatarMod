package com.crowsofwar.avatar.common.bending.earth;

import com.crowsofwar.avatar.common.bending.StatusControl;
import com.crowsofwar.avatar.common.controls.AvatarControl;
import com.crowsofwar.avatar.common.data.ctx.BendingContext;
import com.crowsofwar.avatar.common.entity.AvatarEntity;
import com.crowsofwar.avatar.common.entity.EntityWallSegment;
import com.crowsofwar.avatar.common.entity.data.WallBehavior;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author CrowsOfWar
 * @author Aang23
 */
public class StatCtrlPlaceWall extends StatusControl {

	public StatCtrlPlaceWall() {
		super(2, AvatarControl.CONTROL_RIGHT_CLICK, CrosshairPosition.ABOVE_CROSSHAIR);
	}

	@Override
	public boolean execute(BendingContext ctx) {

		World world = ctx.getWorld();
		EntityLivingBase entity = ctx.getBenderEntity();

		// TODO: When upgrade to a5.0 , call setOwner on the wall itself , then lookup
		// based on wall

		// Wall has no owner so we go for segments
		EntityWallSegment wallSegment = AvatarEntity.lookupOwnedEntity(world, EntityWallSegment.class, entity);

		List<EntityWallSegment> segments = world.getEntities(EntityWallSegment.class, seg -> seg.getOwner() == entity);

		for (EntityWallSegment seg : segments) {
			if (seg.getBehavior().getClass().equals(WallBehavior.Waiting.class))
				seg.setBehavior(new WallBehavior.Place());
		}

		return true;
	}

}
