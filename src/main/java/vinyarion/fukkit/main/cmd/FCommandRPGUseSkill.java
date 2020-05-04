package vinyarion.fukkit.main.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import vinyarion.fukkit.rpg.FPlayerDataRPG;

public class FCommandRPGUseSkill extends FCommand {

	public FCommandRPGUseSkill(String phiName, int slot) {
		super(phiName);
		this.slot = slot;
	}

	public String getCommandUsage(ICommandSender sender) {
		return "/" + this.getCommandName();
	}

	private final int slot;

	public void processCommand(ICommandSender sender, String[] args) {
		Assert(sender instanceof EntityPlayerMP, "Must be a player!");
		EntityPlayerMP player = (EntityPlayerMP)sender;
		FPlayerDataRPG skills = FPlayerDataRPG.of(player);
		skills.activePointer = this.slot;
		skills.useActiveSkill();
	}

	public Object tabComplete(ICommandSender sender, String[] args) {
		return null;
	}

}
