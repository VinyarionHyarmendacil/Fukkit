package vinyarion.fukkit.main.cmd;

import java.util.UUID;

import lotr.common.fac.LOTRFaction;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import vinyarion.fukkit.main.data.FServer;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.rpg.FPlayerDataRPG;
import vinyarion.fukkit.rpg.archetypes.FClass;
import vinyarion.fukkit.rpg.archetypes.FClasses;
import vinyarion.fukkit.rpg.archetypes.Faction;
import vinyarion.fukkit.rpg.archetypes.Race;

public class FCommandRPGFactionLeaders extends FCommand {
	
	public FCommandRPGFactionLeaders(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + this.getCommandName() + " <faction> <add|remove> <leader|officer> <player>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 4, "Not enough args!");
		Faction f = Faction.of(args[0]);
		Assert(f != Faction.of(LOTRFaction.UNALIGNED), "This faction can not have leaders!");
		String op = args[1];
		String type = args[2];
		EntityPlayerMP target = playerFor(sender, args[3]);
		Assert(target != null, "Must specify a player!");
		UUID uuid = target.getGameProfile().getId();
		if("add".equalsIgnoreCase(op)) {
			if("leader".equalsIgnoreCase(op)) {
				if(f.leaders.contains(uuid)) {
					Error(target.getCommandSenderName()+" is already a leader of "+f.name);
				} else {
					f.leaders.add(uuid);
					sender.addChatMessage(Colors.make(target.getCommandSenderName() + " is now a leader of " + f.name));
				}
			} else if("officer".equalsIgnoreCase(op)) {
				if(f.officers.contains(uuid)) {
					Error(target.getCommandSenderName()+" is already an officer of "+f.name);
				} else {
					f.officers.add(uuid);
					sender.addChatMessage(Colors.make(target.getCommandSenderName()+" is now an officer of "+f.name));
				}
			} else Invalid(op);
		} else if("remove".equalsIgnoreCase(op)) {
			if("leader".equalsIgnoreCase(op)) {
				if(!f.leaders.contains(uuid)) {
					Error(target.getCommandSenderName()+" is not a leader of "+f.name);
				} else {
					f.leaders.remove(uuid);
					sender.addChatMessage(Colors.make(target.getCommandSenderName()+" is no longer a leader of "+f.name));
				}
			} else if("officer".equalsIgnoreCase(op)) {
				if(!f.officers.contains(uuid)) {
					Error(target.getCommandSenderName()+" is not an officer of "+f.name);
				} else {
					f.officers.remove(uuid);
					sender.addChatMessage(Colors.make(target.getCommandSenderName()+" is no longer an officer of "+f.name));
				}
			} else Invalid(op);
		} else Invalid(op);
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? Faction.names : 
			args.length == 2 ? new String[]{"add","remove"} : 
			args.length == 3 ? new String[]{"leader","officer"} : 
			args.length == 4 ? players() : 
			null;
	}
	
}
