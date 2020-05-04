package vinyarion.fukkit.main.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import vinyarion.fukkit.main.util.Time;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FTempPerms {
	
	public static void revokeAll(NBTTagCompound data) {
		data.removeTag(FPermissions.TEMP_TAG);
	}
	
	public static boolean revoke(NBTTagCompound data, String perm) {
		NBTTagList list = NBT.ensureList(data, FPermissions.TEMP_TAG);
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			if(tag.getString("perm").equals(perm)) {
				list.removeTag(i);
				return true;
			}
		}
		return false;
	}
	
	public static boolean isValid(NBTTagCompound data, String perm) {
		NBTTagList list = NBT.ensureList(data, FPermissions.TEMP_TAG);
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			if(tag.getString("perm").equals(perm)) {
				if(Time.hasPassed(tag.getString("given"), tag.getString("duration"))) {
					list.removeTag(i);
					return false;
				}
				return true;
			}
		}
		return false;
	}
	
	public static int add(NBTTagCompound data, String perm, String given, String hhmmss) {
		NBTTagList list = NBT.ensureList(data, FPermissions.TEMP_TAG);
		NBTTagCompound newtag = new NBTTagCompound();
		newtag.setString("perm", perm);
		newtag.setString("given", hhmmss);
		newtag.setString("duration", hhmmss);
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			if(tag.getString("perm").equals(perm)) {
				list.removeTag(i);
				list.appendTag(newtag);
				return 1;
			}
		}
		return -1;
	}
	
}
