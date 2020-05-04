package vinyarion.fukkit.sm;

import net.minecraft.init.Blocks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = FSM.MODID, version = FSM.VERSION, acceptableRemoteVersions = "*", dependencies = "required-after:fukkit")
public class FSM {
	
    public static final String MODID = "fukkit_sm";
    public static final String VERSION = "1.0";
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
		
    }
    
}
