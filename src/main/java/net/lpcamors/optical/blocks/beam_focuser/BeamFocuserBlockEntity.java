package net.lpcamors.optical.blocks.beam_focuser;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;
import net.lpcamors.optical.COMod;
import net.lpcamors.optical.blocks.IBeamReceiver;
import net.lpcamors.optical.blocks.optical_source.BeamHelper;
import net.lpcamors.optical.data.COLang;
import net.lpcamors.optical.recipes.AnimatedFocus;
import net.lpcamors.optical.recipes.FocusingRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult.HOLD;
import static com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult.PASS;

public class BeamFocuserBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation {

    public static final int PROCESSING_TICK = 40;

    public FilteringBehaviour filtering;
    protected BeltProcessingBehaviour beltProcessing;
    protected BlockFocusingBehaviour customProcess;
    public int processingTicks;
    public int baseProcessingDuration = PROCESSING_TICK;


    private IBeamReceiver.BeamSourceInstance beamSourceInstance = IBeamReceiver.BeamSourceInstance.empty(null);


    public BeamFocuserBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        processingTicks = -1;
    }
    @Override
    public void tick() {
        super.tick();
        if(this.shouldUpdate()){
            this.update();
        }

        if(!this.isVirtual() && (this.speed == 0 || this.beamSourceInstance.optionalBeamProperties().isEmpty())){
            processingTicks = -1;
        }
        if (processingTicks >= 0) {
            processingTicks--;
        }


//        Optional<BeamHelper.BeamProperties> beamProperties = this.beamSourceInstance.optionalBeamProperties();
//        if (processingTicks == -1 && (isVirtual() || !level.isClientSide()) && beamProperties.isPresent()) {
//            BlockFocusingBehaviour.forEach(behaviour -> {
//                if (customProcess != null)
//                    return;
//                if (behaviour.focus(level, worldPosition.below(2), this, this.beamSourceInstance.optionalBeamProperties(), true) > 0) {
//                    processingTicks = PROCESSING_TICK;
//                    customProcess = behaviour;
//                    notifyUpdate();
//                }
//            });
//        }

//        if (processingTicks >= 0 && (this.speed != 0 && this.beamSourceInstance.optionalBeamProperties().isPresent())) {
//            processingTicks--;
//            if (processingTicks == 5 && customProcess != null) {
//                int fillBlock = customProcess.focus(level, worldPosition.below(2), this, this.beamSourceInstance.optionalBeamProperties(), false);
//                customProcess = null;
//                if (fillBlock > 0) {
//                    //tank.getPrimaryHandler()
//                     //       .setFluid(FluidHelper.copyStackWithAmount(currentFluidInTank,
//                     //               currentFluidInTank.getAmount() - fillBlock));
//                    //sendSplash = true;
//                    notifyUpdate();
//                }
//            }
//        }

    }
    
    /** that's all literally because I didn't like the animation with cos(tick)
         probably worked harder than needed :/
        alpha = [0, pi/2] that's a perfect wave function(not at differential means)
        gotta use this in next projects, the alpha adjust the inertia of the waves,
    
        0.00 < alpha < 0.50, you get near a normal cosine, velocity ~ sine
        0.50 < alpha < 1.20, you get a triangular cosine, velocity ~ constant
        1.20 < alpha < 1.57,  you get a pretty weird thing(really don't know what to say),
                    velocity ~ smaller near knots(not zero), and zero in the radius(that's expected),
        1.57 < alpha, just don't think about using this param. in this case,
                    all peak diverges and don't, characterized by discontinuity
                    velocity ~ don't always diverges, but it happens a lot(what do you expect?)

     
     You can see a special case in @{@link AnimatedFocus#getAngleByTick(int, double, double, double)}

     -lpcamors(message to myself...)
     **/
    public double getAngle(float partialTicks, double radius, double k, double alpha){
        //t is the map of the limits into how many wavelengths(in this case 2)
        // it's interesting because processingTicks get smaller, but T gets higher to 2npi
        // that's all because the therm (5 - PROCESSING_TICK) < 0, then t ~ - c * processingTicks
        //that explains why " -(minus) partialTicks"
        int cycles = (int) Math.ceil(Math.abs(this.getSpeed()) / 64D);
        double t = ((cycles + 1) * 2 * Math.PI) * (-partialTicks + this.processingTicks - this.getProcessDuration()) / (k * (5 - this.getProcessDuration()));
        double angle = radius * Math.tan(alpha * Math.cos(k * t)) / Math.tan(alpha);
        return angle;
    }


    public IBeamReceiver.BeamSourceInstance getBeamSourceInstance() {
        return beamSourceInstance;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

        filtering = new FilteringBehaviour(this, new FocuserFilterSlot()).forRecipes();
        behaviours.add(filtering);

        beltProcessing = new BeltProcessingBehaviour(this).whenItemEnters(this::onItemReceived)
                .whileItemHeld(this::whenItemHeld);
        behaviours.add(beltProcessing);

        registerAwardables(behaviours, AllAdvancements.SPOUT, AllAdvancements.FOODS);
    }


    public boolean shouldUpdate(){
        IBeamReceiver.BeamSourceInstance beamSourceInstance1 = this.beamSourceInstance;
        this.beamSourceInstance = this.beamSourceInstance.checkSourceExistenceAndCompatibility(this);
        return !beamSourceInstance1.equals(this.beamSourceInstance);

    }
    public void update(){
        this.setChanged();

    }
    public boolean changeState(BlockPos pos, BeamHelper.BeamProperties beamProperties){
        if(this.beamSourceInstance.optionalBeamProperties().isEmpty()){
            this.beamSourceInstance = new IBeamReceiver.BeamSourceInstance(Optional.of(beamProperties), pos);
            update();
        }
        return this.beamSourceInstance.optionalBeamProperties().get().equals(beamProperties);
    }


    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().expandTowards(0, -2, 0);
    }


    public int getProcessDuration() {
        return (int) (this.baseProcessingDuration * this.getSpeedDurationMultiplier());
    }
    public float getSpeedDurationMultiplier(){
        return (288 - Math.abs(this.getSpeed()))/256F;
    }

    protected BeltProcessingBehaviour.ProcessingResult whenItemHeld(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
        if(this.speed == 0 || beamSourceInstance.optionalBeamProperties().isEmpty()){
            return PASS;
        }
        if (processingTicks != -1 && processingTicks != 5)
            return HOLD;
        RecipeWrapper w = new RecipeWrapper(new ItemStackHandler(2));
        w.setItem(0, transported.stack);
        w.setItem(1, this.filtering.getFilter());
        BeamHelper.BeamType beamType = this.beamSourceInstance.optionalBeamProperties().get().beamType;
        Optional<FocusingRecipe> recipe = BeamFocuserHelper.canBeProcessed(level, w, beamType);
        if (recipe.isEmpty())
            return PASS;

        if (processingTicks == -1) {
            this.baseProcessingDuration = recipe.get().getProcessingDuration();
            processingTicks = this.getProcessDuration() + 5;
            notifyUpdate();
            return HOLD;
        }

        // Process finished

        List<ItemStack> results = recipe.get().rollResults();
        transported.stack.shrink(1);
        ItemStack out = results.isEmpty() ? ItemStack.EMPTY : results.get(0);
        if (!out.isEmpty()) {
            List<TransportedItemStack> outList = new ArrayList<>();
            TransportedItemStack held = null;
            TransportedItemStack result = transported.copy();
            result.stack = out;
            if (!transported.stack.isEmpty())
                held = transported.copy();
            outList.add(result);
            handler.handleProcessingOnItem(transported, TransportedItemStackHandlerBehaviour.TransportedResult.convertToAndLeaveHeld(outList, held));
        }
        notifyUpdate();
        return HOLD;
    }


    protected BeltProcessingBehaviour.ProcessingResult onItemReceived(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
        if(this.speed == 0 || this.beamSourceInstance.optionalBeamProperties().isEmpty())
            return PASS;
        if (handler.blockEntity.isVirtual())
            return PASS;
        RecipeWrapper w = new RecipeWrapper(new ItemStackHandler(2));
        w.setItem(0, transported.stack);
        w.setItem(1, this.filtering.getFilter());
        BeamHelper.BeamType beamType = this.beamSourceInstance.optionalBeamProperties().get().beamType;
        Optional<FocusingRecipe> recipe = BeamFocuserHelper.canBeProcessed(level, w, beamType);
        if (recipe.isEmpty())
            return PASS;
        return HOLD;
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        this.beamSourceInstance = IBeamReceiver.BeamSourceInstance.read(compound);
        this.processingTicks = compound.getInt("ProcessingTicks");
        this.baseProcessingDuration = compound.getInt("ProcessingDuration");
        if (!clientPacket)
            return;
        if(hasLevel())
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);

    }
    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putInt("ProcessingTicks", processingTicks);
        compound.putInt("ProcessingDuration", baseProcessingDuration);
        this.beamSourceInstance.write(compound);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        Lang.builder("tooltip").translate(COMod.ID +".gui.goggles.beam_properties").forGoggles(tooltip);

        if(this.beamSourceInstance != null && this.beamSourceInstance.optionalBeamProperties().isPresent()){
            Lang.text("").add(COLang.Prefixes.CREATE.translate(("create." + COMod.ID + ".gui.goggles.beam_type")).withStyle(ChatFormatting.GRAY)).forGoggles(tooltip);
            Lang.text("").add(COLang.Prefixes.CREATE.translate(this.beamSourceInstance.optionalBeamProperties().get().beamType.getDescriptionId()).withStyle(ChatFormatting.AQUA)).forGoggles(tooltip, 1);
        }

        return super.addToGoggleTooltip(tooltip, isPlayerSneaking);

    }

    public static class FocuserFilterSlot extends ValueBoxTransform.Sided {

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return state.getValue(BeamFocuserBlock.HORIZONTAL_FACING).getClockWise().getAxis() == direction.getAxis();
        }

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 10f, 15.5f);
        }


    }




}
