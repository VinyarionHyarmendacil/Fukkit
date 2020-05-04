package vinyarion.fukkit.main.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.CommandEvent;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.playerdata.FPlayerData;
import vinyarion.fukkit.main.util.Misc;
import vinyarion.fukkit.main.util.nbt.NBT;

public class FPermissions {
	
	public static ThreadLocal<Boolean> isOverride = ThreadLocal.withInitial(() -> Boolean.FALSE);

	public static void run(Runnable in) {
		runnable(in).run();
	}
	public static <T> T call(Callable<T> in) throws Exception {
		return callable(in).call();
	}
	public static Runnable runnable(Runnable in) {
		return ()->{
			isOverride.set(true);
			try {
				in.run();
			} finally {
				isOverride.remove();
			}
		};
	}
	public static <T> Callable<T> callable(Callable<T> in) {
		return ()->{
			isOverride.set(true);
			try {
				return in.call();
			} finally {
				isOverride.remove();
			}
		};
	}
	
	/*
	 * other permissions to add:
	 *    region.[name]
	 *    pickup.[item]
	 *    crafting.[name]
	 */
	
	public static class Others {
		
		public static final String perm_ColorSign = "misc.colorSign";
		public static final String perm_ColorChat = "misc.colorChat";
		public static final String perm_NickOthers = "misc.nickOthers";
		public static final String perm_CommandOutput = "misc.cmdListen";
		public static final String perm_EditCreativeItems = "misc.editNbtCreative";
		public static final String perm_UseMapFT = "misc.useMapFT";
		public static final String perm_AnnexBlocks = "misc.annexBlocks";
		
		public static final List<String> miscPerms = new ArrayList<String>();
		
		static {
			for(Field f : Others.class.getDeclaredFields()) {
				try {
					f.setAccessible(true);
					String a = (String)f.get(null);
					miscPerms.add(a);
				} catch (Exception e) { }
			}
		}
	}

	public static final String TEMP_TAG = "temporary_permissions";
	public static final String PERMISSION_TAG = "command_permissions";
	
	public static boolean beStrict = false;
	
	private static FPermissions instance;
	
	private NBTTagCompound tag;
	
	static {
		instance = new FPermissions();
		instance.tag = FData.instance().tag(PERMISSION_TAG);
		FMod.log(Level.INFO, "FPermissions initialized!");
	}
	
	public static FPermissions instance() {
		return instance;
	}
	
