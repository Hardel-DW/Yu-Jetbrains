package fr.hardel.yu.idea.lang.highlight;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import fr.hardel.yu.idea.McuiBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.Map;

public final class McuiColorSettingsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] DESCRIPTORS = {
            new AttributesDescriptor(McuiBundle.message("color.tag"), McuiSyntaxHighlighter.TAG),
            new AttributesDescriptor(McuiBundle.message("color.tag_name"), McuiSyntaxHighlighter.TAG_NAME),
            new AttributesDescriptor(McuiBundle.message("color.attribute_name"), McuiSyntaxHighlighter.ATTRIBUTE_NAME),
            new AttributesDescriptor(McuiBundle.message("color.attribute_value"), McuiSyntaxHighlighter.ATTRIBUTE_VALUE),
            new AttributesDescriptor(McuiBundle.message("color.comment"), McuiSyntaxHighlighter.COMMENT),
            new AttributesDescriptor(McuiBundle.message("color.entity"), McuiSyntaxHighlighter.ENTITY),
            new AttributesDescriptor(McuiBundle.message("color.interpolation_delimiter"), McuiSyntaxHighlighter.INTERPOLATION_DELIMITER),
            new AttributesDescriptor(McuiBundle.message("color.interpolation_expression"), McuiSyntaxHighlighter.INTERPOLATION_EXPRESSION),
            new AttributesDescriptor(McuiBundle.message("color.bad_character"), McuiSyntaxHighlighter.BAD_CHARACTER),
    };

    @Override
    public @Nullable Icon getIcon() {
        return null;
    }

    @Override
    public @NotNull SyntaxHighlighter getHighlighter() {
        return new McuiSyntaxHighlighter();
    }

    @Override
    public @NotNull String getDemoText() {
        return """
                <column class="gap-2 p-4">
                    <!-- panneau de démonstration -->
                    <text class="text-white">{{ title }}</text>
                    <button id="ok" enabled="true">
                        <text key="ui.ok"/>
                    </button>
                </column>
                """;
    }

    @Override
    public @Nullable Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @Override
    public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public ColorDescriptor @NotNull [] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @Override
    public @NotNull String getDisplayName() {
        return "Mcui";
    }
}
