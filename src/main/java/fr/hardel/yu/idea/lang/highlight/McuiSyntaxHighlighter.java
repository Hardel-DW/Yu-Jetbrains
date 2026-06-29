package fr.hardel.yu.idea.lang.highlight;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import fr.hardel.yu.idea.lang.McuiLexerAdapter;
import fr.hardel.yu.idea.lang.psi.McuiTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public final class McuiSyntaxHighlighter extends SyntaxHighlighterBase {

    public static final TextAttributesKey TAG =
            createTextAttributesKey("MCUI_TAG", DefaultLanguageHighlighterColors.MARKUP_TAG);
    public static final TextAttributesKey TAG_NAME =
            createTextAttributesKey("MCUI_TAG_NAME", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey ATTRIBUTE_NAME =
            createTextAttributesKey("MCUI_ATTRIBUTE_NAME", DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE);
    public static final TextAttributesKey ATTRIBUTE_VALUE =
            createTextAttributesKey("MCUI_ATTRIBUTE_VALUE", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("MCUI_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);
    public static final TextAttributesKey ENTITY =
            createTextAttributesKey("MCUI_ENTITY", DefaultLanguageHighlighterColors.MARKUP_ENTITY);
    public static final TextAttributesKey INTERPOLATION_DELIMITER =
            createTextAttributesKey("MCUI_INTERPOLATION_DELIMITER", DefaultLanguageHighlighterColors.BRACES);
    public static final TextAttributesKey INTERPOLATION_EXPRESSION =
            createTextAttributesKey("MCUI_INTERPOLATION_EXPRESSION", DefaultLanguageHighlighterColors.IDENTIFIER);
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("MCUI_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);

    private static final Map<IElementType, TextAttributesKey[]> KEYS = new HashMap<>();

    static {
        register(TAG, McuiTypes.L_ANGLE, McuiTypes.L_ANGLE_SLASH, McuiTypes.R_ANGLE, McuiTypes.R_ANGLE_SLASH, McuiTypes.EQ);
        register(TAG_NAME, McuiTypes.TAG_NAME);
        register(ATTRIBUTE_NAME, McuiTypes.ATTR_NAME);
        register(ATTRIBUTE_VALUE, McuiTypes.STRING);
        register(COMMENT, McuiTypes.COMMENT);
        register(ENTITY, McuiTypes.ENTITY);
        register(INTERPOLATION_DELIMITER, McuiTypes.INTERP_START, McuiTypes.INTERP_END);
        register(INTERPOLATION_EXPRESSION, McuiTypes.INTERP_EXPR);
        register(BAD_CHARACTER, TokenType.BAD_CHARACTER);
    }

    private static void register(TextAttributesKey key, IElementType... tokens) {
        TextAttributesKey[] packed = {key};
        for (IElementType token : tokens) {
            KEYS.put(token, packed);
        }
    }

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return new McuiLexerAdapter();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        TextAttributesKey[] keys = KEYS.get(tokenType);
        return keys != null ? keys : TextAttributesKey.EMPTY_ARRAY;
    }
}
