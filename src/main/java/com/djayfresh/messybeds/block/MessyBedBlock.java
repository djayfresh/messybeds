package com.djayfresh.messybeds.block;

import com.djayfresh.messybeds.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A vanilla bed with two extra states.
 * <ul>
 *   <li>{@code messy}: set on both halves when a sleeper leaves the bed, cleared by right-clicking it.</li>
 *   <li>{@code turned_down}: set on both halves while the bed's {@code BedRule} says a player could sleep now.
 *       Driven by a scheduled tick on the head half, so it works without a block entity or renderer.</li>
 * </ul>
 * All sleeping logic, spawn points, explosions and villager handling stay in vanilla {@link BedBlock}.
 */
public class MessyBedBlock extends BedBlock {
    public static final BooleanProperty MESSY = BooleanProperty.create("messy");
    public static final BooleanProperty TURNED_DOWN = BooleanProperty.create("turned_down");

    private static final int STATE_UPDATE_FLAGS = Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE;

    public MessyBedBlock(DyeColor color, BlockBehaviour.Properties properties) {
        super(color, properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(PART, BedPart.FOOT)
                .setValue(OCCUPIED, false)
                .setValue(MESSY, false)
                .setValue(TURNED_DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART, OCCUPIED, MESSY, TURNED_DOWN);
    }

    /** Right-clicking a messy bed makes it instead of sleeping in it. */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (state.getValue(MESSY)) {
            if (!level.isClientSide()) {
                this.setBothHalves(level, pos, state, MESSY, false);
                player.sendOverlayMessage(Component.translatable("block.messybeds.bed.cleaned"));
                level.scheduleTick(headPos(pos, state), this, 1);
            }
            return InteractionResult.SUCCESS_SERVER;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    /** Called by vanilla on the head half when any sleeper (player or villager) leaves the bed. */
    @Override
    public void onStopSleeping(Level level, BlockPos pos) {
        super.onStopSleeping(level, pos);
        if (level.isClientSide()) {
            return;
        }
        BlockState state = level.getBlockState(pos);
        if (!state.is(this)) {
            return;
        }
        this.setBothHalves(level, pos, state, MESSY, true);
        this.setBothHalves(level, pos, level.getBlockState(pos), TURNED_DOWN, false);
        for (Player player : level.players()) {
            if (player.getSleepingPos().filter(pos::equals).isPresent()) {
                player.sendOverlayMessage(Component.translatable("block.messybeds.bed.messy"));
            }
        }
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide() && !oldState.is(this) && state.getValue(PART) == BedPart.HEAD) {
            level.scheduleTick(pos, this, 1);
        }
    }

    /** Head-half tick: keeps {@code turned_down} in sync with whether sleeping is currently allowed, then reschedules. */
    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(PART) != BedPart.HEAD) {
            return;
        }
        boolean turnedDown = Config.TURN_DOWN_ENABLED.get()
                && !state.getValue(MESSY)
                && this.getBedRule(level, pos).canSleep(level);
        if (state.getValue(TURNED_DOWN) != turnedDown) {
            this.setBothHalves(level, pos, state, TURNED_DOWN, turnedDown);
        }
        level.scheduleTick(pos, this, Config.TURN_DOWN_CHECK_INTERVAL.get());
    }

    /** Schedules the first turn-down check for a bed that was placed without going through {@link #onPlace}. */
    public static void scheduleFirstTick(Level level, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof MessyBedBlock bed && state.getValue(PART) == BedPart.HEAD) {
            level.scheduleTick(pos, bed, 1);
        }
    }

    /** Writes a boolean property to this half and, if present, the other half, without triggering shape updates between them. */
    private void setBothHalves(Level level, BlockPos pos, BlockState state, BooleanProperty property, boolean value) {
        level.setBlock(pos, state.setValue(property, value), STATE_UPDATE_FLAGS);
        BlockPos otherPos = pos.relative(getConnectedDirection(state));
        BlockState other = level.getBlockState(otherPos);
        if (other.is(this) && other.getValue(PART) != state.getValue(PART)) {
            level.setBlock(otherPos, other.setValue(property, value), STATE_UPDATE_FLAGS);
        }
    }

    private static BlockPos headPos(BlockPos pos, BlockState state) {
        return state.getValue(PART) == BedPart.HEAD ? pos : pos.relative(state.getValue(FACING));
    }
}
