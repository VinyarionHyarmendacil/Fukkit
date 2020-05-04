package vinyarion.fukkit.main.memorysubtypes;

import java.security.Permission;

import cpw.mods.fml.relauncher.FMLSecurityManager;

public class FukkitSecurityManager extends FMLSecurityManager {
	
    @Override
    public void checkPermission(Permission perm) {
        String permName = perm.getName() != null ? perm.getName() : "missing";
        if(permName.startsWith("exitVM")) {
            Class<?>[] classContexts = getClassContext();
            String callingClass = classContexts.length > 3 ? classContexts[4].getName() : "none";
            String callingParent = classContexts.length > 4 ? classContexts[5].getName() : "none";
            // FML is allowed to call system exit and the Minecraft applet (from the quit button)
            if (!(callingClass.startsWith("cpw.mods.fml.") || ( "net.minecraft.client.Minecraft".equals(callingClass) && "net.minecraft.client.Minecraft".equals(callingParent)) || ("net.minecraft.server.dedicated.DedicatedServer".equals(callingClass) && "net.minecraft.server.MinecraftServer".equals(callingParent))))
            {
                throw new ExitTrappedException();
            }
        }
        return;
    }
    
}
