package vinyarion.fukkit.main.cmd;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import vinyarion.fukkit.main.attrib.FAttribute;
import vinyarion.fukkit.main.attrib.FAttributes;
import vinyarion.fukkit.main.data.FItems;
import vinyarion.fukkit.main.data.FRecipes;
import vinyarion.fukkit.main.game.FItem;

public class FCommandCustomItem extends FCommand {
	
	public FCommandCustomItem(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <add|remove|give> <simpleName> [<player>]|[<parentItem> <displayName>]";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 2, "Not enough args!");
		String op = args[0];
		String op2 = args[1];
		if(op.equals("add")) {
			Assert(args.length >= 4, "Not enough args!");
			String op3 = args[2];
			String op4r = rest(args, 3);
			if(FItems.instance().add(op3, op2, op4r).isoverwrite) {
				Info(sender, "Overwrote item " + op2);
			} else {
				Info(sender, "Added item " + op2);
			}
		} else if(op.equals("remove")) {
			if(FItems.instance().remove(op2)) {
				Info(sender, "Removed item " + op2);
			} else {
				Info(sender, "Item " + op2 + " does not exist!");
			}
		} else if(op.equals("give")) {
			Assert(args.length >= 3, "Not enough args!");
			String op3 = args[2];
			FItem fi = FItems.instance().get(op2);
			if(fi == null) {
				Info(sender, "That item is nonexistant!");
			} else {
				EntityPlayerMP target = playerFor(sender, op3);
				if(target == null) {
					Info(sender, "Must specify a player!");
				} else {
					if(target.inventory.addItemStackToInventory(fi.stack())) {
						Info(sender, "Gave item to player!");
					} else {
						Info(sender, "Couldn't give item to player!");
					}
				}
			}
		} else Invalid(op);
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? new String[]{"add", "remove", "give"} : 
			args.length == 2 ? FItems.instance().names() : 
			args.length == 3 ? Item.itemRegistry.getKeys() : null;
	}
	
}
