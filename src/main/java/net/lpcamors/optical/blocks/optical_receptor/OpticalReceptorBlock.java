package net.lpcamors.optical.blocks.optical_receptor;

import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.block.IBE;
import net.lpcamors.optical.COIcons;
import net.lpcamors.optical.COMod;
import net.lpcamors.optical.blocks.IBeamReceiver;
import net.lpcamors.optical.blocks.COBlockEntities;
import net.lpcamors.optical.blocks.IBeamSource;
import net.lpcamors.optical.blocks.optical_source.BeamHelper;
import net.lpcamors.optical.items.COItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Optional;

public class OpticalReceptorBlock extends DirectionalAxisKineticBlock implements IBE<OpticalReceptorBlockEntity>, IBeamReceiver {

    public static final OpticalReceptorShaper SHAPER = OpticalReceptorShaper.make();
    public final OpticalReceptorGearHeaviness heaviness;

    public static OpticalReceptorBlock light(Properties properties){
        return new OpticalReceptorBlock(properties, OpticalReceptorGearHeaviness.LIGHT);
    }


    public static OpticalReceptorBlock heavy(Properties properties){
        return new OpticalReceptorBlock(properties, OpticalReceptorGearHeaviness.HEAVY);
    }

    private OpticalReceptorBlock(Properties properties, OpticalReceptorGearHeaviness heaviness) {
        super(properties);
        this.heaviness = heaviness;
    }


    @Override
    public void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState) {
        super.onBlockStateChange(level, pos, oldState, newState);
        if(newState.is(this)){
            if(level.getBlockEntity(pos) instanceof OpticalReceptorBlockEntity be){
                be.updateDirectionMap();
            }
        }
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand interactionHand, @NotNull BlockHitResult blockHitResult) {
        if(level.getBlockEntity(pos) instanceof OpticalReceptorBlockEntity opticalReceptorBlockEntity){
            ItemStack stack = player.getItemInHand(interactionHand);
            boolean f;
            if(stack.getItem().equals(COItems.OPTICAL_DEVICE.asItem())){
                ItemStack stack1 = stack.copy();
                stack1.setCount(1);
                f = opticalReceptorBlockEntity.addSensor(stack1, blockHitResult.getDirection());
                if(f) stack.shrink(1);
                return f ? InteractionResult.SUCCESS : InteractionResult.PASS;
            } else if(player.isShiftKeyDown() && player.getItemInHand(interactionHand).isEmpty()){
                f = opticalReceptorBlockEntity.removeSensor(blockHitResult.getDirection(), Optional.of(player));
                return f ? InteractionResult.SUCCESS : InteractionResult.PASS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return super.onWrenched(state, context);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level world = context.getLevel();
        Direction face = context.getClickedFace();
        BlockPos placedOnPos = context.getClickedPos()
                .relative(context.getClickedFace()
                        .getOpposite());
        BlockState placedOnState = world.getBlockState(placedOnPos);
        Block block = placedOnState.getBlock();

        if (block instanceof IRotate && ((IRotate) block).hasShaftTowards(world, placedOnPos, placedOnState, face)) {
            BlockState toPlace = defaultBlockState();
            Direction horizontalFacing = context.getHorizontalDirection();
            Direction nearestLookingDirection = context.getNearestLookingDirection();
            boolean lookPositive = nearestLookingDirection.getAxisDirection() == Direction.AxisDirection.POSITIVE;
            if (face.getAxis() == Direction.Axis.X) {
                toPlace = toPlace.setValue(FACING, lookPositive ? Direction.NORTH : Direction.SOUTH)
                        .setValue(AXIS_ALONG_FIRST_COORDINATE, true);
            } else if (face.getAxis() == Direction.Axis.Y) {
                toPlace = toPlace.setValue(FACING, horizontalFacing.getOpposite())
                        .setValue(AXIS_ALONG_FIRST_COORDINATE, horizontalFacing.getAxis() == Direction.Axis.X);
            } else {
                toPlace = toPlace.setValue(FACING, lookPositive ? Direction.WEST : Direction.EAST)
                        .setValue(AXIS_ALONG_FIRST_COORDINATE, false);
            }

            return toPlace;
        }

        return super.getStateForPlacement(context);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pNewState.is(pState.getBlock())) {
            withBlockEntityDo(pLevel, pPos, be -> {
                be.sensor.forEach(itemStack -> Block.popResource(pLevel, pPos, itemStack));
            });
            pLevel.removeBlockEntity(pPos);
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    protected Direction getFacingForPlacement(BlockPlaceContext context) {
        return context.getClickedFace();
    }

    @Override
    protected boolean getAxisAlignmentForPlacement(BlockPlaceContext context) {
        return context.getHorizontalDirection()
                .getAxis() != Direction.Axis.X;
    }


    @Override
    public Class<OpticalReceptorBlockEntity> getBlockEntityClass() {
        return OpticalReceptorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends OpticalReceptorBlockEntity> getBlockEntityType() {
        return this.heaviness.id == 2 ? COBlockEntities.CAPACITY_OPTICAL_RECEPTOR.get() : COBlockEntities.OPTICAL_RECEPTOR.get();
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return SHAPER.get(p_60555_.getValue(FACING), p_60555_.getValue(AXIS_ALONG_FIRST_COORDINATE));
    }
    @Override
    public boolean useCenteredIncidence() {
        return true;
    }
    @Override
    public void receive(IBeamSource iBeamSource, BlockState state, BlockPos lastPos, BeamHelper.BeamProperties beamProperties, int lastIndex) {

        OpticalReceptorBlockEntity opticalLaserReceptorBlockEntity = this.getBlockEntity(iBeamSource.getLevel(), lastPos);
        if(opticalLaserReceptorBlockEntity == null) return;

        BlockPos pos = opticalLaserReceptorBlockEntity.getBlockPos();
        if(opticalLaserReceptorBlockEntity.changeState(beamProperties.direction, iBeamSource.getBlockPos(), beamProperties)){
            iBeamSource.addDependent(pos);
        }
    }

    public static boolean canPlaceSensorAt(Direction direction, BlockState state){
        boolean axisAlong = state.getValue(AXIS_ALONG_FIRST_COORDINATE);
        Direction stateDirection = state.getValue(FACING);
        boolean f1 = stateDirection.getAxis().isHorizontal() && (stateDirection.getAxis() == Direction.Axis.X) == axisAlong;
        if(f1) return direction.getAxis().isHorizontal();
        return !direction.equals(state.getValue(FACING).getOpposite()) && !direction.getAxis().equals(getStaticRotationAxis(state));
    }

    public static Direction.Axis getStaticRotationAxis(BlockState state) {
        Direction.Axis pistonAxis = state.getValue(FACING)
                .getAxis();
        boolean alongFirst = state.getValue(AXIS_ALONG_FIRST_COORDINATE);

        if (pistonAxis == Direction.Axis.X)
            return alongFirst ? Direction.Axis.Y : Direction.Axis.Z;
        if (pistonAxis == Direction.Axis.Y)
            return alongFirst ? Direction.Axis.X : Direction.Axis.Z;
        if (pistonAxis == Direction.Axis.Z)
            return alongFirst ? Direction.Axis.X : Direction.Axis.Y;

        throw new IllegalStateException("Unknown axis??");
    }

    public enum OpticalReceptorGearHeaviness implements StringRepresentable {
        LIGHT(0),
        MEDIUM(1),
        HEAVY(2);

        private final int id;
        OpticalReceptorGearHeaviness(int id){
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public String getSerializedName() {
            return COMod.ID + ".gear_heaviness." + this.name().toLowerCase(Locale.ROOT);
        }
    }

}
