package vinyarion.fukkit.main.cmd;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.util.Colors;

public class FCommandPerms extends FCommand {
	
	public FCommandPerms(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <perm|user> <add|remove|list> <permName|'attributes'> [<attributeName|playerName>]";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		if(args.length < 3) {
			throw new CommandException("Not enough arguments!");
		}
		String op = args[0];
		String op2 = args[1];
		String op3 = args.length <3 ? "" : args[2];
		if(op.equals("perm")) {
			if(op2.equals("add")) {
				if(args.length == 4) {
					String att = args[3];
					FPermissions.instance().addGroup(op3, att);
					Info(sender, "Added permission '" + att + "' to group '" + op3 + "'.");
				} else {
					FPermissions.instance().addGroup(op3);
					Info(sender, "Added group '" + op3 + "'.");
				}
			} else if(op2.equals("remove")) {
				if(args.length == 4) {
					String att = args[3];
					FPermissions.instance().removeGroup(op3, att);
					Info(sender, "Removed perrmission '" + att + "' from group '" + op3 + "'.");
				} else {
					FPermissions.instance().removeGroup(op3);
					Info(sender, "Removed group '" + op3 + "'.");
				}
			} else if(op2.equals("list")) {
				if(op3.length() == 0) {
					Info(sender, "Permissions:");
					for(String p : FPermissions.instance().perms()) {
						Info(sender, "-->" + p);
					}
				} else if(op3.equals("attributes")) {
					Info(sender, "Attributes:");
					for(String p : FPermissions.instance().attribs()) {
						Info(sender, "-->" + p);
					}
				} else {
					Info(sender, "Attributes of " + op3 + ":");
					for(String p : FPermissions.instance().perms(op3)) {
						Info(sender, "-->" + p);
					}
				}
			}
		} else if(op.equals("user")) {
			Assert(args.length >= 4, "Must give a player name!");
			String op4 = args[3];
			if(op2.equals("add")) {
				FPermissions.instance().addPermission(op3, playerFor(sender, op4));
				NotifyAll(sender, "Added permission '" + op3 + "' to player '" + op4 + "'.");
			} else if(op2.equals("remove")) {
				FPermissions.instance().removePermission(op3, playerFor(sender, op4));
				NotifyAll(sender, "Removed permission '" + op3 + "' from player '" + op4 + "'.");
			} else if(op2.equals("list")) {
				Info(sender, op4 + "'s permissions:");
				for(String p : FPermissions.instance().perms(playerFor(sender, op4))) {
					Info(sender, "-->" + p);
				}
			}
		}
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? new String[]{"perm", "user"} : 
			args.length == 2 ? new String[]{"add", "remove", "list"} : 
			args.length == 3 ? FPermissions.instance().perms() : 
			args[0].equals("user") ? MinecraftServer.getServer().getAllUsernames() : FPermissions.instance().attribs();
	}
	
}
