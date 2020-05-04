package vinyarion.fukkit.main.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import vinyarion.fukkit.main.FNetHandlerPlayServer;
import vinyarion.fukkit.main.chestguis.InventoryChestHijacked;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.data.FServer;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.rpg.FPlayerDataRPG;
import vinyarion.fukkit.rpg.tileentity.FTileEntityData;

public class FCommandRPGManageBlock extends FCommand {
	
	public FCommandRPGManageBlock(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + this.getCommandName() + " [<annex>]";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(sender instanceof EntityPlayerMP, "Must be a player!");
		EntityPlayerMP target = (EntityPlayerMP)sender;
		MovingObjectPosition look = FNetHandlerPlayServer.of(target).data().objectMouseOver;
		if(look.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			TileEntity te = target.worldObj.getTileEntity(look.blockX, look.blockY, look.blockZ);
			if(te != null) {
				FTileEntityData ted = FTileEntityData.of(te);
				if(target.getGameProfile().getId().equals(ted.owner)) {
					FPlayerDataRPG.of(target).curentTileEntity = te;
					FPlayerDataRPG.of(target).curentTileEntityData = ted;
					FPermissions.isOverride.set(Boolean.TRUE);
					MinecraftServer.getServer().getCommandManager().executeCommand(target, "f_gui display system/rpg/teowner");
					FPermissions.isOverride.remove();
				} else if(args.length > 0 && args[0].equalsIgnoreCase("annex")) {
					if(FPermissions.instance().hasPermission(target, FPermissions.Others.perm_AnnexBlocks)) {
						ted.owner = target.getGameProfile().getId();
						println(target, "Annexed "+te.getClass().getName()+" @ "+te.xCoord+","+te.yCoord+","+te.zCoord);
					} else Error("You do not have permission to annex blocks.");
				} else Error("You do not own this tile entity.");
			} else Error("This block does not have a tile entity.");
		} else Error("Look at a tile entity you own.");
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return "annex";
	}
	
}
