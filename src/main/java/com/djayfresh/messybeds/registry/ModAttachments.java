package com.djayfresh.messybeds.registry;

import com.djayfresh.messybeds.MessyBeds;
import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MessyBeds.MOD_ID);

    /** Per-chunk flag: the world-gen bed replacement pass has run on this chunk. Saved with the chunk. */
    public static final Supplier<AttachmentType<Boolean>> BEDS_CONVERTED = ATTACHMENTS.register("beds_converted",
            () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL.fieldOf("value")).build());

    private ModAttachments() {}
}
