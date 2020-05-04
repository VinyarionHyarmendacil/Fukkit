package vinyarion.fukkit.main.attrib;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import vinyarion.fukkit.main.game.FPlayer;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FAttribDurability extends FAttribute<Boolean> {

	public FAttribDurability(String hexid, String name, String desc) {
		super(hexid, name, desc);
	}
	
	public int addTo(ItemStack stack, String... params) {
		NBTTagCompound tag = NBT.ensure(stack, "Uses");
		int max = Integer.parseInt(params[0]);
		int cur = Integer.parseInt(params[1]);
		if(max == -1) {
			this.setOwnedLine(stack, "Uses: infinite");
		} else {
			tag.setInteger("max", max);
			tag.setInteger("current", cur);
			tag.setBoolean("rechargable", Boolean.parseBoolean(params[2]));
			this.setOwnedLine(stack, "Uses: " + Math.max(0, cur) + "/" + max);
		}
		return 0;
	}
	
	public Boolean update(EntityPlayer player, ItemStack stack) {
		NBTTagCompound tag = stack.getTagCompound().getCompoundTag("Uses");
		int max = tag.getInteger("max");
		if(max == -1) {
			return true;
		}
		int current = tag.getInteger("current");
		if(current <= 0) {
			if(tag.getBoolean("rechargable")) {
				if(current == 0) {
					current = -1;
					player.addChatMessage(Colors.make(Colors.color("&cYour &r" + stack.getDisplayName() + "&r&c has no more uses!")));
				}
			}
			return true;
		}
		current--;
		if(current <= 0) {
			FPlayer.decrementHeld(player, 1);
			current = max;
			player.addChatMessage(Colors.make(Colors.color("&cYour &r" + stack.getDisplayName() + "&r&c broke!")));
			return true;
		}
		tag.setInteger("current", current);
		this.setOwnedLine(stack, "Uses: " + Math.max(0, current) + "/" + max);
		return false;
	}
	
	public void removeFrom(ItemStack stack) {
		stack.getTagCompound().removeTag("Uses");
		this.removeOwnedLine(stack);
	}

}
