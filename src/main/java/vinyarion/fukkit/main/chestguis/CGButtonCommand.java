package vinyarion.fukkit.main.chestguis;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import vinyarion.fukkit.main.data.FPermissions;

public class CGButtonCommand implements CGButton {

	private String[] commands = null;
	private ItemStack stack = null;

	public CGButtonCommand with(String command) {
		this.commands = command.split(InventoryChestHijacked.REGEX);
		return this;
	}

	public CGButtonCommand with(String... commands) {
		this.commands = commands;
		return this;
	}

	public CGButtonCommand with(ItemStack stack) {
		this.stack = stack;
		return this;
	}

	public void onSlotClicked(EntityPlayer player, ChestGui gui, int slot, boolean left, boolean right, boolean middle, boolean isShift) {
		FPermissions.isOverride.set(true);
		for(String line : this.commands) {
			if(line.length() <= 2) continue;
			String how = line.substring(0, 2);
			String cmd = line.substring(2);
			if(how.equals("p:")) {
				MinecraftServer.getServer().getCommandManager().executeCommand(player, cmd);
			} else if(how.equals("s:")) {
				MinecraftServer.getServer().getCommandManager().executeCommand(MinecraftServer.getServer(), cmd.replace("{PLAYER}", player.getCommandSenderName()));
			}
		}
		FPermissions.isOverride.remove();
	}

	public ItemStack display(EntityPlayer player, ChestGui gui) {
		return this.stack.copy();
	}

}
