package vinyarion.fukkit.sm.blocklog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.Level;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import vinyarion.fukkit.main.FMod;
import vinyarion.fukkit.main.data.FData;
import vinyarion.fukkit.main.util.Colors;
import vinyarion.fukkit.main.util.StreamTaskThread;
import vinyarion.fukkit.main.util.StreamTaskThreadPool;
import vinyarion.fukkit.main.util.Submittable;
import vinyarion.fukkit.main.util.Submittable.HandledRunnable;

public class FBlockLog {

	public static final FBlockLog INSTANCE = new FBlockLog();

	private FBlockLog() {}

	public static final String database_url = "DatabaseURL";
	public static final String database_username = "DatabaseUsername";
	public static final String database_password = "DatabasePassword";

	private static final NBTTagCompound settings = FData.instance().tag("sql_database_info");
	private Connection connection = null;
	private static final Submittable<Runnable, HandledRunnable> executor = new StreamTaskThread();

	public static final String log_id = "id";
	public static final String log_time = "time";
	public static final String log_x = "x";
	public static final String log_y = "y";
	public static final String log_z = "z";
	public static final String log_block = "block";
	public static final String log_meta = "meta";
	public static final String log_broke = "broke";
	public static final String log_player = "player";

	public void init() {
		executor.submit(this::reconnect);
		executor.submitAlt(()->{
			if(connection == null) return;
			PreparedStatement tableBlocklog = connection.prepareStatement("CREATE TABLE IF NOT EXISTS blocklog(id int NOT NULL AUTO_INCREMENT, time bigint NOT NULL, timelsd int NOT NULL, x int NOT NULL, y int NOT NULL, z int NOT NULL, block varchar(63) NOT NULL, meta int NOT NULL, broke bit NOT NULL, player varchar(16) NOT NULL, PRIMARY KEY(id));");
			tableBlocklog.executeUpdate();
		});
	}

	public boolean isOperable() {
		return connection != null;
	}

	/**@param id (auto)
	 * @param time long
	 * @param x int
	 * @param y int
	 * @param z int
	 * @param block varchar(255)
	 * @param meta int
	 * @param broke boolean
	 * @param player varchar(255)
	 */
	private static final Void Void = null;

	public void post(int x, int y, int z, Block block, int meta, boolean broke, EntityPlayerMP player) {
		executor.submitAlt(()->{
			if(connection == null) return;
			PreparedStatement add = connection.prepareStatement(
				String.format("INSERT INTO blocklog VALUES (%s, %s, %s, %s, '%s', %s, %s, '%s');", 
					String.valueOf(System.currentTimeMillis()), 
					String.valueOf(x), 
					String.valueOf(y), 
					String.valueOf(z), 
					block.getUnlocalizedName(), 
					String.valueOf(meta), 
					String.valueOf(broke ? 1 : 0), 
					player.getCommandSenderName()
				));
			add.executeUpdate();
		});
	}

	public void search(BlockLogQuerey querey, ICommandSender sender) {
		executor.submitAlt(()->{
			if(connection == null) {sender.addChatMessage(Colors.CHAT("No database connection!"));return;}
			PreparedStatement ask = connection.prepareStatement(querey.toStatement());
			ask.execute();
			ResultSet results = ask.getResultSet();
			BlockLogResponse response = new BlockLogResponse(results);
			response.sendTo(sender);
		});
	}

	public void setConnectionData(String url, String username, String password) {
		settings.setString(database_url, url);
		settings.setString(database_username, username);
		settings.setString(database_password, database_password);
	}

	public void reconnect() {
		connection = getConnection();
	}

	private Connection getConnection() {
		String driver = "com.mysql.jdbc.Driver";
		String url = settings.getString(database_url);
		String username = settings.getString(database_username);
		String password = settings.getString(database_password);
		if(url.length() == 0 || username.length() == 0 || password.length() == 0) {
			FMod.log(Level.ERROR, "SQL Database arguments not set!");
			return null;
		}
		try {
			Class.forName(driver);
			Connection ret = DriverManager.getConnection(url, username, password);
			FMod.log(Level.INFO, "Connected to the SQL Database!");
			return ret;
		} catch(Exception e) {
			FMod.log(Level.ERROR, "Error connecting to SQL Database!");
			e.printStackTrace();
		}
		return null;
	}

}
