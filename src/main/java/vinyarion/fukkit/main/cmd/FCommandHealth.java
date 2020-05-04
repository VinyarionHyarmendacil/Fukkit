package vinyarion.fukkit.main.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import vinyarion.fukkit.main.attrib.FAttribHealthBoost;

public class FCommandHealth extends FCommand {

	public FCommandHealth(String phiName) {
		super(phiName);
	}

	public String getCommandUsage(ICommandSender sender) {
		return "/" + this.getCommandName() + " <player> <max|current> <value> [<add>]";
	}

	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length > 2, "Not enough args!");
		EntityPlayerMP player = getPlayer(sender, args[0]);
		Assert(player != null, "Must specify a player!");
		String op = args[1];
		double value = parseDoubleWithMin(sender, args[2], 0);
		if(op.equals("max")) {
			IAttributeInstance iai = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
			AttributeModifier am = iai.getModifier(FAttribHealthBoost.uuid_command);
			if(args.length > 3 && args[3].equals("add")) {
				value += iai.getAttributeValue();
			}
			if(am != null) {
				iai.removeModifier(am);
			}
			if(value == 0D) {
				Info(sender, "Set " + args[0] + "'s max health to 20, the normal amount");
			} else {
				iai.applyModifier(new AttributeModifier(FAttribHealthBoost.uuid_command, "commandhealth", value - 20D, 0));
				Info(sender, "Set " + args[0] + "'s max health");
			}
			if(player.getHealth() > player.getMaxHealth()) {
				player.setHealth(player.getMaxHealth());
			}
		} else if(op.equals("current")) {
			if(args.length > 3 && args[3].equals("add")) {
				value += player.getHealth();
			}
			player.setHealth((float)value);
			Info(sender, "Set " + args[0] + "'s health");
		} else Invalid(op);
	}

	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? players() : 
			args.length == 2 ? new String[]{"max", "current"} : 
			null;
	}

}
