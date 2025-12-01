package com.github.lexi115.projectNozomi.shop.rewards;

import com.github.lexi115.projectNozomi.ProjectNozomi;
import com.github.lexi115.projectNozomi.misc.StringUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;

import java.util.List;

/**
 * Utility class for reward-related operations.
 *
 * @author Lexi115
 * @since 1.0
 */
@Singleton
public class RewardUtils {

    /**
     * The plugin instance.
     */
    private final ProjectNozomi plugin;

    /**
     * The string utility class.
     */
    private final StringUtils stringUtils;

    /**
     * Constructor.
     *
     * @param plugin      The plugin instance.
     * @param stringUtils The string utility class.
     * @since 1.0
     */
    @Inject
    public RewardUtils(final ProjectNozomi plugin, final StringUtils stringUtils) {
        this.plugin = plugin;
        this.stringUtils = stringUtils;
    }

    /**
     * Parse a list of strings into {@link Reward} objects. The specified strings must have a <code>type:value</code>
     * structure, where <code>type</code> is the reward type (<code>cmd</code>, <code>money</code>)
     * while <code>value</code> differs based on the reward itself.
     * <ul>
     *     <li><code>cmd:give %player% iron_ingot 10</code> - executes a console command to give 10 iron ingots to
     *     the player</li>
     *     <li><code>money:50</code> - deposits $50 into the player's account</li>
     * </ul>
     *
     * @param rewardStrings The list of strings to parse.
     * @return The corresponding list of {@link Reward} objects.
     * @since 1.0
     */
    public @NonNull List<Reward> parseFrom(final @NonNull List<String> rewardStrings) {
        return rewardStrings.stream().map(this::parseFrom).toList();
    }

    /**
     * Parse string into a {@link Reward} object. The specified string must have a <code>type:value</code> structure,
     * where <code>type</code> is the reward type (<code>cmd</code>, <code>money</code>) while <code>value</code>
     * differs based on the reward itself.
     * <ul>
     *     <li><code>cmd:give %player% iron_ingot 10</code> - executes a console command to give 10 iron ingots to
     *     the player</li>
     *     <li><code>money:50</code> - deposits $50 into the player's account</li>
     * </ul>
     * @param string The string to parse.
     * @return The corresponding {@link Reward} object.
     * @since 1.0
     */
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
