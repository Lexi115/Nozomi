package com.github.lexi115.projectNozomi.extensions;

/**
 * Interface for a third party plugin extension (often a soft dependency).
 *
 * @author Lexi115
 * @since 1.0
 */
public interface PluginExtension {

    /**
     * Sets up and loads the extension.
     *
     * @return <code>true</code> if the operation is successful, <code>false</code> otherwise.
     * @since 1.0
     */
    boolean setup();

    /**
     * Checks whether the extension is properly loaded and enabled.
     *
     * @return <code>true</code> if the extension is enabled, <code>false</code> otherwise.
     * @since 1.0
     */
    boolean isEnabled();
}
