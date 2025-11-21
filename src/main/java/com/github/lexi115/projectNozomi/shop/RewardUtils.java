package com.github.lexi115.projectNozomi.shop;

import com.github.lexi115.projectNozomi.misc.StringUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

@Singleton
public class RewardUtils {

    private final StringUtils stringUtils;

    @Inject
    public RewardUtils(final StringUtils stringUtils) {
        this.stringUtils = stringUtils;
    }

    public @NonNull @Unmodifiable List<Reward> parseRewards(final @NonNull List<String> rewardStrings) {
        return rewardStrings.stream().map(this::parseReward).toList();
    }

    @Contract("_ -> new")
    public @NonNull Reward parseReward(final @NonNull String string) {
        if (string.startsWith("cmd:")) {
            return new CommandReward(stringUtils, string.replaceFirst("cmd:", "").trim());
        }
        throw new InvalidRewardException();
    }
}
