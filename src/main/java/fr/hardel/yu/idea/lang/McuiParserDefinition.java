package fr.hardel.yu.idea.lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import fr.hardel.yu.idea.lang.parser.McuiParser;
import fr.hardel.yu.idea.lang.psi.McuiFile;
import fr.hardel.yu.idea.lang.psi.McuiTypes;
import org.jetbrains.annotations.NotNull;

public final class McuiParserDefinition implements ParserDefinition {

    private static final IFileElementType FILE = new IFileElementType(McuiLanguage.INSTANCE);
    private static final TokenSet COMMENTS = TokenSet.create(McuiTypes.COMMENT);
    private static final TokenSet STRINGS = TokenSet.create(McuiTypes.STRING);

    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new McuiLexerAdapter();
    }

    @Override
    public @NotNull PsiParser createParser(Project project) {
        return new McuiParser();
    }

    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return FILE;
    }

    @Override
    public @NotNull TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @Override
    public @NotNull TokenSet getStringLiteralElements() {
        return STRINGS;
    }

    @Override
    public @NotNull PsiElement createElement(ASTNode node) {
        return McuiTypes.Factory.createElement(node);
    }

    @Override
    public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new McuiFile(viewProvider);
    }
}
