package br.com.weavenmc.ypvp.jnbt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import br.com.weavenmc.commons.bukkit.worldedit.AsyncWorldEdit;

public class Schematic {
	private short[] blocks;
	private byte[] data;
	private short width;
	private short lenght;
	private short height;
	private static Schematic instance;
	private static AsyncWorldEdit asyncWorldEdit;

	static {
		Schematic.instance = new Schematic();
		Schematic.asyncWorldEdit = AsyncWorldEdit.getInstance();
	}

	public static Schematic getInstance() {
		return Schematic.instance;
	}

	private Schematic() {
	}

	private Schematic(final short[] blocks2, final byte[] data, final short width, final short lenght,
			final short height) {
		this.blocks = blocks2;
		this.data = data;
		this.width = width;
		this.lenght = lenght;
		this.height = height;
	}

	public short[] getBlocks() {
		return this.blocks;
	}

	public byte[] getData() {
		return this.data;
	}

	public short getWidth() {
		return this.width;
	}

	public short getLenght() {
		return this.lenght;
	}

	public short getHeight() {
		return this.height;
	}

	public static List<GladiatorBlock> spawnGladiator(final Player gladiator, final Player target, final Location loc,
			final Schematic schematic) {
		final List<GladiatorBlock> list = new ArrayList<GladiatorBlock>();
		final short[] blocks = schematic.getBlocks();
		final byte[] blockData = schematic.getData();
		final short length = schematic.getLenght();
		final short width = schematic.getWidth();
		final short height = schematic.getHeight();
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				for (int z = 0; z < length; ++z) {
					final int index = y * width * length + z * width + x;
					final Block b = new Location(loc.getWorld(), x + loc.getX(), y + loc.getY(), z + loc.getZ())
							.getBlock();
					Schematic.asyncWorldEdit.setAsyncBlock(loc.getWorld(), b.getLocation().getBlockX(),
							b.getLocation().getBlockY(), b.getLocation().getBlockZ(), (int) blocks[index],
							blockData[index]);
				}
			}
		}
		gladiator.teleport(new Location(loc.getWorld(), (double) (loc.getBlockX() + 4), (double) (loc.getBlockY() + 2),
				(double) (loc.getBlockZ() + 4)));
		return list;
	}

	public static void spawn(final World world, final Location loc, final Schematic schematic) {
		final short[] blocks = schematic.getBlocks();
		final byte[] blockData = schematic.getData();
		final short length = schematic.getLenght();
		final short width = schematic.getWidth();
		final short height = schematic.getHeight();
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				for (int z = 0; z < length; ++z) {
					final int index = y * width * length + z * width + x;
					final Block block = new Location(world, x + loc.getX(), y + loc.getY(), z + loc.getZ()).getBlock();
					Schematic.asyncWorldEdit.setAsyncBlock(world, block.getX(), block.getY(), block.getZ(),
							(int) blocks[index], blockData[index]);
				}
			}
		}
	}

	public Schematic loadSchematic(final File file) throws IOException, DataException {
		final FileInputStream stream = new FileInputStream(file);
		final NBTInputStream nbtStream = new NBTInputStream(stream);
		final CompoundTag schematicTag = (CompoundTag) nbtStream.readTag();
		nbtStream.close();
		if (!schematicTag.getName().equalsIgnoreCase("Schematic")) {
			throw new IllegalArgumentException("Tag \"Schematic\" does not exist or is not first");
		}
		final Map<String, Tag> schematic;
		if (!(schematic = schematicTag.getValue()).containsKey("Blocks")) {
			throw new IllegalArgumentException("Schematic file is missing a \"Blocks\" tag");
		}
		final short width = ((ShortTag) this.getChildTag(schematic, "Width", ShortTag.class)).getValue();
		final short length = ((ShortTag) this.getChildTag(schematic, "Length", ShortTag.class)).getValue();
		final short height = ((ShortTag) this.getChildTag(schematic, "Height", ShortTag.class)).getValue();
		final byte[] blockId = ((ByteArrayTag) this.getChildTag(schematic, "Blocks", ByteArrayTag.class)).getValue();
		final byte[] blockData = ((ByteArrayTag) this.getChildTag(schematic, "Data", ByteArrayTag.class)).getValue();
		byte[] addId = new byte[0];
		final short[] blocks = new short[blockId.length];
		if (schematic.containsKey("AddBlocks")) {
			addId = ((ByteArrayTag) this.getChildTag(schematic, "AddBlocks", ByteArrayTag.class)).getValue();
		}
		for (int index = 0; index < blockId.length; ++index) {
			if (index >> 1 >= addId.length) {
				blocks[index] = (short) (blockId[index] & 0xFF);
			} else if ((index & 0x1) == 0x0) {
				blocks[index] = (short) (((addId[index >> 1] & 0xF) << 8) + (blockId[index] & 0xFF));
			} else {
				blocks[index] = (short) (((addId[index >> 1] & 0xF0) << 4) + (blockId[index] & 0xFF));
			}
		}
		return new Schematic(blocks, blockData, width, length, height);
	}

	private <T extends Tag> Tag getChildTag(final Map<String, Tag> items, final String key, final Class<T> expected)
			throws DataException {
		if (!items.containsKey(key)) {
			throw new DataException("Schematic file is missing a \"" + key + "\" tag");
		}
		final Tag tag = items.get(key);
		if (!expected.isInstance(tag)) {
			throw new DataException(String.valueOf(key) + " tag is not of tag type " + expected.getName());
		}
		return expected.cast(tag);
	}

	public static class GladiatorBlock {
		private int x;
		private int y;
		private int z;
		private int id;
		private byte data;

		public int getX() {
			return this.x;
		}

		public int getY() {
			return this.y;
		}

		public int getZ() {
			return this.z;
		}

		public int getId() {
			return this.id;
		}

		public byte getData() {
			return this.data;
		}

		public GladiatorBlock(final int x, final int y, final int z, final int id, final byte data) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.id = id;
			this.data = data;
		}
	}
}
