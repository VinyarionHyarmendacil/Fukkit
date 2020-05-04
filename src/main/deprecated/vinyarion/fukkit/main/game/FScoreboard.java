package vinyarion.fukkit.main.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreDummyCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;

@Deprecated
public class FScoreboard extends Scoreboard {
	
//	public static void initFor(EntityPlayerMP player) {
	//		
	//	}
	//	
	//	public static void upDatefor(EntityPlayerMP player) {
	//		player.playerNetServerHandler.sendPacket(new S3DPacketDisplayScoreboard());
	//	}
	
	private static class FObjective implements IScoreObjectiveCriteria {
		public String func_96636_a() {
			return null;
		}
		public int func_96635_a(List list) {
			return 0;
		}
		public boolean isReadOnly() {
			return false;
		}
	}
	
	private class MutableScore extends Score {
		private String display = "";
		public MutableScore() {
			super(FScoreboard.this, FScoreboard.this.obj, "");
		}
	    public String getPlayerName() {
	        return this.display;
	    }
		public void setDisplay(String string) {
			this.display = string;
		}
	}
	
	public static final int max = 16;
	
	public int localMax = 0;
	private ScoreObjective obj;
	private MutableScore[] scores = new MutableScore[max];
	private MutableScore[] scores_client = new MutableScore[max];
	private EntityPlayerMP player;

	private Set scoreset = new HashSet();
	private MinecraftServer server = MinecraftServer.getServer();
	
	public FScoreboard(EntityPlayerMP player, String name) {
		this.player = player;
		this.obj = new ScoreObjective(this, name, new FObjective());
		for(int i = 0; i < max; i++) {
			MutableScore s = new MutableScore();
			this.scores[i] = s;
			s.setScorePoints(i);
		}
		this.scores_client = scores.clone();
		player.playerNetServerHandler.sendPacket(new S3BPacketScoreboardObjective(obj, 0));
		player.playerNetServerHandler.sendPacket(new S3DPacketDisplayScoreboard(0, this.obj));
		player.playerNetServerHandler.sendPacket(new S3DPacketDisplayScoreboard(1, this.obj));
	}
	
	public void destroy() {
		player.playerNetServerHandler.sendPacket(new S3BPacketScoreboardObjective(obj, 1));
	}
	
	public void setLine(int line, String string) {
		scores[line].setDisplay(string);
		localMax = Math.max(localMax, line);
	}
	
	public void display() {
		for(MutableScore score : this.scores_client) {
			this.player.playerNetServerHandler.sendPacket(new S3CPacketUpdateScore(score, 1));
		}
		this.scores_client = this.scores.clone();
		for(MutableScore score : this.scores_client) {
			this.player.playerNetServerHandler.sendPacket(new S3CPacketUpdateScore(score, 1));
		}
	}
	
	public void func_96536_a(Score score) {
        super.func_96536_a(score);
        if (this.scoreset.contains(score.func_96645_d()))  {
            this.player.playerNetServerHandler.sendPacket(new S3CPacketUpdateScore(score, 0));
        }
    }
	
    public void func_96516_a(String name) {
        super.func_96516_a(name);
        this.player.playerNetServerHandler.sendPacket(new S3CPacketUpdateScore(name));
    }
    
    public void func_96530_a(int idx, ScoreObjective objective) {
        ScoreObjective scoreobjective1 = this.func_96539_a(idx);
        super.func_96530_a(idx, objective);
        if (scoreobjective1 != objective && scoreobjective1 != null) {
            if (this.func_96552_h(scoreobjective1) > 0) {
                this.player.playerNetServerHandler.sendPacket(new S3DPacketDisplayScoreboard(idx, objective));
            } else {
                this.func_96546_g(scoreobjective1);
            }
        }
        if (objective != null) {
            if (this.scoreset.contains(objective)) {
                this.player.playerNetServerHandler.sendPacket(new S3DPacketDisplayScoreboard(idx, objective));
            } else {
                this.func_96549_e(objective);
            }
        }
    }
    
    public boolean func_151392_a(String p_151392_1_, String p_151392_2_) {
        if (super.func_151392_a(p_151392_1_, p_151392_2_)) {
            ScorePlayerTeam scoreplayerteam = this.getTeam(p_151392_2_);
            this.player.playerNetServerHandler.sendPacket(new S3EPacketTeams(scoreplayerteam, Arrays.asList(new String[] {p_151392_1_}), 3));
            return true;
        } else {
            return false;
        }
    }
    
