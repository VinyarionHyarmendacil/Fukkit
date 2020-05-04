package vinyarion.fukkit.main.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import vinyarion.fukkit.main.attrib.FAttribute;
import vinyarion.fukkit.main.attrib.FAttributes;
import vinyarion.fukkit.main.data.FArmorSetEffects;
import vinyarion.fukkit.main.util.Effects;

public class FCommandArmorSetEffects extends FCommand {
	
	public FCommandArmorSetEffects(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <add|remove|view> <name> [<effect;effect;...>]";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 2, "Not enough args!");
		String op = args[0];
		String op2 = args[1];
		if(op.equals("add")) {
			Assert(args.length >= 3, "Not enough args!");
			String op3 = args[2];
			if(FArmorSetEffects.instance().add(op2, op3)) {
				Info(sender, "Overwrote set " + op2 + " with effects " + op3);
			} else {
				Info(sender, "Added set " + op2 + " with effects " + op3);
			}
		} else if(op.equals("remove")) {
			if(FArmorSetEffects.instance().remove(op2)) {
				Info(sender, "Removed set " + op2);
			} else {
				Info(sender, "Set " + op2 + " does not exist!");
			}
		} else if(op.equals("view")) {
			Info(sender, FArmorSetEffects.instance().get(op2));
		} else Invalid(op);
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? new String[]{"add", "remove", "view"} : 
			args.length == 2 ? FArmorSetEffects.instance().sets() : 
			args.length == 3 ? Effects.list(args[2]) : null;
	}
	
}
