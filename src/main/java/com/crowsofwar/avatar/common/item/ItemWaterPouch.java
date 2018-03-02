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
package com.crowsofwar.avatar.common.item;

import com.crowsofwar.avatar.common.util.Raytrace;
import com.crowsofwar.gorecore.util.Vector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * @author CrowsOfWar
 * @author Mahtaran
 */
public class ItemWaterPouch extends Item implements AvatarItem {

	public ItemWaterPouch() {
		setCreativeTab(AvatarItems.tabItems);
		setUnlocalizedName("water_pouch");
		setMaxStackSize(1);
		setMaxDamage(0);
		setHasSubtypes(false);
	}

	@Override
	public Item item() {
		return this;
	}

	@Override
	public String getModelName(int meta) {
		return "water_pouch";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltips, ITooltipFlag advanced) {
		int meta = stack.getMetadata();
		tooltips.add(I18n.format("avatar.tooltip.water_pouch" + (meta == 0 ? ".empty" : ""), meta));
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (isInCreativeTab(tab)) {
			for (int meta = 0; meta <= 5; meta++) {
				subItems.add(new ItemStack(this, 1, meta));
			}
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack itemstack = player.getHeldItem(hand);
		boolean isFull = itemstack.getMetadata() == 5;
		if (isFull) {
			return new ActionResult(EnumActionResult.PASS, itemstack);
		}
		RayTraceResult raytraceresult = this.rayTrace(world, player, canBeFilled);
		if (raytraceresult == null || raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK) {
			return new ActionResult(EnumActionResult.PASS, itemstack);
		} else {
			BlockPos blockpos = raytraceresult.getBlockPos();
			if (!world.isBlockModifiable(player, blockpos) || !player.canPlayerEdit(blockpos.offset(raytraceresult.sideHit), raytraceresult.sideHit, itemstack)) {
				return new ActionResult(EnumActionResult.PASS, itemstack);
			}
			IBlockState state = world.getBlockState(blockpos);
			Material material = state.getMaterial();
			boolean isCauldron = state.getBlock() instanceof BlockCauldron;
			BlockCauldron cauldron = isCauldron ? (BlockCauldron) state.getBlock() : null;
			if (isCauldron || material == Material.WATER) {
				int level = state.getValue(BlockLiquid.LEVEL).intValue;
				int canBeFilled;
				if (isCauldron) {
					// Level will be 3 when completely filled, and 0 when empty
					canBeFilled = level;
				} else {
					// Level will be 0 when completely filled, and 7 when nearly empty, so we have to invert it
					canBeFilled = 8 - level;
				}
				int toBeFilled = 5 - itemstack.getItemDamage();
				int willBeFilled = Math.min(canBeFilled, toBeFilled);
				IBlockState newState;
				if (isCauldron) {
					cauldron.setWaterLevel(worldIn, pos, state, level - willBeFilled);
					player.addStat(StatList.CAULDRON_USED);
				} else {
					if (willBeFilled > 0) {
						newState = state.getBlock().getStateFromMeta(level + willBeFilled);
					} else {
						newState = Blocks.AIR.getDefaultState();
					}
					/* 11 are the flags. Flags are binary. 11 in binary:
					 *  0 1 0 1 1
					 * 16 8 4 2 1
					 * 1: cause block update
					 * 2: send change to clients
					 * 4: prevent re-render
					 * 8: run re-renders on main thread
					 * 16: prevent observers update
					 * For more information see {@link net.minecraft.world.World#setBlockState World#setBlockState}
					 */
					world.setBlockState(blockpos, newState, 11);
				}
				player.addStat(StatList.getObjectUseStats(this));
				// TODO: Custom sound?
				player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, fillPouch(itemstack, player, level))
			} else {
				new ActionResult(EnumActionResult.PASS, itemstack);
			}
		}
	}

	private ItemStack fillPouch(ItemStack emptyPouches, EntityPlayer player, int levels) {
		if (player.capabilities.isCreativeMode)	{
			return emptyPouches;
		} else {
			int newLevel = Math.max(5, emptyPouches.getItemDamage() + levels);
			final ItemStack filledPouch = new ItemStack(emptyPouches.getItem(), 1, newLevel)
			emptyPouches.shrink(1);
			if (emptyPouches.isEmpty())	{
				return new ItemStack(filledPouch);
			} else {
				if (!player.inventory.addItemStackToInventory(new ItemStack(filledPouch))) {
					player.dropItem(new ItemStack(filledPouch), false);
				} else if (player instanceof EntityPlayerMP) {
					((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
				}
				return emptyPouches;
			}
		}
	}
}