    public void removePlayerFromTeam(String p_96512_1_, ScorePlayerTeam p_96512_2_) {
        super.removePlayerFromTeam(p_96512_1_, p_96512_2_);
        this.player.playerNetServerHandler.sendPacket(new S3EPacketTeams(p_96512_2_, Arrays.asList(new String[] {p_96512_1_}), 4));
    }
    
    public void func_96522_a(ScoreObjective p_96522_1_) {
        super.func_96522_a(p_96522_1_);
    }
    
    public void func_96532_b(ScoreObjective p_96532_1_) {
        super.func_96532_b(p_96532_1_);
        if (this.scoreset.contains(p_96532_1_)) {
            this.player.playerNetServerHandler.sendPacket(new S3BPacketScoreboardObjective(p_96532_1_, 2));
        }
    }
    
    public void func_96533_c(ScoreObjective p_96533_1_) {
        super.func_96533_c(p_96533_1_);
        if (this.scoreset.contains(p_96533_1_)) {
            this.func_96546_g(p_96533_1_);
        }
    }
    
    public void broadcastTeamCreated(ScorePlayerTeam p_96523_1_) {
        super.broadcastTeamCreated(p_96523_1_);
        this.player.playerNetServerHandler.sendPacket(new S3EPacketTeams(p_96523_1_, 0));
    }
    
    public void broadcastTeamRemoved(ScorePlayerTeam p_96538_1_) {
        super.broadcastTeamRemoved(p_96538_1_);
        this.player.playerNetServerHandler.sendPacket(new S3EPacketTeams(p_96538_1_, 2));
    }
    
    public void func_96513_c(ScorePlayerTeam p_96513_1_) {
        super.func_96513_c(p_96513_1_);
        this.player.playerNetServerHandler.sendPacket(new S3EPacketTeams(p_96513_1_, 1));
    }
    
    public List func_96550_d(ScoreObjective p_96550_1_) {
        ArrayList arraylist = new ArrayList();
        arraylist.add(new S3BPacketScoreboardObjective(p_96550_1_, 0));
        for (int i = 0; i < 3; ++i) {
            if (this.func_96539_a(i) == p_96550_1_) {
                arraylist.add(new S3DPacketDisplayScoreboard(i, p_96550_1_));
            }
        }
        Iterator iterator = this.func_96534_i(p_96550_1_).iterator();
        while (iterator.hasNext()) {
            Score score = (Score)iterator.next();
            arraylist.add(new S3CPacketUpdateScore(score, 0));
        }
        return arraylist;
    }
    
    public void func_96549_e(ScoreObjective p_96549_1_) {
        List list = this.func_96550_d(p_96549_1_);
        Iterator iterator1 = list.iterator();
        while (iterator1.hasNext()) {
            Packet packet = (Packet)iterator1.next();
            this.player.playerNetServerHandler.sendPacket(packet);
        }
        this.scoreset.add(p_96549_1_);
    }
    
    public List func_96548_f(ScoreObjective p_96548_1_) {
        ArrayList arraylist = new ArrayList();
        arraylist.add(new S3BPacketScoreboardObjective(p_96548_1_, 1));
        for (int i = 0; i < 3; ++i) {
            if (this.func_96539_a(i) == p_96548_1_) {
                arraylist.add(new S3DPacketDisplayScoreboard(i, p_96548_1_));
            }
        }
        return arraylist;
    }
    
    public void func_96546_g(ScoreObjective p_96546_1_)  {
        List list = this.func_96548_f(p_96546_1_);
        Iterator iterator1 = list.iterator();
        while (iterator1.hasNext()) {
            Packet packet = (Packet)iterator1.next();
            this.player.playerNetServerHandler.sendPacket(packet);
        }
        this.scoreset.remove(p_96546_1_);
    }
    
    public int func_96552_h(ScoreObjective p_96552_1_) {
        int i = 0;
        for (int j = 0; j < 3; ++j) {
            if (this.func_96539_a(j) == p_96552_1_) {
                ++i;
            }
        }
        return i;
    }
	
}
