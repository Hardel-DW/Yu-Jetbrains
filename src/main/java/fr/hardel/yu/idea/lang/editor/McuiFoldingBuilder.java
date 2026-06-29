package fr.hardel.yu.idea.lang.editor;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import fr.hardel.yu.idea.lang.psi.McuiElement;
import fr.hardel.yu.idea.lang.psi.McuiEndTag;
import fr.hardel.yu.idea.lang.psi.McuiTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class McuiFoldingBuilder extends FoldingBuilderEx implements DumbAware {

    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        List<FoldingDescriptor> descriptors = new ArrayList<>();

        for (McuiElement element : PsiTreeUtil.findChildrenOfType(root, McuiElement.class)) {
            ASTNode open = element.getNode().findChildByType(McuiTypes.R_ANGLE);
            McuiEndTag endTag = element.getEndTag();
            if (open == null || endTag == null) {
                continue;
            }

            int start = open.getTextRange().getEndOffset();
            int end = endTag.getTextRange().getStartOffset();
            if (start >= end || document.getLineNumber(start) == document.getLineNumber(end)) {
                continue;
            }

            descriptors.add(new FoldingDescriptor(element.getNode(), TextRange.create(start, end)));
        }

        return descriptors.toArray(FoldingDescriptor.EMPTY_ARRAY);
    }

    @Override
    public @Nullable String getPlaceholderText(@NotNull ASTNode node) {
        return "...";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }
}
