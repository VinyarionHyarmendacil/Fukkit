package vinyarion.fukkit.main.game.jingle;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import vinyarion.fukkit.main.game.jingle.FJingle.FNote;
import vinyarion.fukkit.main.game.jingle.FJingle.IFNote;

public class FMusic {
	
	public FMusic(FJingle jingle, List<EntityPlayerMP> players) {
		this.jingle = jingle;
		this.players = players;
	}
	
	public final FJingle jingle;
	public List<EntityPlayerMP> players;

	private int index = 0;
	private int ticksSinceLast = 0;
	private int ticksTotal = 0;
	
	public void tick() {
		IFNote chord = jingle.allNotes[index];
		ticksTotal++;
		int dur = chord.dur();
		if(ticksTotal - dur >= ticksSinceLast) {
			ticksSinceLast += dur;
			index++;
		}
		for(FNote note : chord.notes()) {
			for(EntityPlayerMP player : players) {
				player.playerNetServerHandler.sendPacket(new S29PacketSoundEffect(note.sound.name, player.posX, player.posY, player.posZ, (float)note.volume, (float)note.pitchat.diff(note.sound.absolutePitch)));
			}
		}
	}
	
}
