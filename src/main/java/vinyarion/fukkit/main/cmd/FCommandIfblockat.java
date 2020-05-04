package vinyarion.fukkit.main.cmd;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import vinyarion.fukkit.main.data.FPermissions;

public class FCommandIfblockat extends FCommand {
	
	public FCommandIfblockat(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <x> <y> <z> <block> <meta|'any'> <command>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		if(args.length < 6) {
			throw new CommandException(this.getCommandUsage(sender), new Object[0]);
		}
		int x = MathHelper.floor_double(CommandBase.func_110666_a(sender, sender.getPlayerCoordinates().posX, args[0]));
		int y = MathHelper.floor_double(CommandBase.func_110666_a(sender, sender.getPlayerCoordinates().posY, args[1]));
		int z = MathHelper.floor_double(CommandBase.func_110666_a(sender, sender.getPlayerCoordinates().posZ, args[2]));
		Block block = CommandBase.getBlockByText(sender, args[3]);
		int meta = MathHelper.parseIntWithDefault(args[4], 0);
		World world = sender.getEntityWorld();
		String cmd = "/" + args[5];
		for(int i = 6; i < args.length; i++) {
			cmd += " " + args[i];
		}
		if(block == world.getBlock(x, y, z) && (args[4].equals("any") || world.getBlockMetadata(x, y, z) == meta)) {
			Info(sender, "Comand is:" + cmd);
			MinecraftServer.getServer().getCommandManager().executeCommand(sender, cmd);
		}
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		if(args.length == 1) {
			return new String[]{String.valueOf(FCommand.getLookPos(sender)[0])};
		} else if(args.length == 2) {
			return new String[]{String.valueOf(FCommand.getLookPos(sender)[1])};
		} else if(args.length == 3) {
			return new String[]{String.valueOf(FCommand.getLookPos(sender)[2])};
		} else if(args.length == 4) {
			return CommandBase.getListOfStringsFromIterableMatchingLastWord(args, Block.blockRegistry.getKeys());
		} else if(args.length == 5) {
			return new String[]{"any", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" ,"11" ,"12", "13", "14", "15"};
		}
		return null;
	}
	
}
