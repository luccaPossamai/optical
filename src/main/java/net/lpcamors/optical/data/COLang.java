package net.lpcamors.optical.data;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.LangBuilder;
import net.lpcamors.optical.COMod;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public class COLang {

    public static void initiate(){}

    static {

        Prefixes.OPTICAL.register("gui.hologram_source.title", "Hologram Source");
        Prefixes.OPTICAL.register("gui.hologram_source.mode", "Rotation Mode");
        Prefixes.OPTICAL.register("gui.hologram_source.mode_clockwise", "Clockwise");
        Prefixes.OPTICAL.register("gui.hologram_source.mode_counterclockwise", "Counterclockwise");
        Prefixes.OPTICAL.register("gui.hologram_source.mode_specific_angle", "At angle");
        Prefixes.TOOLTIP.register("gui.goggles.absorption_polarizing_filter", "Polarizing Filter Stats:");
        Prefixes.TOOLTIP.register("gui.behaviour.optical_source", "Propagated Beam Polarization");
        Prefixes.TOOLTIP.register("gui.goggles.beam_properties", "Beam Properties:");
        Prefixes.TOOLTIP.register("gui.goggles.optical_sensor", "Optical Sensor Properties:");

        Prefixes.CREATE.register("gui.goggles.beam_type", "Beam Type:");
        Prefixes.CREATE.register("gui.goggles.propagation_range", "Propagation Range:");
        Prefixes.CREATE.register("gui.goggles.polarization", "Polarization:");
        Prefixes.CREATE.register("gui.goggles.optical_sensor.mode", "Mode");
        Prefixes.CREATE.register("gui.goggles.optical_sensor.mode.intensity", "Intensity");
        Prefixes.CREATE.register("gui.goggles.optical_sensor.mode.color", "Color");
        Prefixes.CREATE.register("gui.goggles.optical_sensor.mode.digital", "Digital");

        Prefixes.JEI.register("focusing.sequence.0", "Sequenced Radio Focusing");
        Prefixes.JEI.register("focusing.sequence.1", "Sequenced Microwave Focusing");
        Prefixes.JEI.register("focusing.sequence.2", "Sequenced Visible Focusing");
        Prefixes.JEI.register("focusing.sequence.3", "Sequenced Gamma Focusing");
        Prefixes.JEI.register("focusing.sequence.4", "Sequenced Focusing");
        Prefixes.JEI.register("focusing", "Focusing");

        Prefixes.JEI.register("required_beam_type.radio", "Require radio waves");
        Prefixes.JEI.register("required_beam_type.microwave", "Require microwaves");
        Prefixes.JEI.register("required_beam_type.visible", "Require visible light");
        Prefixes.JEI.register("required_beam_type.gamma", "Require gamma rays");
        Prefixes.JEI.register("required_beam_type.none", "No beam type required");

        Prefixes.CREATE.register("beam_type.type.radio","Radio Waves");
        Prefixes.CREATE.register("beam_type.type.microwave","Microwaves");
        Prefixes.CREATE.register("beam_type.type.visible","Visible Light");
        Prefixes.CREATE.register("beam_type.type.gamma","Gamma Ray");
        Prefixes.CREATE.register("beam_type.type.range","Block Range:");
        Prefixes.CREATE.register("polarization.random","Random");
        Prefixes.CREATE.register("polarization.vertical","Vertical");
        Prefixes.CREATE.register("polarization.diagonal_positive","Positive Diagonal");
        Prefixes.CREATE.register("polarization.horizontal","Horizontal");
        Prefixes.CREATE.register("polarization.diagonal_negative","Negative Diagonal");

        Prefixes.CREATIVE_TAB.register("co_base", "Optical Tab");

        Prefixes.DEATH.register("gamma_ray",  "%1$s received a high dose of gamma rays");
    }

    public enum Prefixes {
        OPTICAL(null),
        TOOLTIP("tooltip"),
        CREATE("create"),
        RECIPE("recipe"),
        JEI("jei"),
        CREATIVE_TAB("itemGroup"),
        DEATH("death.attack"),
        ;
        final @Nullable String pFix;
        Prefixes(@Nullable String pFix){
            this.pFix = pFix;
        }

        public void register(String s, String value){
            if(this.pFix == null){
                COMod.REGISTRATE.addRawLang(COMod.ID + "." + s, value);
            } else {
                COMod.REGISTRATE.addLang(this.pFix, new ResourceLocation(COMod.ID, s), value);
            }
        }

        public MutableComponent translate(String key, Object... args){
            String s = this.pFix + "." + COMod.ID + "." + key;
            if(this.pFix == null){
                s = COMod.ID + "." + key;
            }
            return Components.translatable(s, resolveBuilders(args));
        }


    }



    public static Object[] resolveBuilders(Object[] args) {
        for (int i = 0; i < args.length; i++)
            if (args[i]instanceof LangBuilder cb)
                args[i] = cb.component();
        return args;
    }



}
