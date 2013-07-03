package main;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerAchievement extends Container
{
	private TileEntityAchievement tileEntity;

	public ContainerAchievement(InventoryPlayer par1InventoryPlayer, TileEntityAchievement tileEntity)
	{
		this.tileEntity = tileEntity;
		// Sides
		this.addSlotToContainer(new Slot(tileEntity, 0, 80, 15));
		this.addSlotToContainer(new Slot(tileEntity, 1, 27, 46));
		this.addSlotToContainer(new Slot(tileEntity, 2, 133, 46));
		this.addSlotToContainer(new Slot(tileEntity, 3, 27, 90));
		this.addSlotToContainer(new Slot(tileEntity, 4, 133, 90));
		this.addSlotToContainer(new Slot(tileEntity, 5, 80, 121));
		// Center
		this.addSlotToContainer(new Slot(tileEntity, 6, 80, 68));

		int var3;

		for (var3 = 0; var3 < 3; ++var3)
		{
			for (int var4 = 0; var4 < 9; ++var4)
			{
				this.addSlotToContainer(new Slot(par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 144 + var3 * 18));
			}
		}

		for (var3 = 0; var3 < 9; ++var3)
		{
			this.addSlotToContainer(new Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 202));
		}

		this.tileEntity.users.add(par1InventoryPlayer.player);
		tileEntity.openChest();
	}

	@Override
	public void onCraftGuiClosed(EntityPlayer entityplayer)
	{
		super.onCraftGuiClosed(entityplayer);
		this.tileEntity.users.remove(entityplayer);
	}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer)
	{
		return this.tileEntity.isUseableByPlayer(par1EntityPlayer);
	}

	/**
	 * Called to transfer a stack from one inventory to the other eg. when shift clicking.
	 */
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par1)
	{
		ItemStack var2 = null;
		Slot var3 = (Slot) this.inventorySlots.get(par1);

		if (var3 != null && var3.getHasStack())
		{
			ItemStack itemStack = var3.getStack();
			var2 = itemStack.copy();

			if (par1 > 6)
			{
				for (int i = 0; i < 6; i++)
				{
					if (this.tileEntity.isStackValidForSlot(i, itemStack))
					{
						if (this.mergeItemStack(itemStack, i, i + i, false))
						{
							break;
						}
					}
				}
			}
			else if (!this.mergeItemStack(itemStack, 7, 36 + 7, false))
			{
				return null;
			}

			if (itemStack.stackSize == 0)
			{
				var3.putStack((ItemStack) null);
			}
			else
			{
				var3.onSlotChanged();
			}

			if (itemStack.stackSize == var2.stackSize)
			{
				return null;
			}

			var3.onPickupFromSlot(par1EntityPlayer, itemStack);
		}

		return var2;
	}
}
