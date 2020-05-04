package vinyarion.fukkit.main.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.game.FKit;
import vinyarion.fukkit.main.util.Misc;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FServer {
	
	private static FServer instance = null;
	
	public static FServer instance() {
		return instance;
	}
	
	static {
		instance = new FServer();
		FMod.log(Level.INFO, "FServer initialized!");
	}
	
	private FServer() { }
	
	public final NBTTagCompound tag = FData.instance().tag("server");
	
}
