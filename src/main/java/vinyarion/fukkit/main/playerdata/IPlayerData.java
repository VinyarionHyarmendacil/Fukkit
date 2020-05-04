package vinyarion.fukkit.main.playerdata;

import net.minecraft.entity.player.EntityPlayer;
import vinyarion.fukkit.main.util.nbt.INBTSerializable;

public interface IPlayerData extends INBTSerializable {
	
	public void generateDefaults();
	
	public String getID();
	
	public void onLoad(FPlayerData data);
	
	public FPlayerData getData();

	public interface Sub extends IPlayerData {

		public IPlayerData getParent();

		public default FPlayerData getData() {
			return getParent().getData();
		}

	}
	
}
