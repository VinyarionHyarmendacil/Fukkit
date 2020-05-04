package vinyarion.fukkit.main.game.jingle;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S29PacketSoundEffect;

public class FJingle {
	
	public static FJingle fromNBT(NBTTagList list) {
		List<IFNote> notes = new ArrayList<IFNote>();
		for(int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound tag = list.getCompoundTagAt(i);
			if(tag.getBoolean("isChord")) {
				notes.add(FChord.fromNBT(tag.getCompoundTag("chord")));
			} else {
				notes.add(FNote.fromNBT(tag.getCompoundTag("note")));
			}
		}
		return new FJingle(notes.toArray(new IFNote[notes.size()]));
	}
	
	public FJingle(IFNote[] allNotes) {
		this.allNotes = allNotes;
	}
	
	public final IFNote[] allNotes;
	
	public static interface IFNote {
		public FNote[] notes();
		public int dur();
	}
	
	public static class FNote implements IFNote {
		public static FNote fromNBT(NBTTagCompound tag) {
			return new FNote(null, null, 0, 0);
		}
		public FNote(FSound sound, Pitch pitchat, double volume, int tckDur) {
			this.sound = sound;
			this.pitchat = pitchat;
			this.volume = volume;
			this.tckDur = tckDur;
		}
		public FNote[] notes() {
			return new FNote[]{ this };
		}
		public final FSound sound;
		public final Pitch pitchat;
		public final double volume;
		private final int tckDur;
		public int dur() {
			return tckDur;
		}
	}
	
	public static class FChord implements IFNote {
		public static FChord fromNBT(NBTTagCompound tag) {
			return null;
		}
		public FChord(FNote[] notes, int tckDur) {
			this.notes = notes;
			this.tckDur = tckDur;
		}
		private final FNote[] notes;
		private final int tckDur;
		public FNote[] notes() {
			return notes;
		}
		public int dur() {
			return tckDur;
		}
	}
	
}
