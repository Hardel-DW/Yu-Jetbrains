package fr.hardel.yu.idea.lang.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import fr.hardel.yu.idea.lang.McuiFileType;
import fr.hardel.yu.idea.lang.McuiLanguage;
import org.jetbrains.annotations.NotNull;

public final class McuiFile extends PsiFileBase {

    public McuiFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, McuiLanguage.INSTANCE);
    }

    @Override
    public @NotNull FileType getFileType() {
        return McuiFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Mcui File";
    }
}
