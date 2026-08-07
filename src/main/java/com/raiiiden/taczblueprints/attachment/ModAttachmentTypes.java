package com.raiiiden.taczblueprints.attachment;

import com.raiiiden.taczblueprints.TaCZBlueprints;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachmentTypes {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, TaCZBlueprints.MODID);

    public static final Supplier<AttachmentType<GunUnlocks>> UNLOCKS = ATTACHMENT_TYPES.register(
            "gun_unlocks",
            () -> AttachmentType.serializable(GunUnlocks::new)
                    .copyOnDeath()
                    .build()
    );



}
