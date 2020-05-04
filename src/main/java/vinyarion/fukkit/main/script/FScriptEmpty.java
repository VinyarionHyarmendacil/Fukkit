package vinyarion.fukkit.main.script;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

public class FScriptEmpty extends FScript {

	public FScriptEmpty() {
		super(Collections.EMPTY_LIST);
	}

	public void command(EntityPlayer player) {
		// We want absolutely no behavior.
	}

}
