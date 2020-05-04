package vinyarion.fukkit.main.cmd;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import vinyarion.fukkit.main.data.FRecipes;
import vinyarion.fukkit.main.data.FRegions;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FCommandRegion extends FCommand {
	
	public FCommandRegion(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <add|remove|display> <regionName> [<x1> <y1> <z1> <x2> <y2> <z2> <dimension> <perms>]";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 2, "Not enough arguments!");
		String op = args[0];
		String op2 = args[1];
		if(op.equals("add")) {
			Assert(args.length >= 10, "Not enough arguments!");
			NBTTagCompound region = new NBTTagCompound();
			int x1 = parseInt(sender, args[2]);
			int y1 = parseInt(sender, args[3]);
			int z1 = parseInt(sender, args[4]);
			int x2 = parseInt(sender, args[5]);
			int y2 = parseInt(sender, args[6]);
			int z2 = parseInt(sender, args[7]);
			int dim = parseInt(sender, args[8]);
			int pp = parseInt(sender, args[9]);
			boolean x = x1 < x2;
			boolean y = y1 < y2;
			boolean z = z1 < z2;
			region.setString("type", "cuboid");
			region.setString("name", op2);
			region.setInteger("xMax", x ? x2 : x1);
			region.setInteger("yMax", y ? y2 : y1);
			region.setInteger("zMax", z ? z2 : z1);
			region.setInteger("xMin", x ? x1 : x2);
			region.setInteger("yMin", y ? y1 : y2);
			region.setInteger("zMin", z ? z1 : z2);
			region.setInteger("dim", dim);
			region.setInteger("perms", pp);
			if(region != null) {
				FRegions.instance().submit(region);
				Info(sender, "Added region '" + op2 + "'.");
			}
		} else if(op.equals("remove")) {
			if(FRegions.instance().remove(op2)) {
				Info(sender, "Removed region '" + op2 + "'.");
			} else {
				Info(sender, "Region '" + op2 + "' does not exist!");
			}
		} else if(op.equals("display")) {
			NBTTagCompound region = FRegions.instance().retreive(op2);
			Info(sender, region.toString());
			Info(sender, Long.toBinaryString(region.getInteger("perms")));
		} else Invalid(op);
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? new String[]{"add", "remove", "display"} : 
			args.length == 2 ? FRegions.instance().regions() : 
			args.length == 3 || args.length == 6 ? new String[]{String.valueOf(getLookPos(sender)[0])} : 
			args.length == 4 || args.length == 7 ? new String[]{String.valueOf(getLookPos(sender)[1])} : 
			args.length == 5 || args.length == 8 ? new String[]{String.valueOf(getLookPos(sender)[2])} : 
			sender.getEntityWorld() != null ? new String[]{String.valueOf(sender.getEntityWorld().provider.dimensionId)} : null;
	}
	
}
