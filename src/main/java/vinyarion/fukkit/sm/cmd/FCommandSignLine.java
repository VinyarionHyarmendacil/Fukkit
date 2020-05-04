package vinyarion.fukkit.sm.cmd;

import lotr.common.tileentity.LOTRTileEntitySign;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.util.Colors;

public class FCommandSignLine extends FCommand {
	
	public FCommandSignLine(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <clear|remove|set> [<line> [<text>]]";
	}

	public void processCommand(ICommandSender sender, String[] args) {
		Assert(sender instanceof EntityPlayerMP, "Must be a player!");
		EntityPlayerMP player = (EntityPlayerMP)sender;
		int[] looking = this.getLookPos(player);
		TileEntity te = player.worldObj.getTileEntity(looking[0], looking[1], looking[2]);
		Assert(args.length >= 1, "Not enough args!");
		String op = args[0];
		Assert(te != null, "Look at a sign or some chisled text!");
		if(te instanceof TileEntitySign) {
			TileEntitySign sign = (TileEntitySign)te;
			if(op.equals("clear")) {
				for(int i = 0; i < sign.signText.length; i++) {
					sign.signText[i] = "";
				}
			} else if(op.equals("remove")) {
				Assert(args.length >= 2, "Not enough args!");
				String op2 = args[1];
				int i = parseIntBounded(player, op2, 0, sign.signText.length - 1);
				sign.signText[i] = "";
			} else if(op.equals("set")) {
				Assert(args.length >= 3, "Not enough args!");
				String op2 = args[1];
				String op3 = Colors.color(rest(args, 2, 15));
				int i = parseIntBounded(player, op2, 0, sign.signText.length - 1);
				sign.signText[i] = op3;
			} else Invalid(op);
			sign.markDirty();
			MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayersInDimension(sign.getDescriptionPacket(), player.dimension);
		} else if(te instanceof LOTRTileEntitySign) {
			LOTRTileEntitySign sign = (LOTRTileEntitySign)te;
			if(op.equals("clear")) {
				for(int i = 0; i < sign.signText.length; i++) {
					sign.signText[i] = "";
				}
			} else if(op.equals("remove")) {
				Assert(args.length >= 2, "Not enough args!");
				String op2 = args[1];
				int i = parseIntBounded(player, op2, 0, sign.signText.length - 1);
				sign.signText[i] = "";
			} else if(op.equals("set")) {
				Assert(args.length >= 3, "Not enough args!");
				String op2 = args[1];
				String op3 = Colors.color(rest(args, 2));
				int i = parseIntBounded(player, op2, 0, sign.signText.length - 1);
				sign.signText[i] = op3;
			} else Invalid(op);
			sign.markDirty();
			MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayersInDimension(sign.getDescriptionPacket(), player.dimension);
		} else Error("Look at a sign or some chisled text!");
		
	}

	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? new String[]{"clear", "remove", "set"} : null;
	}

}
