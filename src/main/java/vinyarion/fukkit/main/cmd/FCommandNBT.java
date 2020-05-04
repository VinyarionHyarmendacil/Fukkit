package vinyarion.fukkit.main.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FCommandNBT extends FCommand {
	
	public FCommandNBT(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <name|lore_add|lore_remove|unbreakable> <value>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(sender instanceof EntityPlayerMP, "Must be a player!");
		EntityPlayerMP player = (EntityPlayerMP)sender;
		Assert(player.getHeldItem() != null, "Must hold something!");
		ItemStack held = player.getHeldItem();
		Assert(args.length >= 2, "Not enough args!");
		String op = args[0];
		String op2 = Colors.color(rest(args, 1));
		if(op.equals("name")) {
			held.setStackDisplayName(op2);
			Info(sender, "Set item name to " + Colors.uncolor(op2));
		} else if(op.equals("lore_add")) {
			NBT.ensureList(NBT.ensure(held, "display"), "Lore").appendTag(new NBTTagString(op2));
			Info(sender, "Added lore line " + Colors.uncolor(op2));
		} else if(op.equals("lore_remove")) {
			try {
				NBTTagList list = held.getTagCompound().getCompoundTag("display").getTagList("Lore", NBT.STRING);
				int i = Integer.parseInt(op2);
				String say = list.getStringTagAt(i);
				list.removeTag(i);
    			Info(sender, "Removed lore line '" + say + "&r&a' at line " + i);
			} catch(Exception e) {
				Error("Tag index not a number, or it is out of bounds!");
			}
		} else if(op.equals("unbreakable")) {
    		NBT.ensure(held, null);
    		if(held.getTagCompound().hasKey("Unbreakable")) {
    			held.getTagCompound().removeTag("Unbreakable");
    			Info(sender, "Removed unbreakability.");
    		} else {
    			held.getTagCompound().setBoolean("Unbreakable", true);
    			Info(sender, "Added unbreakability.");
    		}
		} else Invalid(op);
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? new String[]{"name", "lore_add", "lore_remove", "unbreakable"} : null;
	}
	
}
