package vinyarion.fukkit.rpg;

import net.minecraft.init.Blocks;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = FRPG.MODID, version = FRPG.VERSION, acceptableRemoteVersions = "*", dependencies = "required-after:fukkit")
public class FRPG {
	
    public static final String MODID = "fukkit_rpg";
    public static final String VERSION = "1.0";
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
		
    }
    
}
