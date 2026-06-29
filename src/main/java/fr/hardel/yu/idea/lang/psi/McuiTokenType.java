package fr.hardel.yu.idea.lang.psi;

import com.intellij.psi.tree.IElementType;
import fr.hardel.yu.idea.lang.McuiLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public final class McuiTokenType extends IElementType {

    public McuiTokenType(@NonNls @NotNull String debugName) {
        super(debugName, McuiLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "McuiTokenType." + super.toString();
    }
}
