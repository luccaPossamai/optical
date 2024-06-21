package net.lpcamors.optical.content.blocks.mirror;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class EncasedMirrorBlockEntity extends KineticBlockEntity {

    public float rotVelocity;
    public float angle;
    private static final float ANGLE_RANGE = 80F;


    public EncasedMirrorBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }


    @Override
    public void tick() {
        super.tick();

        float actualSpeed = getSpeed();
        rotVelocity += ((actualSpeed * 5F) - 2.85F * rotVelocity) * .25f;
        this.angle += rotVelocity;
        if(!this.isRotating() && rotVelocity != 0){
            this.rotVelocity = 0;
            if(Math.abs((Math.abs(this.angle) % 1131) - 565.5) < 282.75){
                this.angle = 565.5F * ((this.angle < 0) ? -1 : 1);
            } else {
                this.angle = 0F;
            }
        }

    }


    public @Nullable State getState(){
        if(Math.abs(Math.abs(this.angle) - 565.5F) <= ANGLE_RANGE) {
            return State.PERPENDICULAR;
        } else if (Math.abs(this.angle) <= ANGLE_RANGE || Math.abs(this.angle) >= 1131 - ANGLE_RANGE) {
            return State.PARALLEL;
        }
        return null;
    }


    public boolean isRotating(){
        return Math.abs(rotVelocity) > 1e-1;
    }


    public float getIndependentAngle(float partialTicks) {
        return (this.angle + partialTicks * this.rotVelocity) / 360;
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
