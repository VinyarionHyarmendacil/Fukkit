package vinyarion.fukkit.main.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import vinyarion.fukkit.main.attrib.FAttribute;
import vinyarion.fukkit.main.attrib.FAttributes;
import vinyarion.fukkit.main.data.FRecipes;

public class FCommandAttribute extends FCommand {
	
	public FCommandAttribute(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <add|remove|help> <attribute> [<attribute args>...]";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(sender instanceof EntityPlayerMP, "Must be a player!");
		EntityPlayerMP player = (EntityPlayerMP)sender;
		Assert(player.getHeldItem() != null, "Must hold something!");
		ItemStack held = player.getHeldItem();
		Assert(args.length >= 2, "Not enough args!");
		String op = args[0];
		String op2 = args[1];
		FAttribute a = FAttributes.get(op2);
		Assert(a != null, "Invalid attribute!");
		if(op.equals("add")) {
			Assert(a != FAttributes.forgingRandomness, "You can not manually add this attribute, it is for internal use only!");
			String[] rest;
			if(!a.desc.startsWith("None")) {
				Assert(args.length >= 3, "Not enough args!");
				rest = rest(args, 2).split(" ");
			} else {
				rest = new String[0];
			}
			try {
				a.addTo(held, rest);
				Info(sender, "Added " + op2 + " to held item!");
			} catch(Exception e) {
				Info(sender, "Weird! " + e.toString() + " @ " + e.getStackTrace()[0].toString());
				Error("Failed to add " + op2 + " to held item! Use: " + a.desc);
			}
		} else if(op.equals("remove")) {
			Assert(a != FAttributes.forgingRandomness, "You can not manually remove this attribute, it is for internal use only!");
			if(a.isOn(held)) {
				a.removeFrom(held);
				Info(sender, "Removed " + op2 + " to held item!");
			} else {
				Error("Attribute " + op2 + " is not in this item!");
			}
		} else if(op.equals("help")) {
			Info(sender, "Help for " + op2 + ": " + a.desc);
		} else Invalid(op);
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? new String[]{"add", "remove", "help"} : 
			args.length == 2 ? FAttributes.names : 
			args.length == 3 ? 
				args[1].equals(FAttributes.gainRecipe.name) ? FRecipes.instance().recipes() : null : null;
	}
	
}
