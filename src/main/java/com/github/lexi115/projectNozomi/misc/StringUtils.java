package com.github.lexi115.projectNozomi.misc;

import com.google.inject.Singleton;

import java.util.Map;

@Singleton
public class StringUtils {

    public String fillPlaceholders(final String text, final Map<String, String> placeholders) {
        return fillPlaceholders(text, placeholders, "%");
    }

    public String fillPlaceholders(
            final String text,
            final Map<String, String> placeholders,
            final String delimiter
    ) {
        var newText = text;
        StringBuilder sb;
        for (var key : placeholders.keySet()) {
            sb = new StringBuilder();
            var placeholder = sb.append(delimiter).append(key).append(delimiter).toString();
            newText = newText.replaceAll(placeholder, placeholders.get(key));
        }
        return newText;
    }
}
