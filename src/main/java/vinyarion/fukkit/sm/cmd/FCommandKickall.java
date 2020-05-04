package vinyarion.fukkit.sm.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import vinyarion.fukkit.main.cmd.FCommand;

public class FCommandKickall extends FCommand {
	
	public FCommandKickall(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <kick message...>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		for(Object o : MinecraftServer.getServer().getConfigurationManager().playerEntityList) {
			EntityPlayerMP player = (EntityPlayerMP)o;
			if(player != sender) {
				player.playerNetServerHandler.kickPlayerFromServer(args.length > 0 ? rest(args, 0) : "All players were kicked from the server");
			}
		}
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return null;
	}
	
}
