package vinyarion.fukkit.main.remotescript;

import java.util.function.Supplier;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import vinyarion.fukkit.main.data.FPermissions;
import vinyarion.fukkit.main.remotescript.FPHIExecutor.Sender;
import vinyarion.fukkit.main.script.FScriptImprov;
import vinyarion.fukkit.main.script.js.FScriptNashorn;

public class FPHIExecutor {
	
	private static final MinecraftServer mc = MinecraftServer.getServer();
	private static final Sender sender = new Sender();
	private static final ThreadLocal<EntityPlayerMP> threadplayer = 
		ThreadLocal.withInitial(new Supplier<EntityPlayerMP>() { public EntityPlayerMP get() {
			return null;
	}});
	
	private static String name = "Console";
	
	public static void execute(EntityPlayerMP player, String string) {
		if(string.startsWith("@")) {
			name = string.substring(1);
		}
		if(string.startsWith("/")) {
			threadplayer.set(player);
			FPermissions.isOverride.set(true);
			mc.getCommandManager().executeCommand(sender/*RConConsoleSource.instance*/, string);
			FPermissions.isOverride.remove();
			threadplayer.remove();
		}
	}
	
	public static void execute(Entity entity, String string) {
		if(string.startsWith("@")) {
			name = string.substring(1);
		}
		if(string.startsWith("/")) {
			FPermissions.isOverride.set(true);
			mc.getCommandManager().executeCommand(new FPHIContainer(entity), string);
			FPermissions.isOverride.remove();
		}
	}
	
	public static void script(EntityPlayerMP player, String string) {
		new FPHIScript(string.split("\n")).execute(sender, player);
	}
	
	public static void commandlist(EntityPlayerMP player, String string) {
		new FScriptImprov(string).command(player);
	}
	
	public static void groovy(EntityPlayerMP player, String string) {
		FScriptNashorn.Improv.of(string).command(player);
	}
	
	public static class Sender extends RConConsoleSource implements ICommandSender {
		
	    private StringBuffer buffer = new StringBuffer();
		
		public String getCommandSenderName() {
			return name;
		}
		
		public IChatComponent func_145748_c_() {
			return new ChatComponentText(this.getCommandSenderName());
		}
		
		public void addChatMessage(IChatComponent c) {
			if(threadplayer.get() == null) {
				return;
			}
			threadplayer.get().addChatMessage(new ChatComponentText("[PHI] ").appendSibling(c));
		}
		
		public boolean canCommandSenderUseCommand(int t, String s) {
			return true;
		}
		
		public ChunkCoordinates getPlayerCoordinates() {
			return new ChunkCoordinates(0, 0, 0);
		}
		
		public World getEntityWorld() {
			if(threadplayer.get() != null) {
				return threadplayer.get().worldObj;
			}
			return MinecraftServer.getServer().getEntityWorld();
		}
		
		@SideOnly(Side.SERVER)
	    public void resetLog()
	    {
	        this.buffer.setLength(0);
	    }
		
	    @SideOnly(Side.SERVER)
	    public String getLogContents()
	    {
	        return this.buffer.toString();
	    }
		
	}
	
}
