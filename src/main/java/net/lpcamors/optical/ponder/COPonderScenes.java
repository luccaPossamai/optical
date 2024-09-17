package net.lpcamors.optical.ponder;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.simibubi.create.foundation.ponder.*;
import com.simibubi.create.foundation.ponder.element.BeltItemElement;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.utility.Pointing;
import net.lpcamors.optical.blocks.beam_focuser.BeamFocuserBlockEntity;
import net.lpcamors.optical.blocks.optical_sensor.OpticalSensorBlock;
import net.lpcamors.optical.items.COItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import oshi.jna.platform.mac.SystemConfiguration;

public class COPonderScenes {

    public static void base(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("opticals.base", "Transmitting Rotational Force with Light");
        scene.configureBasePlate(0, 0, 5);
        BlockPos source = util.grid.at(4,1, 2);
        Selection sourceSystemSelect = util.select.fromTo(4, 1, 2, 5, 1, 2);
        Selection bigWheelSelect = util.select.position(5, 0, 1);
        BlockPos receptor = util.grid.at(0, 1, 2);
        Selection receptorSystemSelect = util.select.fromTo(0, 1, 2, 2, 1, 3);
        BlockPos blocking = util.grid.at(3, 1, 2);
        Selection blockingSelect = util.select.position(blocking);

        scene.idle(5);
        scene.world.showSection(util.select.layer(0), Direction.UP);
        scene.idle(5);
        scene.world.showSection(util.select.fromTo(4, 1, 2, 5, 1, 2), Direction.DOWN);
        changeSpeed(scene, sourceSystemSelect, bigWheelSelect, 0);
        scene.world.setKineticSpeed(receptorSystemSelect, 0);
        scene.idle(10);
        changeSpeed(scene, sourceSystemSelect, bigWheelSelect, 64);
        scene.effects.indicateSuccess(source);
        scene.idle(20);
        scene.overlay.showText(40)
                .text("Optical sources transform rotational force into light")
                .pointAt(util.vector.blockSurface(util.grid.at(4, 1, 2), Direction.UP));
        scene.idle(60);

        scene.world.showSection(util.select.fromTo(0, 1, 2, 2, 1, 3), Direction.DOWN);
        scene.idle(10);
        scene.world.setKineticSpeed(receptorSystemSelect, 64);
        scene.effects.rotationSpeedIndicator(receptor);
        scene.idle(20);
        scene.overlay.showText(40)
                .text("Optical receptors can transform the received light into rotational force")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector.blockSurface(util.grid.at(0, 1, 2), Direction.WEST));
        scene.idle(60);

