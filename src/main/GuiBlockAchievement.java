package main;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

public class GuiBlockAchievement extends GuiContainer
{
	private TileEntityAchievement tileEntity;

	private int containerWidth;
	private int containerHeight;

	public GuiBlockAchievement(InventoryPlayer par1InventoryPlayer, TileEntityAchievement tileEntity)
	{
		super(new ContainerAchievement(par1InventoryPlayer, tileEntity));
		this.tileEntity = tileEntity;
		this.ySize = 230;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	public void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		this.fontRenderer.drawString(this.tileEntity.getInvName(), 65 - this.tileEntity.getInvName().length(), 5, 4210752);
		this.fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, this.ySize - 96, 4210752);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		this.mc.renderEngine.bindTexture(AchievementBlock.GUI_DIRECTORY + "gui_achievement.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		this.containerWidth = (this.width - this.xSize) / 2;
		this.containerHeight = (this.height - this.ySize) / 2;

		this.drawTexturedModalRect(this.containerWidth, this.containerHeight, 0, 0, this.xSize, this.ySize);
	}
}