package vinyarion.fukkit.main.cmd;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class FCommandFill extends FCommand {
	
	public FCommandFill(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <x1> <y1> <z1> <x2> <y2> <z2> <block> [meta]";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		if(args.length < 7) {
			throw new CommandException(this.getCommandUsage(sender), new Object[0]);
		}
		int px = sender.getPlayerCoordinates().posX;
		int py = sender.getPlayerCoordinates().posY;
		int pz = sender.getPlayerCoordinates().posZ;
		
		int x1 = MathHelper.floor_double(CommandBase.func_110666_a(sender, px, args[0]));
		int y1 = MathHelper.floor_double(CommandBase.func_110666_a(sender, py, args[1]));
		int z1 = MathHelper.floor_double(CommandBase.func_110666_a(sender, pz, args[2]));
		int x2 = MathHelper.floor_double(CommandBase.func_110666_a(sender, px, args[3]));
		int y2 = MathHelper.floor_double(CommandBase.func_110666_a(sender, py, args[4]));
		int z2 = MathHelper.floor_double(CommandBase.func_110666_a(sender, pz, args[5]));
		
		int xmin = Math.min(x1, x2);
		int xmax = Math.max(x1, x2);
		int ymin = Math.min(y1, y2);
		int ymax = Math.max(y1, y2);
		int zmin = Math.min(z1, z2);
		int zmax = Math.max(z1, z2);
		
		Block block = CommandBase.getBlockByText(sender, args[6]);
		int meta = args.length > 7 ? Integer.valueOf(args[7]) : 0;
		World world = sender.getEntityWorld();
		int total = (xmax - xmin) * (ymax - ymin) * (zmax - zmin);
		int sofar = 0;
		
		for(int x = xmin; x <= xmax; x++) {
			for(int y = ymin; y <= ymax; y++) {
				for(int z = zmin; z <= zmax; z++) {
					if(world.blockExists(x, y, z) && sofar <= 32768) {
						if(world.setBlock(x, y, z, block, meta, 3)) {
							sofar++;
						}
					}
				}
			}
		}
		
		Info(sender, (sofar > 32768 ? "Wanted " + total + " blocks, f" : "F") + "illed " + (sofar) + " blocks.");
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		if(args.length == 1 || args.length == 4) {
			return new String[]{String.valueOf(FCommand.getLookPos(sender)[0])};
		} else if(args.length == 2 || args.length == 5) {
			return new String[]{String.valueOf(FCommand.getLookPos(sender)[1])};
		} else if(args.length == 3 || args.length == 6) {
			return new String[]{String.valueOf(FCommand.getLookPos(sender)[2])};
		} else if(args.length == 7) {
			return Block.blockRegistry.getKeys();
		}
		return null;
	}
	
}
