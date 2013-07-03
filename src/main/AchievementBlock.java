package main;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HttpUtil;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = AchievementBlock.ID, name = AchievementBlock.NAME, version = AchievementBlock.VERSION, useMetadata = true)
@NetworkMod(clientSideRequired = true, channels = { AchievementBlock.ID })
/*
 * , packetHandler = PacketManager.class
 */
public class AchievementBlock
{
	public static final String ID = "achievementblock";
	public static final String NAME = "Achievement Block";
	public static final String VERSION = "1.0.0";

	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), NAME + ".cfg"));

	/**
	 * Directories Definition
	 */
	public static final String RESOURCE_DIRECTORY = "/mods/achievementblock/";
	public static final String LANGUAGE_DIRECTORY = RESOURCE_DIRECTORY + "languages/";
	public static final String TEXTURE_DIRECTORY = RESOURCE_DIRECTORY + "textures/";
	public static final String BLOCK_DIRECTORY = TEXTURE_DIRECTORY + "blocks/";
	public static final String GUI_DIRECTORY = TEXTURE_DIRECTORY + "gui/";

	@Instance(ID)
	public static AchievementBlock instance;
	@Mod.Metadata(ID)
	public static ModMetadata metadata;
	@SidedProxy(clientSide = "main.ClientProxy", serverSide = "main.CommonProxy")
	public static CommonProxy proxy;

	/**
	 * Auto-incrementing configuration IDs. Use this to make sure no config ID is the same.
	 */
	public static final int BLOCK_ID_PREFIX = 452;

	private static int NEXT_BLOCK_ID = BLOCK_ID_PREFIX;

	public static String SERVER_URL = "http://calclavia.com";

	@SuppressWarnings("unchecked")
	public static final HashSet<ItemStack>[] ACCEPTABLE_ITEMS = new HashSet[7];

	static
	{
		for (int i = 0; i < ACCEPTABLE_ITEMS.length; i++)
		{
			ACCEPTABLE_ITEMS[i] = new HashSet<ItemStack>();
		}
	}

	public static int getNextBlockID()
	{
		NEXT_BLOCK_ID++;
		return NEXT_BLOCK_ID;
	}

	public static Block blockAchievement;

	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		NetworkRegistry.instance().registerGuiHandler(this, proxy);

		CONFIGURATION.load();
		blockAchievement = new BlockAchievement(getNextBlockID(), Material.iron);
		GameRegistry.registerBlock(blockAchievement, "blockAchievement");
		GameRegistry.registerTileEntity(TileEntityAchievement.class, "TileEntityAchievement");

		SERVER_URL = CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "HHTP URL", SERVER_URL).getString();

		/**
		 * Slot configurations
		 */
		for (int i = 0; i < 7; i++)
		{
			String listOfString = CONFIGURATION.get(Configuration.CATEGORY_GENERAL, "Slot_" + i + " Allowed_Items", Item.diamond.itemID + ",").getString();

			if (listOfString != null && !listOfString.isEmpty())
			{
				if (!listOfString.contains(","))
				{
					listOfString = listOfString + ",";
				}

				for (String IDString : listOfString.split(","))
				{
					if (IDString != null && !IDString.isEmpty())
					{
						try
						{
							int id = Integer.parseInt(IDString);

							if (id > 0)
							{
								if (Item.itemsList[id] != null)
								{
									ACCEPTABLE_ITEMS[i].add(new ItemStack(Item.itemsList[id]));

								}
								else if (Block.blocksList[id] != null)
								{
									ACCEPTABLE_ITEMS[i].add(new ItemStack(Block.blocksList[id]));
								}
							}
						}
						catch (Exception e)
						{
							FMLLog.severe("Invalid block ID!");
							e.printStackTrace();
						}
					}
				}
			}
		}

		CONFIGURATION.save();
	}

	@Init
	public void load(FMLInitializationEvent evt)
	{
		/**
		 * Load language file(s)
		 */
		FMLLog.fine("Language(s) Loaded: " + TranslationHelper.loadLanguages(LANGUAGE_DIRECTORY, new String[] { "en_US" }));

		/**
		 * Write metadata information
		 */
		metadata.modId = ID;
		metadata.name = NAME;
		metadata.description = "A mod that adds an achievement block!";
		metadata.url = "http://www.calclavia.com/";
		metadata.logoFile = "/mffs_logo.png";
		metadata.version = VERSION;
		metadata.authorList = Arrays.asList(new String[] { "Calclavia" });
		metadata.credits = "Please visit the website.";
		metadata.autogenerated = false;
	}

	public static boolean onAchieve(String username, int x, int y, int z, int itemId, int itemMeta, int stackCount)
	{
		// Used to store the values we will be sending to the target server
		HashMap<String, Object> postValues = new HashMap<String, Object>();
		postValues.put("user", username);
		postValues.put("blockX", Integer.valueOf(x));
		postValues.put("blockY", Integer.valueOf(y));
		postValues.put("blockZ", Integer.valueOf(z));
		postValues.put("itemId", Integer.valueOf(itemId));
		postValues.put("itemMeta", Integer.valueOf(itemMeta));
		postValues.put("stackCount", Integer.valueOf(stackCount));

		try
		{
			/**
			 * Use the ambient logger, send to the url specified, and log it. This might cause a
			 * short amount of lag in game. Multi-thread this if better performance is wanted.
			 */
			HttpUtil.sendPost(null, new URL(SERVER_URL), postValues, false);
			return true;
		}
		catch (Exception e)
		{
			FMLLog.severe("Failed to send data to server");
			e.printStackTrace();
		}

		return false;
	}
}
