package vinyarion.fukkit.main.cmd;

import com.google.common.collect.Lists;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import vinyarion.fukkit.main.util.Colors;

public class FCommandHeldItem extends FCommand {
	
	public FCommandHeldItem(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " [<action>]";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(sender instanceof EntityPlayerMP, "Must be a player!");
		EntityPlayerMP player = (EntityPlayerMP)sender;
		Assert(player.getHeldItem() != null, "Must hold an item!");
		ItemStack held = player.getHeldItem();
		String op = "holds";
		if(args.length > 0) {
			op = rest(args, 0);
		}
		String post = "";
		if(op.contains("~item")) {
			String[] sa = op.split("~item", 2);
			op = sa[0].trim();
			post = sa[1].trim();
		}
		op = " "+Colors.RESET+Colors.GRAY+Colors.color(op)+Colors.RESET+" ";
		post = post.length() > 0 ? " "+Colors.RESET+Colors.GRAY+Colors.color(post) : post;
		IChatComponent say = FCommandTitles.getClickableFormattedTitle(player).appendText(op).appendSibling(held.func_151000_E()).appendText(post);
		MinecraftServer.getServer().getConfigurationManager().sendChatMsg(say);
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return Lists.asList("~item", players());
	}
	
}
