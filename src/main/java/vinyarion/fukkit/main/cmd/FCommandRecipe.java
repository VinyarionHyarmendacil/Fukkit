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
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FCommandRecipe extends FCommand {
	
	public FCommandRecipe(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <add|remove|display> [<recipeName>|<valid recipe nbt>]";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 2, "Not enough arguments!");
		String op = args[0];
		String op2 = args[1];
		if(op.equals("add")) {
			NBTTagCompound recipe = null;
			try {
				recipe = (NBTTagCompound)JsonToNBT.func_150315_a(rest(args, 1));
			} catch(Exception e) {
				Error("Not a valid NBT tag!");
			}
			if(recipe != null) {
				FRecipes.instance().submit(recipe);
				Info(sender, "Added recipe '" + op2 + "'.");
			}
		} else if(op.equals("remove")) {
			if(FRecipes.instance().remove(op2)) {
				Info(sender, "Removed recipe '" + op2 + "'.");
			} else {
				Info(sender, "Recipe '" + op2 + "' does not exist!");
			}
		} else if(op.equals("display")) {
			NBTTagCompound recipe = FRecipes.instance().retreive(op2);
			Info(sender, recipe.toString());
		} else Invalid(op);
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? new String[]{"add", "remove", "display"} : 
			args.length == 2 ? (args[0].equals("add") ? new String[0] : FRecipes.instance().recipes()) : null;
	}
	
}