        scene.world.showSection(blockingSelect, Direction.DOWN);
        scene.world.setKineticSpeed(receptorSystemSelect, 0);
        scene.effects.rotationSpeedIndicator(receptor);
        scene.idle(10);
        scene.overlay.showText(40)
                .text("Tinted glass, non-transparent blocks and entities can block the light")
                .placeNearTarget()
                .attachKeyFrame()
                .pointAt(util.vector.blockSurface(blocking, Direction.UP));
        scene.idle(60);
    }

    public static void beamTypes(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("opticals.beam_types", "Propagating Different Types of Light");
        scene.configureBasePlate(0, 0, 5);
        BlockPos source = util.grid.at(4,1, 2);
        Selection sourceSystemSelect = util.select.fromTo(4, 1, 2, 5, 1, 2);
        Selection bigWheelSelect = util.select.position(5, 0, 1);
        Selection beamSelect = util.select.fromTo(1, 1, 2, 3, 1, 2);
        scene.idle(5);
        scene.world.showSection(util.select.layer(0), Direction.UP);
        scene.idle(5);
        scene.world.showSection(util.select.layer(1), Direction.DOWN);
        changeSpeed(scene, sourceSystemSelect, bigWheelSelect, 0);
        scene.idle(10);
        changeSpeed(scene, sourceSystemSelect, bigWheelSelect, 64);
        scene.effects.indicateSuccess(source);
        scene.idle(20);
        scene.overlay.showText(40)
                .text("Optical sources can propagate different types of light depending on the given rotational force")
                .pointAt(util.vector.blockSurface(util.grid.at(4, 1, 2), Direction.UP));
        scene.idle(60);

        String[] actions =
                new String[] { "Radio Waves,", "Microwaves,", "Visible Light,", "and Gamma Ray"};
        scene.overlay.showText(80)
                .attachKeyFrame()
                .independent(40)
                .placeNearTarget()
                .text("The beam types are:");

        int y = 60;
        for (String s : actions) {
            scene.idle(15);
            scene.overlay.showText(50)
                    .colored(PonderPalette.MEDIUM)
                    .placeNearTarget()
                    .independent(y)
                    .text(s);
            y += 16;
        }
        scene.idle(20);

        scene.overlay.showText(40)
                .independent(y + 4)
                .placeNearTarget()
                .text("Higher the frequency, lower the range of the beam.");
        scene.idle(60);
        changeSpeed(scene, sourceSystemSelect, bigWheelSelect, 256);
        scene.effects.indicateSuccess(source);
        scene.idle(10);
        scene.overlay.showSelectionWithText(beamSelect, 40)
                .attachKeyFrame()
                .colored(PonderPalette.RED)
                .independent(72)
                .placeNearTarget()
                .text("Caution with higher frequencies, entities can be hurt by the beam.");
        scene.idle(60);
    }

    public static void mirror(SceneBuilder scene, SceneBuildingUtil util){
        scene.title("opticals.mirror", "Propagating Beams through mirrors");
        scene.configureBasePlate(0, 0, 5);
        BlockPos mirror = util.grid.at(1, 1, 2);
        BlockPos source = util.grid.at(4,1, 2);
        BlockPos upReceptor = util.grid.at(0,1, 4);
        BlockPos downReceptor = util.grid.at(0,1, 0);
        Selection sourceSystemSelect = util.select.fromTo(4, 1, 2, 5, 1, 2);
        Selection rotationSection = util.select.fromTo(1, 1, 2, 1, 2, 2);
        Selection bigWheelSelect = util.select.position(5, 0, 1);
        Selection upReceptorSelect = util.select.fromTo(0, 1, 4, 1, 1, 4);
        Selection downReceptorSelect = util.select.fromTo(0, 1, 0, 1, 1, 0);
        scene.idle(5);
        scene.world.showSection(util.select.layer(0), Direction.UP);
        scene.idle(5);
        scene.world.showSection(sourceSystemSelect, Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(upReceptorSelect, Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(downReceptorSelect, Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(rotationSection, Direction.DOWN);
        scene.idle(5);
        changeSpeed(scene, sourceSystemSelect, bigWheelSelect, 0);
        scene.idle(10);
        changeSpeed(scene, sourceSystemSelect, bigWheelSelect, 64);
        scene.effects.indicateSuccess(source);

        scene.idle(20);
        scene.world.setKineticSpeed(downReceptorSelect, 64);
        scene.effects.indicateSuccess(downReceptor);
        scene.idle(20);
        scene.overlay.showText(40)
                .text("Encased mirrors can propagate the incident beam perpendicularly.")
                .pointAt(util.vector.blockSurface(mirror, Direction.UP));
        scene.idle(60);



        scene.overlay.showText(40)
                .text("You can rotate it to make the beam propagate wherever you want")
                .pointAt(util.vector.blockSurface(mirror, Direction.UP));

        scene.world.setKineticSpeed(rotationSection, 16);
        scene.world.setKineticSpeed(downReceptorSelect, 0);
        scene.idle(20);
        scene.world.setKineticSpeed(rotationSection, 0);
        scene.idle(10);

        scene.world.setKineticSpeed(upReceptorSelect, 64);
        scene.effects.indicateSuccess(upReceptor);

        scene.idle(40);

    }

    public static void polarizingFilter(SceneBuilder scene, SceneBuildingUtil util){
        scene.title("opticals.polarizing_filter", "Polarizing Filter");
        scene.configureBasePlate(0, 0, 5);

        BlockPos source = util.grid.at(4,1, 2);
        BlockPos receptor = util.grid.at(0,1, 0);
        Selection sourceSystemSelect = util.select.fromTo(4, 1, 2, 5, 1, 2);
        Selection bigWheelSelect = util.select.position(5, 0, 1);
        Selection receptorSystemSelect = util.select.fromTo(0, 1, 0, 1, 1, 0);
        Selection mirror = util.select.position(0 , 1, 2);
        BlockPos filterPos0 = util.grid.at(1, 1, 2);
        Selection filterSelection0 = util.select.position(filterPos0);
        BlockPos filterPos1 = util.grid.at(3, 1, 2);
        Selection filterSelection1 = util.select.position(filterPos1);
        scene.idle(5);
        scene.world.showSection(util.select.layer(0), Direction.UP);
        scene.idle(5);
        scene.world.showSection(sourceSystemSelect, Direction.DOWN);
        scene.world.showSection(receptorSystemSelect, Direction.DOWN);
        scene.world.showSection(mirror, Direction.DOWN);
        scene.idle(5);
        changeSpeed(scene, sourceSystemSelect, bigWheelSelect, 0);
        scene.idle(10);
        changeSpeed(scene, sourceSystemSelect, bigWheelSelect, 32);
        scene.effects.indicateSuccess(source);
        scene.overlay.showText(40)
                .independent(0)
                .text("Propagated beams have certain properties. One of them, is the polarization.");
        scene.idle(60);
        scene.world.setKineticSpeed(receptorSystemSelect, 32);
        scene.effects.rotationSpeedIndicator(receptor);
        scene.overlay.showText(60)
                .text("This receptor is receiving a randomly polarized beam. This means that the beam is not oscillating in a preferred direction.")
                .pointAt(util.vector.blockSurface(receptor, Direction.UP));
        scene.idle(80);

        scene.world.showSection(filterSelection0, Direction.DOWN);
        scene.idle(5);
        scene.world.setKineticSpeed(receptorSystemSelect, 16);
        scene.effects.rotationSpeedIndicator(receptor);
        scene.overlay.showText(40)
                .text("Polarizing filters can change the polarization of the beam and its intensity.")
                .attachKeyFrame()
                .pointAt(util.vector.blockSurface(filterPos0, Direction.UP));
        scene.idle(60);
        scene.overlay.showText(50)
                .independent(0)
                .text("Regardless of the direction of polarization, if the ray is randomly polarized it will always reduce its intensity by half.");
        scene.idle(70);
        scene.world.showSection(filterSelection1, Direction.DOWN);
        scene.idle(5);
        scene.world.setKineticSpeed(receptorSystemSelect, 0);
        scene.effects.rotationSpeedIndicator(receptor);
        scene.overlay.showText(50)
                .text("You can combine polarizing filters to get your desired intensity. Keep in mind that polarizing a beam perpendicularly will nullify its intensity.")
                .attachKeyFrame()
                .pointAt(util.vector.blockSurface(filterPos1, Direction.UP));
        scene.idle(70);
        scene.world.hideSection(filterSelection1, Direction.UP);
        scene.idle(5);
        Vec3 blockSurface = util.vector.blockSurface(source, Direction.NORTH)
                .add(2 / 16f, 0, 3 / 16f);
        scene.overlay.showFilterSlotInput(blockSurface, Direction.NORTH, 80);
        scene.overlay.showControls(new InputWindowElement(blockSurface, Pointing.DOWN).rightClick(), 60);
        scene.idle(20);
        scene.overlay.showText(60)
                .text("The initial polarization can be configured in the source panel.")
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(blockSurface);
        scene.idle(10);
    }

    public static void polarizingCube(SceneBuilder scene, SceneBuildingUtil util){
        scene.title("opticals.polarizing_cube", "Polarizing Cube");
        scene.configureBasePlate(0, 0, 5);

        BlockPos source = util.grid.at(4,1, 3);
        BlockPos polCube = util.grid.at(2, 1, 3 );
        BlockPos mirror = util.grid.at(2, 1, 0);
        Selection polCubeSelect = util.select.position(polCube).add(util.select.position(3,1,3));
        Selection select = util.select.position(mirror).add(util.select.position(0,1,0)).add(util.select.position(0,1,3));

        Selection polarizedBeamVertical = util.select.fromTo(1, 1, 3, 1, 1, 3);
        Selection polarizedBeamHorizontal = util.select.fromTo(2,1, 2, 2, 1, 1).add(util.select.fromTo(1, 1, 0, 1, 1, 0));

        Selection sourceSystemSelect = util.select.fromTo(4, 1, 3, 5, 1, 3);
        Selection bigWheelSelect = util.select.position(5, 0, 2);
        scene.idle(5);
        scene.world.showSection(util.select.layer(0), Direction.UP);
        scene.idle(5);
        scene.world.showSection(sourceSystemSelect, Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(polCubeSelect, Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(select, Direction.DOWN);
        scene.idle(5);
        changeSpeed(scene, sourceSystemSelect, bigWheelSelect, 0);
        scene.idle(10);
        changeSpeed(scene, sourceSystemSelect, bigWheelSelect, 64);
        scene.effects.indicateSuccess(source);
        scene.overlay.showText(40)
                .independent(0)
                .text("Polarizing Beam Splitter blocks can split the beam in two polarized beams.")
                .pointAt(util.vector.blockSurface(polCube, Direction.UP));
        scene.idle(60);

        scene.overlay.showSelectionWithText(polarizedBeamVertical, 40)
                .attachKeyFrame()
                .colored(PonderPalette.BLUE)
                .independent(72)
                .placeNearTarget()
                .text("One of them will propagate straight and be vertically polarized.");
        scene.idle(60);
        scene.overlay.showSelectionWithText(polarizedBeamHorizontal, 40)
                .colored(PonderPalette.BLUE)
                .independent(82)
                .placeNearTarget()
                .text("The other one of them will propagate perpendicularly and be horizontally polarized.");
        scene.idle(60);

    }
    public static void sensor(SceneBuilder scene, SceneBuildingUtil util){
        scene.title("opticals.sensor", "Sensor");
        scene.configureBasePlate(0, 0, 5);

        BlockPos source = util.grid.at(4,1, 2);
        BlockPos sensor = util.grid.at(1, 1, 2);
        BlockPos pol = util.grid.at(2, 1, 2);
        BlockPos glass = util.grid.at(3, 1, 2);
        BlockPos tube = util.grid.at(1, 2, 3);
        Vec3 sensorCenter = util.vector.blockSurface(sensor, Direction.UP).add(0, -8 / 16F, 0);
        Selection sourceSystemSelect = util.select.fromTo(4, 1, 2, 5, 1, 2);
        Selection bigWheelSelect = util.select.position(5, 0, 2);
        Selection sensorGroupSelect = util.select.fromTo(1, 1, 2, 1, 2, 3);
        Selection tubeSelect = util.select.position(tube);
        scene.idle(5);
        scene.world.modifyBlock(sensor, state -> state.setValue(OpticalSensorBlock.MODE, OpticalSensorBlock.Mode.INTENSITY), false);
        scene.world.showSection(util.select.layer(0), Direction.UP);
        scene.idle(5);
        scene.world.showSection(sourceSystemSelect, Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(sensorGroupSelect, Direction.DOWN);
        scene.world.showSection(tubeSelect, Direction.UP);
        scene.idle(5);
        changeSpeed(scene, sourceSystemSelect, bigWheelSelect, 0);
        scene.idle(10);
        changeSpeed(scene, sourceSystemSelect, bigWheelSelect, 32);
        scene.effects.indicateSuccess(source);
        scene.idle(10);
        scene.world.toggleRedstonePower(util.select.position(sensor));
        scene.world.modifyBlockEntityNBT(tubeSelect, NixieTubeBlockEntity.class, nbt -> nbt.putInt("RedstoneStrength", 15));
        scene.effects.indicateRedstone(sensor);
        scene.overlay.showText(40)
                .attachKeyFrame()
                .independent(0)
                .text("Optical sensors emits redstone signal if a beam hits it.")
                .pointAt(sensorCenter);
        scene.idle(60);
        Vec3 blockSurface = util.vector.blockSurface(sensor, Direction.UP)
                .add(0, -8 / 16F, 0);
        scene.overlay.showFilterSlotInput(blockSurface, Direction.NORTH, 80);
        scene.overlay.showControls(new InputWindowElement(blockSurface, Pointing.DOWN).rightClick().whileSneaking(), 60);
        scene.overlay.showText(40)
                .attachKeyFrame()
                .independent(0)
                .text("You can change the criteria of the redstone level mapping by shift + right click.")
                .pointAt(sensorCenter);
        scene.idle(60);
        scene.world.showSection(util.select.position(pol), Direction.DOWN);
        scene.idle(5);
        scene.world.modifyBlockEntityNBT(tubeSelect, NixieTubeBlockEntity.class, nbt -> nbt.putInt("RedstoneStrength", 7));
        scene.overlay.showText(40)
                .attachKeyFrame()
                .independent(0)
                .text("The intensity mode makes a reduction based on the intensity of the incident beam.")
                .pointAt(util.vector.blockSurface(tube, Direction.UP));
        scene.idle(60);
        scene.overlay.showFilterSlotInput(blockSurface, Direction.NORTH, 40);
        scene.overlay.showControls(new InputWindowElement(blockSurface, Pointing.DOWN).rightClick().whileSneaking(), 30);
        scene.world.modifyBlock(sensor, state -> state.setValue(OpticalSensorBlock.MODE, OpticalSensorBlock.Mode.COLOR), false);
        scene.idle(10);
        scene.world.hideSection(util.select.position(pol), Direction.UP);
        scene.world.showSection(util.select.position(glass), Direction.UP);
        scene.world.modifyBlockEntityNBT(tubeSelect, NixieTubeBlockEntity.class, nbt -> nbt.putInt("RedstoneStrength", 6));

        scene.overlay.showText(40)
                .attachKeyFrame()
                .independent(0)
                .text("The color mode maps a specific color to an specific color.")
                .pointAt(util.vector.blockSurface(tube, Direction.UP));
        scene.idle(60);
        scene.overlay.showFilterSlotInput(blockSurface, Direction.NORTH, 40);
        scene.overlay.showControls(new InputWindowElement(blockSurface, Pointing.DOWN).rightClick().whileSneaking(), 30);
        scene.world.modifyBlock(sensor, state -> state.setValue(OpticalSensorBlock.MODE, OpticalSensorBlock.Mode.DIGITAL), false);
        scene.idle(10);
        scene.world.modifyBlockEntityNBT(tubeSelect, NixieTubeBlockEntity.class, nbt -> nbt.putInt("RedstoneStrength", 15));

        scene.overlay.showText(40)
                .attachKeyFrame()
                .independent(0)
                .text("The digital mode make the block full signal if there is a beam incident, if it don't have, then zero redstone signal.")
                .pointAt(util.vector.blockSurface(tube, Direction.UP));
        scene.idle(60);
        scene.overlay.showText(40)
                .independent(0)
                .text("The block also absorb the color of the beam and produces light.")
                .pointAt(sensorCenter);
        scene.idle(60);

    }

    public static void condenser(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("opticals.condenser", "Condenser and Receptor's heaviness");
        scene.configureBasePlate(0, 0, 5);

        Selection middleSourceSelect = util.select.fromTo(3, 1, 2, 4,1,2).add(util.select.fromTo(5,1,0, 5, 1, 5));
        Selection leftSourceSelect = util.select.fromTo(4, 1, 4, 4,1,4);
        Selection rightSourceSelect = util.select.fromTo(4, 1, 0, 4,1,0);
        Selection resultantBeamSelection = util.select.fromTo(0,1,2,1,1,2);
        Selection leftMirror = util.select.fromTo(2,1,3,3,1,4);
        Selection rightMirror = util.select.fromTo(2,1,0,3,1,1);
        BlockPos condenser = util.grid.at(2,1,2);
        BlockPos receptor = util.grid.at(0, 1, 2);
        Selection condenserSelect = util.select.position(condenser);
        Selection receptorSelect = util.select.fromTo(0,1,1,0,1,2);
        scene.world.showSection(util.select.layer(0), Direction.UP);
        scene.idle(5);
        scene.world.showSection(condenserSelect, Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(middleSourceSelect, Direction.DOWN);
        scene.idle(5);
        scene.world.setKineticSpeed(middleSourceSelect, 64);
        scene.idle(5);
        scene.world.showSection(rightSourceSelect, Direction.DOWN);
        scene.world.showSection(rightMirror, Direction.DOWN);
        scene.idle(5);
        scene.world.setKineticSpeed(rightSourceSelect, 64);
        scene.world.showSection(leftSourceSelect, Direction.DOWN);
        scene.world.showSection(leftMirror, Direction.DOWN);
        scene.idle(5);
        scene.world.setKineticSpeed(leftSourceSelect, 64);
        scene.effects.indicateSuccess(condenser);
        scene.idle(10);
        scene.overlay.showText(40)
                .attachKeyFrame()
                .independent(0)
                .text("Optical Condensers can combine up to three beams simultaneously.")
                .pointAt(util.vector.blockSurface(condenser, Direction.UP));
        scene.idle(60);
        String[] actions =
                new String[] { "Speed is the sum of the beam intensity velocities,", "Beam Type and speed direction is the same of the higher beam,", "Visibility depends if there's at least one visible beam composing it,", "Polarization and color follow the composing system."};
        scene.overlay.showText(80)
                .attachKeyFrame()
                .independent(20)
                .placeNearTarget()
                .text("The resultant beam acts exactly like a normal beam. But its initial properties follow these criteria:");
        scene.idle(10);
        int y = 62;
        for (String s : actions) {
            scene.idle(20);
            scene.overlay.showText(50)
                    .colored(PonderPalette.MEDIUM)
                    .placeNearTarget()
                    .independent(y)
                    .text(s);
            y += 32;
        }
        scene.idle(30);
        scene.overlay.showText( 60)
                .colored(PonderPalette.GREEN)
                .placeNearTarget()
                .independent(20)
                .text("Composing system means that the components can compose a different result than itself. Different colors create a white resultant beam, same colors create a beam with the specific color. ");
        scene.idle(40);
        scene.overlay.showText( 40)
                .colored(PonderPalette.BLUE)
                .placeNearTarget()
                .independent(92)
                .text("The same goes to the polarization property.");
        scene.idle(50);

        scene.overlay.showText(40)
                .attachKeyFrame()
                .independent(30)
                .placeNearTarget()
                .text("To transform really fast beams into rotational force you'll need a greater receptor");
        scene.idle(40);
        scene.world.showSection(receptorSelect, Direction.DOWN);
        scene.idle(10);
        scene.world.setKineticSpeed(receptorSelect, 64);
        scene.effects.rotationSpeedIndicator(receptor);
        scene.idle(40);
        scene.overlay.showText(40)
                .independent(0)
                .text("This is a heavy optical receptor. It works more like the light receptor but this one supports more stress.")
                .pointAt(util.vector.blockSurface(receptor, Direction.UP));
        scene.idle(50);
        scene.overlay.showText(40)
                .independent(0)
                .text("The speed of this component goes as one third of the speed of the incident beam.")
                .pointAt(util.vector.blockSurface(receptor, Direction.UP));
        scene.idle(50);

    }

    public static void focuser(SceneBuilder scene, SceneBuildingUtil util){
        scene.title("opticals.beam_focuser", "Beam Focuser");
        scene.configureBasePlate(0, 0, 5);

        BlockPos focuser = util.grid.at(3,3, 2);
        BlockPos source = util.grid.at(3, 4, 4);
        BlockPos mirror = util.grid.at(3,4, 2);
        BlockPos beltStart = util.grid.at(4,1,2);
        Selection base = util.select.fromTo(0,0,0, 4,0,4);
        Selection belt = util.select.fromTo(0,1,2, 4,1,2);
        Selection motor = util.select.fromTo(3,1,3,3,3,3);
        Selection sourceSelect = util.select.fromTo(3,0,5,3,4,5)
                .add(util.select.position(source))
                .add(util.select.fromTo(3,1,4,3,3,4));

        scene.world.showSection(base, Direction.UP);
        scene.idle(5);
        scene.world.showSection(belt, Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(motor, Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(util.select.position(focuser), Direction.DOWN);
        scene.idle(5);
        scene.world.setKineticSpeed(belt, 16);
        scene.world.setKineticSpeed(util.select.position(focuser), 16);
        scene.world.setKineticSpeed(motor, 16);
        scene.world.setKineticSpeed(sourceSelect, 16);
        scene.effects.indicateSuccess(focuser);
        scene.idle(5);
        scene.idle(10);
        scene.overlay.showText(40)
                .attachKeyFrame()
                .independent(0)
                .text("Optical focuser can process items with incident light and rotational power")
                .pointAt(util.vector.blockSurface(focuser, Direction.UP));
        scene.idle(50);
        scene.world.showSection(util.select.position(mirror), Direction.DOWN);
        scene.idle(5);
        scene.world.showSection(sourceSelect, Direction.DOWN);
        scene.overlay.showText(60)
                .independent(0)
                .attachKeyFrame()
                .text("This component needs a incident beam to work properly, you'll need to control the beam type of the source to process certain items")
                .pointAt(util.vector.blockSurface(focuser, Direction.UP));
        scene.idle(70);
        ElementLink<BeltItemElement> item = scene.world.createItemOnBelt(beltStart, Direction.NORTH, new ItemStack(Items.GLASS_PANE));
        scene.idle(30);
        scene.world.stallBeltItem(item, true);
        scene.world.modifyBlockEntity(focuser, BeamFocuserBlockEntity.class, pte -> pte.processingTicks = 40);
        scene.idle(35);
        scene.world.modifyBlockEntity(focuser, BeamFocuserBlockEntity.class, pte -> pte.processingTicks = -1);
        scene.world.removeItemsFromBelt(focuser.below(2));
        scene.world.createItemOnBeltLike(focuser.below(2), Direction.UP, new ItemStack(COItems.MIRROR));
        scene.idle(60);
        scene.overlay.showText(40)
                .independent(0)
                .attachKeyFrame()
                .text("Some recipes need specific items to operate with the required mode")
                .pointAt(util.vector.blockSurface(focuser, Direction.EAST));
        scene.world.modifyBlockEntity(focuser, BeamFocuserBlockEntity.class, pte -> pte.filtering.setFilter(Direction.WEST ,new ItemStack(Items.BLUE_DYE)));
        scene.effects.indicateSuccess(focuser);
        scene.idle(15);
        item = scene.world.createItemOnBelt(beltStart, Direction.NORTH, new ItemStack(Items.WHITE_WOOL));
        scene.idle(30);
        scene.world.stallBeltItem(item, true);
        scene.world.modifyBlockEntity(focuser, BeamFocuserBlockEntity.class, pte -> pte.processingTicks = 40);
        scene.idle(35);
        scene.world.modifyBlockEntity(focuser, BeamFocuserBlockEntity.class, pte -> pte.processingTicks = -1);
        scene.world.removeItemsFromBelt(focuser.below(2));
        scene.world.createItemOnBeltLike(focuser.below(2), Direction.UP, new ItemStack(Items.BLUE_WOOL));
        scene.idle(60);

    }


    public static void changeSpeed(SceneBuilder scene, Selection laser, Selection bigWheel, int speed){
        scene.world.setKineticSpeed(laser, speed);
        scene.world.setKineticSpeed(bigWheel, (int) (speed * (-0.5)));
    }


}
