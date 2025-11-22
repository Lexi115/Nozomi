package com.github.lexi115.projectNozomi.shop.rewards;

import com.github.lexi115.projectNozomi.ProjectNozomi;
import com.github.lexi115.projectNozomi.misc.StringUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

@Singleton
public class RewardUtils {

    private final ProjectNozomi plugin;

    private final StringUtils stringUtils;

    @Inject
    public RewardUtils(final ProjectNozomi plugin, final StringUtils stringUtils) {
        this.plugin = plugin;
        this.stringUtils = stringUtils;
    }

    public @NonNull @Unmodifiable List<Reward> parseFrom(final @NonNull List<String> rewardStrings) {
        return rewardStrings.stream().map(this::parseFrom).toList();
    }

    public @NonNull Reward parseFrom(final @NonNull String string) {
        // Command reward
        if (string.startsWith("cmd:")) {
            return new CommandReward(stringUtils, string.replaceFirst("cmd:", "").trim());

        // Money reward
        } else if (string.startsWith("money:")) {
            return new MoneyReward(plugin.getVaultExtension(),
                    Double.parseDouble(string.replaceFirst("money:", "").trim()));
        }
        throw new InvalidRewardException();
    }
}
