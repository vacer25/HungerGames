package tk.shanebee.hg.util;

import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.material.Attachable;
import org.bukkit.material.MaterialData;
import tk.shanebee.hg.HG;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Generalized utility class for shortcut methods
 */
@SuppressWarnings("WeakerAccess")
public class Util {

	public static final BlockFace[] faces = new BlockFace[]{BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};

	/**
	 * Log a message to console prefixed with the plugin's name
	 *
	 * @param s Message to log to console
	 */
	public static void log(String s) {
		scm(Bukkit.getConsoleSender(), "&7[&3&lHungerGames&7] " + s);
	}

	/**
	 * Send a warning to console prefixed with the plugin's name
	 *
	 * @param s Message to log to console
	 */
	public static void warning(String s) {
		scm(Bukkit.getConsoleSender(), "&7[&e&lHungerGames&7] &eWARNING: " + s);
	}

	/**
	 * Send a colored message to a player or console
	 *
	 * @param sender Receiver of message
	 * @param s      Message to send
	 */
	public static void scm(CommandSender sender, String s) {
		sender.sendMessage(getColString(s) + ChatColor.RESET);
	}

	/**
	 * Broadcast a message prefixed with plugin name
	 *
	 * @param s Message to send
	 */
	public static void broadcast(String s) {
		Bukkit.broadcastMessage(getColString(HG.getPlugin().getLang().prefix + " " + s));
	}

	/**
	 * Shortcut for adding color to a string
	 *
	 * @param string String including color codes
	 * @return Formatted string
	 */
	public static String getColString(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	/**
	 * Check if a string is an Integer
	 *
	 * @param string String to get
	 * @return True if string is an Integer
	 */
	public static boolean isInt(String string) {
		try {
			Integer.parseInt(string);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static BlockFace getSignFace(BlockFace face) {
		switch (face) {
			case WEST:
				return BlockFace.SOUTH;
			case SOUTH:
				return BlockFace.EAST;
			case EAST:
				return BlockFace.NORTH;
			default:
				return BlockFace.WEST;
		}
	}

	/**
	 * Clear the inventory of a player including equipment
	 *
	 * @param player Player to clear inventory
	 */
	public static void clearInv(Player player) {
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		player.updateInventory();
	}

	/**
	 * Convert a list of UUIDs to a string of player names
	 *
	 * @param uuid UUID list to convert
	 * @return String of player names
	 */
	public static List<String> convertUUIDListToStringList(List<UUID> uuid) {
		List<String> winners = new ArrayList<>();
		for (UUID id : uuid) {
			winners.add(Objects.requireNonNull(Bukkit.getPlayer(id)).getName());
		}
		return winners;
	}

	public static String translateStop(List<String> win) {
		StringBuilder bc = null;
		int count = 0;
		for (String s : win) {
			count++;
			if (count == 1) bc = new StringBuilder(s);
			else if (count == win.size()) {
				assert bc != null;
				bc.append(", and ").append(s);
			} else {
				assert bc != null;
				bc.append(", ").append(s);
			}
		}
		if (bc != null)
			return bc.toString();
		else
			return "No one";
	}

	public static void shootFirework(Location l) {
		assert l.getWorld() != null;
		Firework fw = l.getWorld().spawn(l, Firework.class);
		FireworkMeta fm = fw.getFireworkMeta();
		List<Color> c = new ArrayList<>();
		c.add(Color.GREEN);
		c.add(Color.BLUE);
		FireworkEffect e = FireworkEffect.builder().flicker(true).withColor(c).withFade(c).with(Type.BALL_LARGE).trail(true).build();
		fm.addEffect(e);
		fm.setPower(2);
		fw.setFireworkMeta(fm);
	}

	@SuppressWarnings("deprecation")
	public static boolean isAttached(Block base, Block attached) {
		if (attached.getType() == Material.AIR) return false;

		MaterialData bs = attached.getState().getData();
		//BlockData bs = attached.getBlockData();

		if (!(bs instanceof Attachable)) return false;

		Attachable at = (Attachable) bs;
		BlockFace face = at.getAttachedFace();

		return attached.getRelative(face).equals(base);
	}

	/**
	 * Check if server is running a minimum Minecraft version
	 *
	 * @param major Major version to check (Most likely just going to be 1)
	 * @param minor Minor version to check
	 * @return True if running this version or higher
	 */
	public static boolean isRunningMinecraft(int major, int minor) {
		return isRunningMinecraft(major, minor, 0);
	}

	/**
	 * Check if server is running a minimum Minecraft version
	 *
	 * @param major    Major version to check (Most likely just going to be 1)
	 * @param minor    Minor version to check
	 * @param revision Revision to check
	 * @return True if running this version or higher
	 */
	public static boolean isRunningMinecraft(int major, int minor, int revision) {
		String[] version = Bukkit.getServer().getBukkitVersion().split("-")[0].split("\\.");
		int maj = Integer.parseInt(version[0]);
		int min = Integer.parseInt(version[1]);
		int rev;
		try {
			rev = Integer.parseInt(version[2]);
		} catch (Exception ignore) {
			rev = 0;
		}
		return maj > major || min > minor || (min == minor && rev >= revision);
	}

	/**
	 * Check if a material is a wall sign
	 * <p>Due to sign material changes in 1.14 this method checks for both 1.13 and 1.14+</p>
	 *
	 * @param item Material to check
	 * @return True if material is a wall sign
	 */
	public static boolean isWallSign(Material item) {
		if (isRunningMinecraft(1, 14)) {
			switch (item) {
				case ACACIA_WALL_SIGN:
				case BIRCH_WALL_SIGN:
				case DARK_OAK_WALL_SIGN:
				case JUNGLE_WALL_SIGN:
				case OAK_WALL_SIGN:
				case SPRUCE_WALL_SIGN:
					return true;
			}
		} else {
			return item == Material.getMaterial("WALL_SIGN");
		}
		return false;
	}

	/**
	 * Check if a method exists
	 *
	 * @param c              Class that contains this method
	 * @param methodName     Method to check
	 * @param parameterTypes Parameter types if the method contains any
	 * @return True if this method exists
	 */
	public static boolean methodExists(final Class<?> c, final String methodName, final Class<?>... parameterTypes) {
		try {
			c.getDeclaredMethod(methodName, parameterTypes);
			return true;
		} catch (final NoSuchMethodException | SecurityException e) {
			return false;
		}
	}

	/**
	 * Check if a class exists
	 *
	 * @param className Class to check for existence
	 * @return True if this class exists
	 */
	public static boolean classExists(final String className) {
		try {
			Class.forName(className);
			return true;
		} catch (final ClassNotFoundException e) {
			return false;
		}
	}

}
