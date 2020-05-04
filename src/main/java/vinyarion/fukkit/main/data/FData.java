package vinyarion.fukkit.main.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.Level;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.util.collection.VMap;
import vinyarion.fukkit.rpg.archetypes.Faction;

public class FData {
	
	public static interface IFData {
		public NBTTagCompound tag();
	}
	
	public static void changed() {
		changed = true;
	}
	
	private static boolean changed = false;
	
	private static final FData instance = new FData(MinecraftServer.getServer().getFile("fukkit"));
	
	private static final Map<String, String> extensions = new HashMap<String, String>();
	
	static {
		if(!instance.fukkitDir.exists()) {
			instance.fukkitDir.mkdirs();
		}
		extensions.put("txt", "ftf");
		extensions.put("nbt", "fcf");
		extensions.put("gvs", "fgf");
		FMod.log(Level.INFO, "FData initialized!");
	}
	
	public static FData instance() {
		return instance;
	}
	
	private final File fukkitDir;
	private VMap<String, File> map = VMap.newVHashMap();
	private VMap<String, NBTTagCompound> tags = VMap.newVHashMap();
	private VMap<String, Configuration> configs = VMap.newVHashMap();
	
	public FData(File file) {
		fukkitDir = file;
		FMod.log(Level.INFO, "Fukkit data file is at " + file.getAbsolutePath());
	}
	
	public void save() {
		if(changed) {
			changed = false;
			{
				Faction.save();
			}
			long l = System.currentTimeMillis();
			tags.forEachConsumer((name, tag)->writeRawText(file(name), tag.toString()));
			configs.forEachConsumer((name, config)->config.save());
			FMod.log(Level.INFO, "Saved FData! Took " + (System.currentTimeMillis() - l) + " ms.");
		}
	}
	
	public boolean has(String name, String ext) {
		return map.containsKey(name + "." + ext);
	}
	
	public boolean has(String name) {
		return has(name, "nbt");
	}
	
	public File file(String name) {
		return this.file(fukkitDir, name);
	}
	
	public File file(File parent, String name) {
		return file(parent, name, "nbt");
	}
	
	public File file(String name, String ext) {
		return this.file(fukkitDir, name, ext);
	}
	
	public File file(File parent, String name, String ext) {
		if(has(name, ext)) {
			return map.get(name + "." + ext);
		} else {
			File f = new File(parent, name + "." + ext);
			f.getParentFile().mkdirs();
			if(FMod.config.renameFiles) {
				if(extensions.containsKey(ext)) {
					String oldext = extensions.get(ext);
					File oldfile = new File(parent, name + "." + oldext);
					if(oldfile.exists()) {
						if(!oldfile.renameTo(f)) {
							FMod.log(Level.INFO, "FData: Renamed file " + oldfile.getName() + " to " + f.getName());
							return f;
						}
					}
				}
			}
			map.put(name, f);
			return f;
		}
	}
	
	public File dir(String name) {
		File f = new File(fukkitDir, name);
		if(!f.exists()) {
			f.mkdirs();
		}
		return f;
	}
	
	public NBTTagCompound tag(String name) {
		return this.tag(fukkitDir, name);
	}
	
	public NBTTagCompound tag(String name, boolean shouldDefault) {
		return this.tag(fukkitDir, name, shouldDefault);
	}
	
	public NBTTagCompound tag(File parent, String name) {
		return this.tag(parent, name, true);
	}
	
	public NBTTagCompound tag(File parent, String name, boolean shouldDefault) {
		File actual = null;
		try {
			if(has(name)) {
				return tags.get(name);
			} else if((actual = file(parent, name, "nbt")).exists()) {
				try {
					NBTTagCompound tag = (NBTTagCompound)JsonToNBT.func_150315_a(readRawText(actual));
					tags.put(name, tag);
					return tag;
				} catch (NBTException e) {
					e.printStackTrace();
					return null;
				}
			} else if(shouldDefault) {
				NBTTagCompound tag = new NBTTagCompound();
				tags.put(name, tag);
				return tag;
			} else {
				return null;
			}
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Configuration config(String name) {
		return this.config(fukkitDir, name);
	}
	
	public Configuration config(File parent, String name) {
		if(this.configs.containsKey(name)) {
			return this.configs.get(name);
		}
		Configuration ret = new Configuration(new File(parent, name + ".config"));
		ret.load();
		ret.save();
		return ret;
	}
	
	public static void writeRawLines(File file, List<String> lines) {
		Closeable c = null;
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			c = bw;
			for(String line : lines) {
				bw.write(line);
				bw.newLine();
			}
			c.close();
		} catch(IOException e) {
			e.printStackTrace();
			try {
				c.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public static void writeRawText(File file, String text) {
		Closeable c = null;
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			c = bw;
			bw.write(text);
			bw.newLine();
			c.close();
		} catch(IOException e) {
			e.printStackTrace();
			try {
				c.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (NullPointerException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public static List<String> readRawLines(File file) {
		List<String> lines = new ArrayList<String>();
		Closeable c = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			c = br;
			String line = null;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			c.close();
			return lines;
		} catch(IOException e) {
			e.printStackTrace();
			if(c != null) {
				try {
					c.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			return lines;
		}
	}
	
	public static String readRawText(File file) {
		Closeable c = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			c = br;
			String line = null;
			StringBuffer ret = new StringBuffer();
			while ((line = br.readLine()) != null) {
				ret.append(line);
				ret.append("\n");
			}
			c.close();
			return ret.toString();
		} catch(IOException e) {
			e.printStackTrace();
			try {
				c.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return "";
		}
	}
	
	
}
