package vinyarion.fukkit.sm.blocklog;

import java.sql.ResultSet;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.FNetHandlerPlayServer;
import vinyarion.fukkit.main.FNetHandlerPlayServer.FNetData;
import vinyarion.fukkit.main.cmd.FCommand;
import vinyarion.lotrclientutil.common.packets.PacketBlockLogClient;
import vinyarion.lotrclientutil.common.packets.PacketBlockLogClient.Blyat;

public class BlockLogResponse {

	private final List<Blyat> blyats = Lists.newArrayList();

	public BlockLogResponse(ResultSet results) throws Exception {
		if(!results.first()) return;
		int col_id = results.findColumn(FBlockLog.log_id);
		int col_time = results.findColumn(FBlockLog.log_time);
		int col_x = results.findColumn(FBlockLog.log_x);
		int col_y = results.findColumn(FBlockLog.log_y);
		int col_z = results.findColumn(FBlockLog.log_z);
		int col_block = results.findColumn(FBlockLog.log_block);
		int col_meta = results.findColumn(FBlockLog.log_meta);
		int col_broke = results.findColumn(FBlockLog.log_broke);
		int col_player = results.findColumn(FBlockLog.log_player);
		for(; !results.isLast(); results.next()) {
			Blyat row = new Blyat(
				results.getInt(col_id), 
				results.getLong(col_time), 
				results.getInt(col_x), 
				results.getInt(col_y), 
				results.getInt(col_z), 
				results.getString(col_block), 
				results.getInt(col_meta), 
				results.getBoolean(col_broke), 
				results.getString(col_player)
			);
			blyats.add(row);
		}
	}

	public void sendTo(ICommandSender sender) {
		if(sender instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP)sender;
			FNetData data = ((FNetHandlerPlayServer)player.playerNetServerHandler).data();
			if(data.tirathurin) {
				PacketBlockLogClient packet = new PacketBlockLogClient();
				packet.blyats.addAll(this.blyats);
				FMod.tirathurinNet.sendTo(packet, player);
			} else if(data.notifyNeedsTirathurin) {
				data.notifyNeedsTirathurin = false;
				FMod.later.queue(()->FCommand.Info(sender, "See if you can get ahold of the Tírathurin mod, which will make it easier to visualize block logs."));
			}
		} else {
			FMod.later.queue(()->FCommand.Info(sender, "This doesn't really do anything unless you are a player."));
		}
	}

}
