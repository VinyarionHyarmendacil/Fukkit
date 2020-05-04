package vinyarion.fukkit.main.cmd;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.Vec3;
import vinyarion.fukkit.main.util.Misc;

public class FCommandVelocity extends FCommand {
	
	public FCommandVelocity(String phiName) {
		super(phiName);
	}
	
	public String getCommandUsage(ICommandSender sender) {
		return "/" + getCommandName() + " <selector> <add|set> <grid|interp> <vx> <vy> <vz>";
	}
	
	public void processCommand(ICommandSender sender, String[] args) {
		Assert(args.length >= 6, "Not enough args!");
		String op = args[0];
		String op2 = args[1];
		String op3 = args[2];
		String op4 = args[3];
		String op5 = args[4];
		String op6 = args[5];
		double vx = parseDouble(sender, op4);
		double vy = parseDouble(sender, op5);
		double vz = parseDouble(sender, op6);
		EntityPlayerMP player = playerFor(sender, op);
		if(op3.equals("interp")) {
			Vec3 l = Vec3.createVectorHelper(vx, vy, vz);
            l.rotateAroundX(-player.rotationPitch * (float)Math.PI / 180.0F);
            l.rotateAroundY(-player.rotationYaw * (float)Math.PI / 180.0F);
            vx = l.xCoord;
            vy = l.yCoord;
            vz = l.zCoord;
		} else if(op3.equals("grid")) {
		} else Invalid(op3);
		if(op2.equals("add")) {
			player.addVelocity(vx, vy, vz);
			player.velocityChanged = true;
		} else if(op2.equals("set")) {
			player.motionX = vx;
			player.motionY = vy;
			player.motionZ = vz;
			player.velocityChanged = true;
		} else Invalid(op2);
	}
	
	public Object tabComplete(ICommandSender sender, String[] args) {
		return args.length == 1 ? players() : 
			args.length == 2 ? new String[]{"add", "set"} : 
			args.length == 3 ? new String[]{"grid", "interp"} : null;
	}
	
}
