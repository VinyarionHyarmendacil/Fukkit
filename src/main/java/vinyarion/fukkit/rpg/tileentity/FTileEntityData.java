package vinyarion.fukkit.rpg.tileentity;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import vinyarion.fukkit.main.util.FieldHandle;
import vinyarion.fukkit.main.util.Misc;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FTileEntityData {

	public static FTileEntityData factory(TileEntity te) {
		return new FTileEntityData(te);
	}

	private static final FieldHandle<TileEntity, FTileEntityData> getter = FieldHandle.of(ReflectionHelper.findField(TileEntity.class, "fukkit_tedata"));
	public static FTileEntityData of(TileEntity te) {
		return getter.get(te);
	}

	public UUID owner = Misc.zero;
	public List<String> users = Lists.newArrayList();
	public boolean isBlacklist = false;

	private NBTTagCompound data = new NBTTagCompound();

	public NBTTagCompound getData() {
		return data;
	}

	private FTileEntityData(TileEntity te) {
//		System.out.println("Instantiating a "+te.getClass().getSimpleName());
	}

	public void hook_readFromNBT(TileEntity te, NBTTagCompound tag) {
		tag = NBT.ensure(tag, "Fukkit");
		try {
			owner = UUID.fromString(tag.getString("owner"));
		} catch(Exception e) {
			owner = Misc.zero;
			System.out.println("A "+te.getClass()+" had a bad uuid, using zero instead! ("+te.xCoord+" "+te.yCoord+" "+te.zCoord+")");
		}
		users = NBT.toListString(tag.getTagList("users", NBT.STRING));
		isBlacklist = tag.getBoolean("isBlacklist");
		data = tag.getCompoundTag("data");
	}

	public void hook_writeToNBT(TileEntity te, NBTTagCompound tag) {
		tag = NBT.ensure(tag, "Fukkit");
		try {
			tag.setString("owner", owner.toString());
		} catch(Exception e) {
			tag.setString("owner", Misc.zero.toString());
			System.out.println("A "+te.getClass()+" had a bad uuid, using zero instead! ("+te.xCoord+" "+te.yCoord+" "+te.zCoord+")");
		}
		tag.setTag("users", NBT.toListTagString(users));
		tag.setBoolean("isBlacklist", isBlacklist);
		tag.setTag("data", data);
	}

}
