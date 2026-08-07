package com.raiiiden.taczblueprints.network;

import com.raiiiden.taczblueprints.attachment.IGunUnlocks;
import com.raiiiden.taczblueprints.attachment.ModAttachmentTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;


import java.util.HashSet;
import java.util.Set;

public record SyncUnlockedGunsPacket(Set<String> unlockedGuns) implements CustomPacketPayload{

    public static final CustomPacketPayload.Type<SyncUnlockedGunsPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath("taczblueprints", "sync_unlocked_guns_packet")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncUnlockedGunsPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(HashSet::new, ByteBufCodecs.STRING_UTF8),
            SyncUnlockedGunsPacket::unlockedGuns,
            SyncUnlockedGunsPacket::new
    );

    public static SyncUnlockedGunsPacket decode(FriendlyByteBuf buf) {
        int size = buf.readInt();
        Set<String> guns = new HashSet<>();
        for (int i = 0; i < size; i++) {
            guns.add(buf.readUtf(32767));
        }
        return new SyncUnlockedGunsPacket(guns);
    }

    public static void handle(SyncUnlockedGunsPacket pkt, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) {
                // TaCZBlueprints.LOGGER.warn("[Blueprint] Received sync packet but player is null!");
                return;
            }
            // Wait for capability to be attached before updating
            IGunUnlocks unlocks = mc.player.getData(ModAttachmentTypes.UNLOCKS);
            unlocks.setUnlockedGuns(pkt.unlockedGuns);
            // TaCZBlueprints.LOGGER.info("[Blueprint] Client synced {} unlocked guns", pkt.unlockedGuns.size());

            // Debug log what was synced
            if (!pkt.unlockedGuns.isEmpty()) {
                // TaCZBlueprints.LOGGER.debug("[Blueprint] Unlocked guns: {}", pkt.unlockedGuns);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}