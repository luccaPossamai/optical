package net.lpcamors.optical.data;

import net.lpcamors.optical.COMod;
import net.minecraft.resources.ResourceLocation;

public class COLang {

    public static void initiate(){}

    static {
        tooltip("gui.goggles.absorption_polarizing_filter", "Polarizing Filter Stats:");
        tooltip("gui.behaviour.optical_source", "Propagated Beam Polarization");
        tooltip("gui.goggles.beam_properties", "Beam Properties:");
        tooltip("gui.goggles.optical_sensor", "Optical Sensor Properties:");

        mod("gui.goggles.beam_type", "Beam Type:");
        mod("gui.goggles.propagation_range", "Propagation Range:");
        mod("gui.goggles.polarization", "Polarization:");
        mod("gui.goggles.optical_sensor.mode", "Mode");
        mod("gui.goggles.optical_sensor.mode.intensity", "Intensity");
        mod("gui.goggles.optical_sensor.mode.color", "Color");
        mod("gui.goggles.optical_sensor.mode.digital", "Digital");

        recipe("focusing.sequence", "Sequenced Focusing");
        recipe("focusing", "Focusing");

        mod("recipe.required_beam_type.radio", "Require radio waves");
        mod("recipe.required_beam_type.microwave", "Require microwaves");
        mod("recipe.required_beam_type.visible", "Require visible light");
        mod("recipe.required_beam_type.gamma", "Require gamma rays");
        mod("recipe.required_beam_type.none", "No beam type required");

        mod("beam_type.type.radio","Radio Waves");
        mod("beam_type.type.microwave","Microwaves");
        mod("beam_type.type.visible","Visible Light");
        mod("beam_type.type.gamma","Gamma Ray");
        mod("beam_type.type.range","Block Range:");
        mod("polarization.random","Random");
        mod("polarization.vertical","Vertical");
        mod("polarization.diagonal_positive","Positive Diagonal");
        mod("polarization.horizontal","Horizontal");
        mod("polarization.diagonal_negative","Negative Diagonal");
        common("itemGroup", "co_base", "Optical Tab");
        common("death.attack", "gamma_ray",  "%1$s received a high dose of gamma rays");
    }


    public static void tooltip(String s, String value){
        common("tooltip", s, value);
    }
    public static void mod(String s, String value){
        common("create", s, value);
    }
    public static void recipe(String s, String value){
        common("recipe", s, value);
    }

    public static void common(String pfix, String s, String value){
        COMod.REGISTRATE.addLang(pfix, new ResourceLocation(COMod.ID, s), value);
    }



}
