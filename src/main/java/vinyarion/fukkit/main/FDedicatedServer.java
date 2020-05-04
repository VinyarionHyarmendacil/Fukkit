package vinyarion.fukkit.main;

import java.io.File;

import net.minecraft.server.dedicated.DedicatedServer;

public class FDedicatedServer extends DedicatedServer {

	@Deprecated
	public FDedicatedServer(File file) {
		super(file);
	}

	@Override
	public void stopServer() {
		try {
			super.stopServer();
		} finally {
			FMod.theMod.serverStopImpl();
		}
	}

}
