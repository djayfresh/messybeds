package com.djayfresh.messybeds;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Common config: turn-down behaviour and the world-gen bed replacement toggles. */
public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue TURN_DOWN_ENABLED = BUILDER
            .comment("Whether beds are turned down (blanket folded back) while players are able to sleep")
            .define("turnDownEnabled", true);

    public static final ModConfigSpec.IntValue TURN_DOWN_CHECK_INTERVAL = BUILDER
            .comment("How often, in ticks, each bed re-checks whether it should be turned down (20 ticks = 1 second)")
            .defineInRange("turnDownCheckInterval", 100, 20, 1200);

    public static final ModConfigSpec.BooleanValue REPLACE_WORLD_GEN_BEDS = BUILDER
            .comment("Replace vanilla beds with messy beds in newly generated chunks (villages, igloos, camps, modded structures)")
            .define("replaceWorldGenBeds", true);

    public static final ModConfigSpec.BooleanValue CONVERT_EXISTING_CHUNKS = BUILDER
            .comment("Also replace vanilla beds in chunks that were generated before this mod was installed, the first time they load")
            .define("convertExistingChunks", true);

    public static final ModConfigSpec.BooleanValue REPLACE_PLACED_BEDS = BUILDER
            .comment("Replace a vanilla bed with the messy bed of the same colour when a player or dispenser places one")
            .define("replacePlacedBeds", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {}
}