	public boolean hasGroup(EntityPlayer player, String string) {
		NBTTagCompound data = FPlayerData.forPlayer(player.getCommandSenderName()).tag();
		if(!data.hasKey(PERMISSION_TAG)) {
			NBTTagList pl = new NBTTagList();
			data.setTag(PERMISSION_TAG, pl);
			return false;
		}
		NBTTagList list = data.getTagList(PERMISSION_TAG, NBT.STRING);
		for(int i = 0; i < list.tagCount(); i++) {
			if(string.equals(list.getStringTagAt(i))) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasPermission(EntityPlayer player, String string) {
		if(player == null) {
			return false;
		}
		if(isOverride.get()) {
			return true;
		}
		if(MinecraftServer.getServer().getConfigurationManager().func_152596_g(player.getGameProfile())) {
			return true;
		}
		NBTTagCompound data = FPlayerData.forPlayer(player.getCommandSenderName()).tag();
		if(!data.hasKey(PERMISSION_TAG)) {
			NBTTagList pl = new NBTTagList();
			pl.appendTag(new NBTTagString("default"));
			data.setTag(PERMISSION_TAG, pl);
			return false;
		}
		NBTTagList list = data.getTagList(PERMISSION_TAG, NBT.STRING);
		List<String> perms = getPermissionEntries(string);
		for(int i = 0; i < list.tagCount(); i++) {
			String userperm = list.getStringTagAt(i);
			if(userperm.contains(".") && userperm.equals(string)) {
				return true;
			}
			for(String perm : perms) {
				if(perm.startsWith("inherit.")) {
					String inh = perm.substring(8, perm.length());
					for(String sub : getPermissionEntries(inh)) {
						if(sub.equals(userperm) || sub.equals("*")) {
							return true;
						}
					}
				} else if(perm.equals(userperm) || perm.equals("*")) {
					return true;
				}
			}
		}
		if(FTempPerms.isValid(data, string)) {
			return true;
		}
		return false;
	}

//	public boolean hasPermission(CommandEvent chat) {
//		NBTTagCompound data = FPlayerData.forPlayer(chat.sender.getCommandSenderName()).tag();
//		if(!data.hasKey(PERMISSION_TAG)) {
//			NBTTagList pl = new NBTTagList();
//			pl.appendTag(new NBTTagString("default"));
//			data.setTag(PERMISSION_TAG, pl);
//			return beStrict ? false : chat.command.canCommandSenderUseCommand(chat.sender);
//		}
//		NBTTagList list = data.getTagList(PERMISSION_TAG, NBT.STRING);
//		if(list.getStringTagAt(0).equals("*")) {
//			return true;
//		}
//		List<String> perms = getPermissionEntries(chat.command);
//		for(int i = 0; i < list.tagCount(); i++) {
//			String userperm = list.getStringTagAt(i);
//			for(String perm : perms) {
//				if(perm.startsWith("inherit.")) {
//					String inh = perm.substring(8, perm.length());
//					for(String sub : getPermissionEntries(inh)) {
//						if(sub.equals(userperm) || sub.equals("*")) {
//							return true;
//						}
//					}
//				} else if(perm.equals(userperm) || perm.equals("*")) {
//					return true;
//				}
//			}
//		}
//		return beStrict ? false : chat.command.canCommandSenderUseCommand(chat.sender);
//	}
	
	public void addPermission(String perm, EntityPlayer player) {
		NBTTagCompound data = FPlayerData.forPlayer(player.getCommandSenderName()).tag();
		NBTTagList list = data.getTagList(PERMISSION_TAG, NBT.STRING);
		for(int i = 0; i < list.tagCount(); i++) {
			String has = list.getStringTagAt(i);
			if(perm.equals(has)) {
				return;
			}
		}
		list.appendTag(new NBTTagString(perm));
	}
	
	public void removePermission(String perm, EntityPlayer player) {
		NBTTagCompound data = FPlayerData.forPlayer(player.getCommandSenderName()).tag();
		NBTTagList list = data.getTagList(PERMISSION_TAG, NBT.STRING);
		for(int i = 0; i < list.tagCount(); i++) {
			String userperm = list.getStringTagAt(i);
			if(userperm.equals(perm)) {
				list.removeTag(i);
				i--;
			}
		}
	}
	
	private List<String> getPermissionEntries(String string) {
		List<String> ret = new ArrayList<String>();
		for(Object o : tag.func_150296_c()) {
			String perm = (String)o;
			NBTTagList list = tag.getTagList(perm, NBT.STRING);
			for(int i = 0; i < list.tagCount(); i++) {
				String s = list.getStringTagAt(i);
				if(s.equals(string)) {
					ret.add(perm);
				}
			}
		}
		ret = Misc.simplify(ret);
		return ret;
	}
	
	private List<String> getPermissionEntries(ICommand command) {
		List<String> ret = new ArrayList<String>();
		for(Object o : tag.func_150296_c()) {
			String perm = (String)o;
			NBTTagList list = tag.getTagList(perm, NBT.STRING);
			for(int i = 0; i < list.tagCount(); i++) {
				String s = list.getStringTagAt(i);
				if(s.equals(command.getCommandName())) {
					ret.add(perm);
				} else {
					try {
						for(Object a : command.getCommandAliases()) {
							if(s.equals(a)) {
								ret.add("(" + perm + ")");
							}
						}
					} catch(Exception e) {}
				}
			}
		}
		ret = Misc.simplify(ret);
		return ret;
	}

	public String[] perms() {
		List<String> ret = new ArrayList<String>();
		for(Object o : tag.func_150296_c()) {
			ret.add((String)o);
		}
		for(String rn : FRecipes.instance().recipes()) {
			ret.add("recipe." + rn);
		}
		for(String kn : FKits.instance().kits()) {
			ret.add("kit." + kn);
		}
		for(String rn : FRegions.instance().regions()) {
			ret.add("region." + rn);
		}
		ret.addAll(Others.miscPerms);
		ret = Misc.simplify(ret);
		return ret.toArray(new String[ret.size()]);
	}
	
	public String[] perms(EntityPlayer sender) {
		List<String> ret = new ArrayList<String>();
		NBTTagCompound data = FPlayerData.forPlayer(sender.getCommandSenderName()).tag();
		NBTTagList list = data.getTagList(PERMISSION_TAG, NBT.STRING);
		for(int i = 0; i < list.tagCount(); i++) {
			String perm = list.getStringTagAt(i);
			if(perm.startsWith("inherit.")) {
				String inh = perm.substring(8, perm.length());
				for(String sub : perms(inh)) {
					ret.add(sub);
				}
			} else {
				ret.add(perm);
			}
		}
		ret = Misc.simplify(ret);
		return ret.toArray(new String[ret.size()]);
	}
	
	public String[] perms(String perm) {
		List<String> ret = new ArrayList<String>();
		NBTTagList list = tag.getTagList(perm, NBT.STRING);
		for(int i = 0; i < list.tagCount(); i++) {
			perm = list.getStringTagAt(i);
			if(perm.startsWith("inherit.")) {
				String inh = perm.substring(8, perm.length());
				for(String sub : perms(inh)) {
					ret.add(inh + ": " + sub);
				}
			} else {
				ret.add(perm);
			}
		}
		ret = Misc.simplify(ret);
		return ret.toArray(new String[ret.size()]);
	}

	public String[] attribs() {
		List<String> ret = new ArrayList<String>();
		for(Object o: MinecraftServer.getServer().getCommandManager().getCommands().values()) {
			ICommand cmd = (ICommand)o;
			ret.add(cmd.getCommandName());
			try {
				for(Object a : cmd.getCommandAliases()) {
					ret.add("(" + (String)a + ")");
				}
			} catch(Exception e) {}
		}
		for(String rn : FRecipes.instance().recipes()) {
			ret.add("recipe." + rn);
		}
		for(String kn : FKits.instance().kits()) {
			ret.add("kit." + kn);
		}
		for(String rn : FRegions.instance().regions()) {
			ret.add("region." + rn);
		}
		ret.addAll(Others.miscPerms);
		ret = Misc.simplify(ret);
		return ret.toArray(new String[ret.size()]);
	}
	
	public void addGroup(String perm, String att) {
		NBTTagList list = addGroup(perm);
		for(int i = 0; i < list.tagCount(); i++) {
			String p = list.getStringTagAt(i);
			if(p.equals(att)) {
				return;
			}
		}
		list.appendTag(new NBTTagString(att));
		FData.changed();
	}
	
	public NBTTagList addGroup(String perm) {
		NBTTagList list;
		if(tag.hasKey(perm)) {
			list = tag.getTagList(perm, NBT.STRING);
		} else {
			list = new NBTTagList();
			tag.setTag(perm, list);
		}
		FData.changed();
		return list;
	}
	
	public void removeGroup(String perm, String att) {
		if(tag.hasKey(perm)) {
			NBTTagList list = tag.getTagList(perm, NBT.STRING);
			for(int i = 0; i < list.tagCount(); i++) {
				String p = list.getStringTagAt(i);
				if(p.equals(att)) {
					list.removeTag(i);
					FData.changed();
					return;
				}
			}
		}
	}
	
	public void removeGroup(String perm) {
		if(tag.hasKey(perm)) {
			tag.removeTag(perm);
			FData.changed();
		}
	}
	
}
