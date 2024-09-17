package net.lpcamors.optical.blocks.encased_mirror;

import com.simibubi.create.content.fluids.pipes.valve.FluidValveBlock;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class EncasedMirrorBlockEntity extends KineticBlockEntity {

    public float rotVelocity;
    public float oAngle;
    public float angle;
    private static final float ANGLE_RANGE = 80F;
    private @Nullable State state = null;


    public EncasedMirrorBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }



    @Override
    public void tick() {
        super.tick();
        float actualSpeed = getSpeed();
        rotVelocity = actualSpeed / (90F / 21F) ;

        this.oAngle = angle;
        this.angle = Mth.clamp(this.angle + rotVelocity, 0F, 90F);
        float i = (angle / 9F);
        if(i >= 9.5){
            this.state = State.PERPENDICULAR;
        } else if(i <= 0.5){
            this.state = State.PARALLEL;
        } else {
            this.state = null;
        }
    }


    public @Nullable State getState(){
        return this.state;
    }


    public boolean isRotating(){
        return this.oAngle != this.angle;
    }


    public float getIndependentAngle(float partialTicks) {
        return Mth.clamp(this.angle + partialTicks * this.rotVelocity, 0F, 90F);
    }

    public @Nullable Direction getReflectedDirection(Direction dir, BlockState state){
        if(this.getState() == null) return null;
        Direction facing = this.getBlockState().getValue(DirectionalKineticBlock.FACING);
        Direction direction = null;

        if(facing.getAxis().isVertical()){
            //ENCASED_MIRROR HORIZONTAL
            if(dir.getAxis().isHorizontal()) {
                direction = dir.getCounterClockWise();
                if (dir.getAxis().equals(Direction.Axis.X)) {
                    direction = direction.getOpposite();
                }
            }
        } else {
            if(dir.getAxis().isVertical()){
                if(facing.getAxis().equals(Direction.Axis.X)){
                    direction = Direction.NORTH;
                } else {
                    direction = Direction.WEST;
                }
                if(facing.getAxisDirection().equals(Direction.AxisDirection.POSITIVE)){
                    direction = direction.getOpposite();
                }
                if(dir.getAxisDirection().equals(Direction.AxisDirection.NEGATIVE)){
                    direction = direction.getOpposite();
                }

            } else {
                if(!dir.getAxis().equals(facing.getAxis())) {
                    direction = Direction.UP;
                    if(facing.getAxisDirection().equals(Direction.AxisDirection.NEGATIVE)){
                        direction = direction.getOpposite();
                    }
                    if(dir.getAxisDirection().equals(Direction.AxisDirection.NEGATIVE)){
                        direction = direction.getOpposite();
                    }
                }
            }
        }

        if(direction != null && !this.getState().isParallel()) direction = direction.getOpposite();

        return direction;
    }


    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        if(compound.contains("AngularPosition")){
            this.angle = compound.getFloat("AngularPosition");
        }
        if(compound.contains("AngularVelocity")){
            this.rotVelocity = compound.getFloat("AngularVelocity");
        }

    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putFloat("AngularPosition", this.angle);
        compound.putFloat("AngularVelocity", this.rotVelocity);

    }

    public enum State {

        PARALLEL(0),
        PERPENDICULAR(1);


        private final int id;
        State(int id){
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public State getPerpendicular(){
            if(this.getAngle() == 0){
                return PERPENDICULAR;
            }
            return PARALLEL;
        }

        public boolean isParallel(){
            return this.equals(PARALLEL);
        }

        public double getAngle(){
            return this.equals(PARALLEL) ? 0D : Math.PI / 2D;
        }
    }

}
