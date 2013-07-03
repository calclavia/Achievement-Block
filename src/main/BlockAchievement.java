package main;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockAchievement extends BlockContainer
{
	public BlockAchievement(int id, Material par2Material)
	{
		super(id, par2Material);
		this.setUnlocalizedName(AchievementBlock.ID + ":achievement");
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
	{
		if (entityPlayer.isSneaking())
		{
			TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

			if (tileEntity instanceof TileEntityAchievement)
			{
				if (((TileEntityAchievement) tileEntity).tryAchieve(entityPlayer.username))
				{
					return true;
				}
				else
				{
					if (!world.isRemote)
					{
						entityPlayer.sendChatToPlayer("Cannot achieve yet!");
					}
					return false;
				}
			}
		}
		entityPlayer.openGui(AchievementBlock.instance, 0, world, x, y, z);
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TileEntityAchievement();
	}
}
