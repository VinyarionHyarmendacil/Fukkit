package vinyarion.fukkit.main.chestguis;

import net.minecraft.entity.player.EntityPlayer;

public interface CGConfig<T> {

	public CGConfig<T> setPlayer(String player);

	public T getValue();

	public static interface Button<T> {

		public CGConfig<T> asConfigElement(String player);

	}

}
