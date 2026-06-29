package fr.hardel.yu.idea.lang;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileTypes.LanguageFileType;
import fr.hardel.yu.idea.McuiBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

public final class McuiFileType extends LanguageFileType {

    public static final McuiFileType INSTANCE = new McuiFileType();

    private McuiFileType() {
        super(McuiLanguage.INSTANCE);
    }

    @Override
    public @NonNls @NotNull String getName() {
        return "Mcui";
    }

    @Override
    public @NotNull String getDescription() {
        return McuiBundle.message("filetype.mcui.description");
    }

    @Override
    public @NonNls @NotNull String getDefaultExtension() {
        return "mcui";
    }

    @Override
    public Icon getIcon() {
        return AllIcons.FileTypes.Text;
    }
}
