package vinyarion.fukkit.main.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import vinyarion.fukkit.main.attrib.FAttribute;
import vinyarion.fukkit.main.attrib.FAttributes;
import vinyarion.fukkit.main.data.FRecipes;
import vinyarion.fukkit.main.game.FAesthetics;
import vinyarion.fukkit.main.util.Misc;

public class FCommandParticle extends FCommand {
	
	public FCommandParticle(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <dimension|'own'> <particle> <amount|min:max> [<x> <y> <z> [<dx> <dy> <dz> [<velocity>]]]";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 3, "Not enough args!");
		int dim;
		if(args[0].equals("own")) {
			dim = sender.getEntityWorld().provider.dimensionId;
		} else {
			dim = parseInt(sender, args[0]);
		}
		String name = args[1];
		int amount;
		if(args[2].contains(":")) {
			String[] sa = args[2].split(":");
			amount = parseInt(sender, sa[0]) + Misc.rand.nextInt(parseInt(sender, sa[1]) + 1);
		} else {
			amount = parseInt(sender, args[2]);
		}
		double x, y, z, dx = 0, dy = 0, dz = 0, v = 0;
		if(args.length >= 6) {
			x = parseDouble(sender, args[3]);
			y = parseDouble(sender, args[4]);
			z = parseDouble(sender, args[5]);
		} else {
			x = sender.getPlayerCoordinates().posX;
			y = sender.getPlayerCoordinates().posY;
			z = sender.getPlayerCoordinates().posZ;
		}
		if(args.length >= 9) {
			dx = parseDouble(sender, args[6]);
			dy = parseDouble(sender, args[7]);
			dz = parseDouble(sender, args[8]);
		}
		if(args.length >= 10) {
			v = parseDouble(sender, args[9]);
		}
		FAesthetics.particle(MinecraftServer.getServer().worldServerForDimension(dim), name, x, y, z, dx, dy, dz, v, amount);
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return null;
	}
	
}
