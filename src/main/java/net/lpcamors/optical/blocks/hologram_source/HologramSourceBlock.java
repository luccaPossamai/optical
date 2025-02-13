package net.lpcamors.optical.blocks.hologram_source;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlockEntity;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftScreen;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.lpcamors.optical.COShapes;
import net.lpcamors.optical.COUtils;
import net.lpcamors.optical.blocks.COBlockEntities;
import net.lpcamors.optical.blocks.IBeamReceiver;
import net.lpcamors.optical.blocks.IBeamSource;
import net.lpcamors.optical.blocks.optical_source.BeamHelper;
import net.lpcamors.optical.gui.HologramSourceScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class HologramSourceBlock extends HorizontalDirectionalBlock implements IBeamReceiver, IWrenchable, IBE<HologramSourceBlockEntity> {

    public static final BooleanProperty CONNECTED_POSITIVE = BooleanProperty.create("positive_connection");
    public static final BooleanProperty CONNECTED_NEGATIVE = BooleanProperty.create("negative_connection");

    public HologramSourceBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(CONNECTED_POSITIVE, Boolean.FALSE));
        registerDefaultState(defaultBlockState().setValue(CONNECTED_NEGATIVE, Boolean.FALSE));
    }


    @Override
    public boolean useCenteredIncidence() {
        return false;
    }

    @Override
    public BlockState updateAfterWrenched(BlockState newState, UseOnContext context) {
        return updateStateConnections(IWrenchable.super.updateAfterWrenched(newState, context), context.getLevel(), context.getClickedPos());
    }

    @Override
    public void receive(IBeamSource iBeamSource, BlockState state, BlockPos lastPos, BeamHelper.BeamProperties beamProperties, int lastIndex) {
        Direction direction = beamProperties.direction;
        HologramSourceBlockEntity be = this.getBlockEntity(iBeamSource.getLevel(), lastPos);
        if(!beamProperties.getType().equals(BeamHelper.BeamType.VISIBLE) || be == null || state.getValue(FACING).getAxis().equals(direction.getAxis())) return;

        BlockPos pos = be.getBlockPos();
        if(be.changeState(iBeamSource.getBlockPos(), beamProperties)){
            iBeamSource.addDependent(pos);
            IBeamSource.propagateLinearBeamVar(iBeamSource, lastPos, beamProperties, lastIndex);

        }
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean p_60519_) {
        super.onRemove(state, world, pos, newState, p_60519_);
        if (world.isClientSide)
            return;
        world.removeBlockEntity(pos);
        updateNeighbourConnections(state, world, pos, true);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos otherPos, boolean p_60514_) {
        super.neighborChanged(state, level, pos, block, otherPos, p_60514_);
        if (level.isClientSide)
            return;
        //updateNeighbourConnections(state, level, pos);
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        super.onPlace(state, world, pos, oldState, isMoving);
        if (world.isClientSide)
            return;
        updateNeighbourConnections(state, world, pos, false);

    }

    @Override
    public @NotNull VoxelShape getShape(BlockState p_60555_, @NotNull BlockGetter p_60556_, @NotNull BlockPos p_60557_, @NotNull CollisionContext p_60558_) {
        return (COShapes.HOLOGRAM_SOURCE).get(p_60555_.getValue(FACING));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        super.createBlockStateDefinition(p_49915_.add(FACING).add(CONNECTED_POSITIVE).add(CONNECTED_NEGATIVE));
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite());
        return updateStateConnections(state, context.getLevel(), context.getClickedPos());

    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player,
                                          @NotNull InteractionHand interactionHand, @NotNull BlockHitResult blockHitResult) {
        if(player.isShiftKeyDown()) return InteractionResult.PASS;


        ItemStack stack = player.getItemInHand(interactionHand);

        if(!stack.isEmpty()){
            HologramSourceBlockEntity be = getBlockEntity(level, pos);
            if(be == null) return InteractionResult.PASS;
            if(stack.is(AllItems.WRENCH.asItem())) return super.use(state, level, pos, player, interactionHand, blockHitResult);
            be = be.getController();
            if(be == null) return InteractionResult.PASS;
            be.setItemStack(stack.copy());
            return InteractionResult.SUCCESS;
        }

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> withBlockEntityDo(level, pos, be -> this.displayScreen(be, player)));
        return InteractionResult.SUCCESS;
    }

    @Override
    public Class<HologramSourceBlockEntity> getBlockEntityClass() {
        return HologramSourceBlockEntity.class;
    }
    @Override
    public BlockEntityType<? extends HologramSourceBlockEntity> getBlockEntityType() {
        return COBlockEntities.HOLOGRAM_SOURCE.get();
    }
    public static BlockState updateStateConnections(BlockState state, BlockGetter level, BlockPos pos){
        boolean f1 = canConnect(state, level.getBlockState(pos.relative(Direction.fromAxisAndDirection(getConnectionAxis(state), Direction.AxisDirection.POSITIVE))));
        boolean f2 = canConnect(state, level.getBlockState(pos.relative(Direction.fromAxisAndDirection(getConnectionAxis(state), Direction.AxisDirection.NEGATIVE))));
        Direction direction = state.getValue(FACING);
        boolean f = direction.equals(Direction.EAST) || direction.equals(Direction.NORTH);
        BlockState state1 = state.setValue(f ? CONNECTED_NEGATIVE : CONNECTED_POSITIVE, f1);
        state1 = state1.setValue(f ? CONNECTED_POSITIVE : CONNECTED_NEGATIVE, f2);
        return state1;
    }
    protected static boolean canConnect(BlockState state, BlockState other) {
        return other.getBlock() instanceof HologramSourceBlock && state.getValue(FACING).getAxis() == other.getValue(FACING).getAxis();
    }

    protected static Optional<HologramSourceBlockEntity> getConnection(BlockState state, BlockPos pos, Level level, Direction.AxisDirection axisDirection){
        if(getConnectionAxis(state).isVertical()) return Optional.empty();
        Optional<HologramSourceBlockEntity> op = Optional.ofNullable(COUtils.getBlockEntity(level, pos.relative(Direction.fromAxisAndDirection(getConnectionAxis(state), axisDirection)), HologramSourceBlockEntity.class));
        if(op.isPresent()) op = getConnectionAxis(op.get().getBlockState()).equals(getConnectionAxis(state)) ? op : Optional.empty();
        return op;
    }
    protected static Direction.Axis getConnectionAxis(BlockState state){
        return state.getValue(FACING).getCounterClockWise().getAxis();
    }
    public static void updateNeighbourConnections(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, boolean onRemove){

        List<HologramSourceBlockEntity> bes =new ArrayList<>();
        getConnection(state, pos, level, Direction.AxisDirection.POSITIVE).ifPresent(be -> {
            level.setBlock(be.getBlockPos(), HologramSourceBlock.updateStateConnections(be.getBlockState(), level, be.getBlockPos()), 3);
            bes.add(be);
        });
        getConnection(state, pos, level, Direction.AxisDirection.NEGATIVE).ifPresent(be -> {
            level.setBlock(be.getBlockPos(), HologramSourceBlock.updateStateConnections(be.getBlockState(), level, be.getBlockPos()), 3);
            bes.add(be);
        });
        if(!bes.isEmpty()) {
            if(onRemove) {
                bes.forEach(HologramSourceBlockEntity::onAdded);
            } else {
                ((HologramSourceBlock)state.getBlock()).getBlockEntity(level, pos).onAdded();
            }


        }
    }

    @OnlyIn(value = Dist.CLIENT)
    protected void displayScreen(HologramSourceBlockEntity be, Player player) {
        if (player instanceof LocalPlayer)
            ScreenOpener.open(new HologramSourceScreen(be));
    }


    public static <T extends Block> Function<BlockState, ModelFile> getBlockModel(DataGenContext<Block, T> c, RegistrateBlockstateProvider p) {
        return state -> AssetLookup.partialBaseModel(c, p, getNameForState(state));
    }
    private static String getNameForState(BlockState state){
        boolean f1 = state.getValue(CONNECTED_POSITIVE);
        boolean f2 = state.getValue(CONNECTED_NEGATIVE);
        return f1 && f2 ? "connected" : f1 ? "connected_positive" : f2  ? "connected_negative" : "not_connected";
    }

}
