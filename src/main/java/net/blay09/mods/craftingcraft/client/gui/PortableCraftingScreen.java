package net.blay09.mods.craftingcraft.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.craftingcraft.container.PortableCraftingContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class PortableCraftingScreen extends ContainerScreen<PortableCraftingContainer> {

    private static final ResourceLocation texture = new ResourceLocation("textures/gui/container/crafting_table.png");

    public PortableCraftingScreen(PortableCraftingContainer container, PlayerInventory playerInventory, ITextComponent displayName) {
        super(container, playerInventory, displayName);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        func_230459_a_(matrixStack, mouseX, mouseY); // renderHoveredTooltip
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        Minecraft.getInstance().getTextureManager().bindTexture(texture);
        blit(matrixStack, guiLeft, guiTop, 0, 0, xSize, ySize);
    }

}
