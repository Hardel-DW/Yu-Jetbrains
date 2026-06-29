package fr.hardel.yu.idea.lang.editor;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import fr.hardel.yu.idea.lang.psi.McuiElement;
import fr.hardel.yu.idea.lang.psi.McuiFile;
import fr.hardel.yu.idea.lang.psi.McuiTypes;
import org.jetbrains.annotations.NotNull;

public final class McuiTypedHandler extends TypedHandlerDelegate {

    @Override
    public @NotNull Result charTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        if (c != '>' || !(file instanceof McuiFile)) {
            return Result.CONTINUE;
        }

        Document document = editor.getDocument();
        int offset = editor.getCaretModel().getOffset();
        CharSequence text = document.getCharsSequence();
        if (offset < 2 || text.charAt(offset - 1) != '>' || text.charAt(offset - 2) == '/') {
            return Result.CONTINUE;
        }

        PsiDocumentManager.getInstance(project).commitDocument(document);
        PsiElement at = file.findElementAt(offset - 1);
        if (at == null || at.getNode().getElementType() != McuiTypes.R_ANGLE) {
            return Result.CONTINUE;
        }

        McuiElement element = PsiTreeUtil.getParentOfType(at, McuiElement.class);
        if (element == null || element.getEndTag() != null) {
            return Result.CONTINUE;
        }

        ASTNode nameNode = element.getNode().findChildByType(McuiTypes.TAG_NAME);
        if (nameNode == null) {
            return Result.CONTINUE;
        }

        document.insertString(offset, "</" + nameNode.getText() + ">");
        return Result.STOP;
    }
}
