package vinyarion.fukkit.main.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import vinyarion.fukkit.main.attrib.FAttribute;
import vinyarion.fukkit.main.attrib.FAttributes;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FCommandWand extends FCommand {
	
	public FCommandWand(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <set|remove|display> [<attributeName|'all'> [<value>]]";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(sender instanceof EntityPlayerMP, "Must be a player!");
		EntityPlayerMP player = (EntityPlayerMP)sender;
		Assert(player.getHeldItem() != null, "Must hold something!");
		ItemStack held = player.getHeldItem();
		Assert(args.length >= 2, "Not enough args!");
		String op = args[0];
		String op2 = args[1];
		NBTTagCompound tag = NBT.ensure(held, "Staves");
		if(op.equals("set")){
			Assert(args.length >= 3, "Not enough args!");
			String op3 = rest(args, 2);
			if(op2.equals("particleFly")) {
				tag.setString("particleFly", op3);
			} else if(op2.equals("soundFly")) {
				tag.setString("soundFly", op3);
			} else if(op2.equals("velocityFly")) {
				tag.setFloat("velocityFly", (float)parseDouble(sender, op3));
			} else if(op2.equals("particleHit")) {
				tag.setString("particleHit", op3);
			} else if(op2.equals("soundHit")) {
				tag.setString("soundHit", op3);
			} else if(op2.equals("effectHit")) {
				tag.setString("effectHit", op3);
			} else if(op2.equals("effectValue")) {
				tag.setString("effectValue", op3);
			} else if(op2.equals("areaSize")) {
				tag.setDouble("areaSize", parseDouble(sender, op3));
			} else if(op2.equals("shots")) {
				tag.setInteger("shots", parseInt(sender, op3));
			} else if(op2.equals("deviation")) {
				tag.setDouble("deviation", parseDouble(sender, op3));
			} else if(op2.equals("flame")) {
				tag.setBoolean("flame", parseBoolean(sender, op3));
			} else if(op2.equals("gravity")) {
				tag.setDouble("gravity", parseDouble(sender, op3));
			} else Invalid(op3);
			Info(sender, "Added attribute " + op2 + ", value " + op3 + "!");
		} else if(op.equals("remove")){
			tag.removeTag(op2);
			Info(sender, "Removed attribute " + op2 + "!");
		} else if(op.equals("display")){
			if(op2.equals("all")) {
				Info(sender, "Values:");
				if(tag.hasKey("particleFly")) {
					Info(sender, "particleFly: " + tag.getString("particleFly"));
				}
				if(tag.hasKey("soundFly")) {
					Info(sender, "soundFly: " + tag.getString("soundFly"));
				}
				if(tag.hasKey("velocityFly")) {
					Info(sender, "velocityFly: " + tag.getFloat("velocityFly"));
				}
				if(tag.hasKey("particleHit")) {
					Info(sender, "particleHit: " + tag.getString("particleHit"));
				}
				if(tag.hasKey("soundHit")) {
					Info(sender, "soundHit: " + tag.getString("soundHit"));
				}
				if(tag.hasKey("effectHit")) {
					Info(sender, "effectHit: " + tag.getString("effectHit"));
				}
				if(tag.hasKey("effectValue")) {
					Info(sender, "effectValue: " + tag.getDouble("effectValue"));
				}
				if(tag.hasKey("areaSize")) {
					Info(sender, "areaSize: " + tag.getDouble("areaSize"));
				}
				if(tag.hasKey("shots")) {
					Info(sender, "shots: " + tag.getInteger("shots"));
				}
				if(tag.hasKey("deviation")) {
					Info(sender, "deviation: " + tag.getDouble("deviation"));
				}
				if(tag.hasKey("flame")) {
					Info(sender, "flame: " + tag.getBoolean("flame"));
				}
				if(tag.hasKey("gravity")) {
					Info(sender, "gravity: " + tag.getDouble("gravity"));
				}
			} else {
				Info(sender, "Value of " + String.valueOf(tag.getTag(op2)));
			}
		} else Invalid(op);
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? new String[]{"set", "remove", "display"} : 
			args.length == 2 ? new String[]{"particleFly", "soundFly", "velocityFly", "particleHit", "soundHit", "effectHit", "effectValue", "areaSize", "shots", "deviation", "flame", "gravity"} : 
			null;
	}
	
}
