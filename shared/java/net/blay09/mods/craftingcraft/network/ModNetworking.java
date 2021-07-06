package net.blay09.mods.craftingcraft.network;

import net.blay09.mods.craftingcraft.CraftingCraft;
import net.blay09.mods.forbic.network.ForbicNetworking;
import net.minecraft.resources.ResourceLocation;

public class ModNetworking {

    public static void initialize() {
        ForbicNetworking.registerServerboundPacket(id("portable_crafting"), PortableCraftingMessage.class, PortableCraftingMessage::encode, PortableCraftingMessage::decode, PortableCraftingMessage::handle);
    }

    private static ResourceLocation id(String known_waystones) {
        return new ResourceLocation(CraftingCraft.MOD_ID, known_waystones);
    }
}
