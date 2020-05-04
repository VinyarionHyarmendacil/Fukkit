package vinyarion.fukkit.main.chestguis;

import java.util.function.Function;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.nbt.NBT;

public class CGButtonToggle extends CGButtonSelect implements CGConfig.Button<Boolean> {

	public CGButtonToggle(String name, String setting, ItemStack display) {
		super(name, display);
		this.setting = setting;
	}

	public CGButtonToggle(String name, String setting, Function<EntityPlayer, ItemStack> display) {
		super(name, display);
		this.setting = setting;
	}

	private final String setting;

	public boolean isSelected(EntityPlayer player, ChestGui gui) {
		return FPlayerData.forPlayer(player).tagSettings().getBoolean(this.setting);
	}

	public boolean canSelect(EntityPlayer player, ChestGui gui) {
		return !FPlayerData.forPlayer(player).tagSettings().getBoolean(this.setting);
	}

	public boolean canDeselect(EntityPlayer player, ChestGui gui) {
		return FPlayerData.forPlayer(player).tagSettings().getBoolean(this.setting);
	}

	public void onSelect(EntityPlayer player, ChestGui gui) {
		FPlayerData.forPlayer(player).tagSettings().setBoolean(this.setting, true);
		player.addChatMessage(Colors.CHAT("&aEnabled option."));
		gui.refresh(player);
	}

	public void onDeselect(EntityPlayer player, ChestGui gui) {
		FPlayerData.forPlayer(player).tagSettings().setBoolean(this.setting, false);
		player.addChatMessage(Colors.CHAT("&aDisabled option."));
		gui.refresh(player);
	}

	public ItemStack display(EntityPlayer player, ChestGui gui) {
		ItemStack ret = super.display(player, gui).copy();
		if(this.isSelected(player, gui)) {
			NBT.ensureList(ret.getTagCompound(), "ench");
		}
		return ret;
	}

	public CGToggle asConfigElement(String player) {
		return new CGToggle().setPlayer(player);
	}

	public class CGToggle implements CGConfig<Boolean> {
		private String player;
		public Boolean getValue() {
			return FPlayerData.forPlayer(this.player).tagSettings().getBoolean(CGButtonToggle.this.setting);
		}
		public CGToggle setPlayer(String player) {
			this.player = player;
			return this;
		}
	}

}
