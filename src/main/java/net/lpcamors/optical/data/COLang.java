package net.lpcamors.optical.data;

import net.lpcamors.optical.COMod;
import net.minecraft.resources.ResourceLocation;

public class COLang {

    public static void initiate(){}

    static {
        tooltip("gui.goggles.absorption_polarizing_filter", "Polarizing Filter Stats:");
        tooltip("gui.behaviour.optical_source", "Propagated Beam Polarization");
        tooltip("gui.goggles.beam_properties", "Beam Properties:");

        create("gui.goggles.beam_type", "Beam Type:");
        create("gui.goggles.propagation_range", "Propagation Range:");
        create("gui.goggles.polarization", "Polarization:");


        create("beam_type.type.radio","Radio Waves");
        create("beam_type.type.microwave","Microwaves");
        create("beam_type.type.visible","Visible Light");
        create("beam_type.type.gamma","Gamma Ray");
        create("beam_type.type.range","Block Range:");
        create("polarization.random","Random");
        create("polarization.vertical","Vertical");
        create("polarization.diagonal_positive","Positive Diagonal");
        create("polarization.horizontal","Horizontal");
        create("polarization.diagonal_negative","Negative Diagonal");
        common("itemGroup", "co_base", "Optical Tab");
        common("death.attack", "gamma_ray",  "%1$s received a high dose of gamma rays");
    }


    public static void tooltip(String s, String value){
        common("tooltip", s, value);
    }
    public static void create(String s, String value){
        common("create", s, value);
    }

    public static void common(String pfix, String s, String value){
        COMod.REGISTRATE.addLang(pfix, new ResourceLocation(COMod.ID, s), value);
    }



}
