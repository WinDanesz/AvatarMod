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
package com.crowsofwar.avatar.common.network.packets;

import net.minecraftforge.fml.common.network.simpleimpl.*;

import com.crowsofwar.avatar.common.TransferConfirmHandler;
import io.netty.buffer.ByteBuf;

/**
 * @author CrowsOfWar
 */
public class PacketSConfirmTransfer extends AvatarPacket<PacketSConfirmTransfer> {

	@Override
	public void avatarFromBytes(ByteBuf buf) {
	}

	@Override
	public void avatarToBytes(ByteBuf buf) {
	}

	public static class Handler extends AvatarPacketHandler<PacketSConfirmTransfer, IMessage> {
		/**
		 * This method will always be called on the main thread. In the case that that's not wanted, create your own {@link IMessageHandler}
		 *
		 * @param message The packet that is received
		 * @param ctx     The context to that packet
		 * @return An optional packet to reply with, or null
		 */
		@Override
		IMessage avatarOnMessage(PacketSConfirmTransfer message, MessageContext ctx) {
			TransferConfirmHandler.confirmTransfer(ctx.getServerHandler().player);
			return null;
		}
	}
}
