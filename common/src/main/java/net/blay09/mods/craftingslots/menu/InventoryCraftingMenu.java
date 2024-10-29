package net.blay09.mods.craftingslots.menu;

import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class InventoryCraftingMenu extends CustomCraftingMenu {

    private static final int RESULT_SLOT = 0;
    private static final int CRAFT_SLOT_START = 1;
    private static final int CRAFT_SLOT_COUNT = 9;
    private static final int CRAFT_SLOT_END = CRAFT_SLOT_START + CRAFT_SLOT_COUNT;

    public static final MenuProvider provider = new BalmMenuProvider<Unit>() {
        @Override
        public Unit getScreenOpeningData(ServerPlayer serverPlayer) {
            return Unit.INSTANCE;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, Unit> getScreenStreamCodec() {
            return StreamCodec.unit(Unit.INSTANCE);
        }

        @Override
        public Component getDisplayName() {
            return Component.translatable("container.craftingslots.inventory_crafting");
        }

        @Override
        public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
            return new InventoryCraftingMenu(windowId, playerInventory);
        }
    };

    private final ResultContainer resultContainer = new ResultContainer();
    private final CraftingContainer craftingContainer;

    public InventoryCraftingMenu(int windowId, Inventory playerInventory) {
        super(ModMenus.inventoryCrafting.get(), windowId, playerInventory);
        craftingContainer = new InventoryCraftingContainer(this, playerInventory);

        addSlot(new ResultSlot(playerInventory.player, craftingContainer, resultContainer, RESULT_SLOT, 193, 38));

        // Crafting Matrix
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                addSlot(new Slot(craftingContainer, j + i * 3, 119 + j * 18, 20 + i * 18));
            }
        }

        // Inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 6; j++) {
                addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 20 + i * 18));
            }
        }

        // Hotbar
        for (int i = 0; i < 9; i++) {
            int x = 8 + i * 18;
            if (i >= 6) {
                x += 3;
            }
            addSlot(new Slot(playerInventory, i, x, 78));
        }

        slotsChanged(craftingContainer);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        final int CRAFTING_GRID_START = 1;
        final int CRAFTING_GRID_END = 10;
        final int CRAFTING_RESULT_SLOT = 0;
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();
            if (index == CRAFTING_RESULT_SLOT) {
                slotStack.getItem().onCraftedBy(slotStack, player.level(), player);
                if (!this.moveItemStackTo(slotStack, CRAFTING_GRID_END, slots.size() - 1, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(slotStack, itemStack);
            } else if (index < CRAFTING_GRID_END) {
                if (!this.moveItemStackTo(slotStack, CRAFTING_GRID_END, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, CRAFTING_GRID_START, CRAFTING_GRID_END, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
            if (index == CRAFTING_RESULT_SLOT) {
                player.drop(slotStack, false);
            }
        }
        return itemStack;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack itemStack, Slot slot) {
        return slot.container != resultContainer && super.canTakeItemForPickAll(itemStack, slot);
    }

    @Override
    public Slot getResultSlot() {
        //noinspection SequencedCollectionMethodCanBeUsed
        return slots.get(RESULT_SLOT);
    }

    @Override
    public List<Slot> getInputGridSlots() {
        return slots.subList(CRAFT_SLOT_START, CRAFT_SLOT_END);
    }

    @Override
    public CraftingContainer getCraftingContainer() {
        return craftingContainer;
    }

    @Override
    protected ResultContainer getResultContainer() {
        return resultContainer;
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedItemContents stackedItemContents) {
        craftingContainer.fillStackedContents(stackedItemContents);
    }
}
