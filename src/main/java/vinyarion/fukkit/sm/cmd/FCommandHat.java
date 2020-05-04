package vinyarion.fukkit.sm.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.server.MinecraftServer;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.fukkit.main.util.Colors;

public class FCommandHat extends FCommand {
	
	public FCommandHat(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender args) {
		return "/" + getCommandName();
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(sender instanceof EntityPlayerMP, "Must be a player!");
		EntityPlayerMP player = (EntityPlayerMP)sender;
		ItemStack held = player.inventory.mainInventory[player.inventory.currentItem];
		player.inventory.mainInventory[player.inventory.currentItem] = player.inventory.armorInventory[3];
		player.inventory.armorInventory[3] = held;
		MinecraftServer.getServer().getConfigurationManager().syncPlayerInventory(player);
		println(sender, "There you go, a hat!");
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return null;
	}
	
}
