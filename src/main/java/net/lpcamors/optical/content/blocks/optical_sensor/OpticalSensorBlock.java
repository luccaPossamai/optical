package net.lpcamors.optical.content.blocks.optical_sensor;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.lpcamors.optical.COMod;
import net.lpcamors.optical.COShapes;
import net.lpcamors.optical.COUtils;
import net.lpcamors.optical.content.blocks.COBlockEntities;
import net.lpcamors.optical.content.blocks.IBeamReceiver;
import net.lpcamors.optical.content.blocks.optical_source.BeamHelper;
import net.lpcamors.optical.content.blocks.optical_source.OpticalSourceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

public class OpticalSensorBlock extends DirectionalBlock implements IWrenchable, IBeamReceiver, IBE<OpticalSensorBlockEntity>{

    public static final EnumProperty<Mode> MODE = EnumProperty.create("sensor_mode", Mode.class);


    public OpticalSensorBlock(Properties p_54120_) {
        super(p_54120_);
    }

    @Override
    public Class<OpticalSensorBlockEntity> getBlockEntityClass() {
        return OpticalSensorBlockEntity.class;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return COShapes.SENSOR.get(state.getValue(FACING));
    }

    @Override
    public BlockEntityType<? extends OpticalSensorBlockEntity> getBlockEntityType() {
        return COBlockEntities.OPTICAL_SENSOR.get();
    }


    @Override
    public InteractionResult use(@NotNull BlockState p_60503_, @NotNull Level p_60504_, @NotNull BlockPos p_60505_, @NotNull Player p_60506_, @NotNull InteractionHand p_60507_, BlockHitResult p_60508_) {
        if(p_60506_.isShiftKeyDown()){
            if(p_60504_.isClientSide) return InteractionResult.SUCCESS;
            p_60504_.setBlock(p_60505_, p_60503_.setValue(MODE, p_60503_.getValue(MODE).getNext()), 3);
            p_60504_.updateNeighborsAt(p_60505_, this);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void receive(OpticalSourceBlockEntity opticalSourceBE, BlockState state, BlockPos lastPos, BeamHelper.BeamProperties beamProperties, List<BlockPos> toRemove, int lastIndex) {
        Direction direction = beamProperties.direction();
        OpticalSensorBlockEntity be = this.getBlockEntity(opticalSourceBE.getLevel(), lastPos);
        if(be == null || state.getValue(FACING).equals(direction)) return;

        BlockPos pos = be.getBlockPos();
        if(be.changeState(opticalSourceBE.getBlockPos(), beamProperties)){
            if(!opticalSourceBE.iBeamReceiverBlockPos.contains(pos)){
                opticalSourceBE.iBeamReceiverBlockPos.add(pos);
            } else {
                toRemove.remove(pos);
            }
            if(state.getValue(MODE).equals(Mode.DIGITAL)){
                opticalSourceBE.propagateLinearBeamVar(lastPos, beamProperties, toRemove, lastIndex);
            }
        }

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> p_49915_) {
        super.createBlockStateDefinition(p_49915_);
        p_49915_.add(FACING).add(MODE).add(BlockStateProperties.LIT);
    }

    public static int getLight(BlockState state) {
        return (state.getValue(BlockStateProperties.LIT) ? 1 : 0) * (state.getValue(MODE).equals(Mode.DIGITAL) ? 15 : 10);
    }
    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        for(Direction direction : context.getNearestLookingDirections()) {
            BlockState blockstate = this.defaultBlockState().setValue(FACING, direction.getOpposite());
            if (blockstate.canSurvive(context.getLevel(), context.getClickedPos())) {
                return blockstate.setValue(MODE, Mode.INTENSITY).setValue(BlockStateProperties.LIT, Boolean.FALSE);
            }
        }
        return null;
    }
    @Override
    public boolean canSurvive(@NotNull BlockState p_53186_, @NotNull LevelReader p_53187_, @NotNull BlockPos p_53188_) {
        return canAttach(p_53187_, p_53188_, p_53186_.getValue(FACING).getOpposite());
    }

    public static boolean canAttach(LevelReader p_53197_, BlockPos p_53198_, Direction p_53199_) {
        BlockPos blockpos = p_53198_.relative(p_53199_);
        return p_53197_.getBlockState(blockpos).isFaceSturdy(p_53197_, blockpos, p_53199_.getOpposite());
    }

    @Override
    public boolean isSignalSource(@NotNull BlockState p_60571_) {
        return true;
    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        if (side != blockState.getValue(FACING))
            return 0;
        return getSignal(blockState, blockAccess, pos, side);
    }

    @Override
    public int getSignal(@NotNull BlockState state, @NotNull BlockGetter blockGetter, @NotNull BlockPos pos, @NotNull Direction direction) {
        return getBlockEntityOptional(blockGetter, pos).map(OpticalSensorBlockEntity::getSignal)
                .orElse(0);
    }



    public enum Mode implements StringRepresentable {

        INTENSITY(0, new Vec3i(67, 144, 141), optionalBeamProperties -> optionalBeamProperties.map(beamProperties -> (int)(beamProperties.intensity() * 16) - 1).orElse(0)),
        COLOR(1,  new Vec3i(136, 162, 255),optionalBeamProperties -> optionalBeamProperties.map(beamProperties -> 15 - beamProperties.dyeColor().getId()).orElse(0)),
        DIGITAL(2, optionalBeamProperties -> optionalBeamProperties.map(beamProperties -> COUtils.getVec3iFromArray(IntStream.range(0, 3).mapToDouble(i -> beamProperties.dyeColor().getTextureDiffuseColors()[i]).mapToObj(value -> (int) (value * 255)).toList())).orElse(Vec3i.ZERO), optionalBeamProperties -> optionalBeamProperties.isPresent() ? 1 : 0)
        ;
        private final int id;
        private final Function<Optional<BeamHelper.BeamProperties>, Vec3i> color;
        private final Function<Optional<BeamHelper.BeamProperties>, Integer> function;

        Mode(int id, Vec3i color, Function<Optional<BeamHelper.BeamProperties>, Integer> function){
            this(id, optionalBeamProperties -> color, function);
        }
        Mode(int id, Function<Optional<BeamHelper.BeamProperties>, Vec3i> color, Function<Optional<BeamHelper.BeamProperties>, Integer> function){
            this.id = id;
            this.color = color;
            this.function = function;
        }

        public Integer apply(Optional<BeamHelper.BeamProperties> optionalBeamProperties){
            return this.function.apply(optionalBeamProperties);
        }
        public Mode getNext(){
            return Mode.values()[this.id + 1 >= Mode.values().length ? 0 : this.id + 1];
        }

        public Vec3i getColor(Optional<BeamHelper.BeamProperties> optionalBeamProperties) {
            return color.apply(optionalBeamProperties);
        }


        public String getDescriptionId(){
            return "create." + COMod.ID + ".gui.goggles.optical_sensor.mode." + this.getSerializedName();
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }



}

