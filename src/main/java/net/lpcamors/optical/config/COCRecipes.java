package net.lpcamors.optical.config;

import com.simibubi.create.foundation.config.ConfigBase;

public class COCRecipes extends ConfigBase{

    public final ConfigBase.ConfigFloat focusingSmokingFailedOutputProbability =
            f(0.0F, 0.0F, 1F, "focusingSmokingFailedOutputProbability", Comments.focusingSmokingFailComment);
    public final ConfigBase.ConfigFloat focusingColoringFailedOutputProbability =
            f(0.1F, 0.0F, 1F, "focusingColoringFailedOutputProbability", Comments.focusingColoringFailComment);
    public final ConfigBase.ConfigFloat focusingSandingFailedOutputProbability =
            f(0.0F, 0.0F, 1F, "focusingSandingFailedOutputProbability", Comments.focusingSandingFailComment);


    @Override
    public String getName() {
        return "optical-recipes";
    }

    public static class Comments {
        static String focusingSmokingFailComment = "Regulate the probability of getting charcoal as a failed output in smoking focusing recipes.";
        static String focusingColoringFailComment = "Regulate the probability of getting charcoal as a failed output in coloring focusing recipes.";
        static String focusingSandingFailComment = "Regulate the probability of getting charcoal as a failed output in sanding focusing recipes.";
    }
}
