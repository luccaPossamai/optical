package net.lpcamors.optical.config;

import com.simibubi.create.foundation.config.ConfigBase;
import com.simibubi.create.infrastructure.config.CRecipes;

public class COCServer extends ConfigBase {

    public final COCRecipes recipes = nested(0, COCRecipes::new, COCServer.Comments.recipes);

    @Override
    public String getName() {
        return "optical-server";
    }

    private static class Comments {
        static String recipes = "Packmakers' control panel for internal optical recipe compat";
    }

}
