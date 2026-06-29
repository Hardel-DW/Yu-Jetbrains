package fr.hardel.yu.idea.lang.editor;

import com.intellij.lang.Commenter;
import org.jetbrains.annotations.Nullable;

public final class McuiCommenter implements Commenter {

    @Override
    public @Nullable String getLineCommentPrefix() {
        return null;
    }

    @Override
    public @Nullable String getBlockCommentPrefix() {
        return "<!--";
    }

    @Override
    public @Nullable String getBlockCommentSuffix() {
        return "-->";
    }

    @Override
    public @Nullable String getCommentedBlockCommentPrefix() {
        return null;
    }

    @Override
    public @Nullable String getCommentedBlockCommentSuffix() {
        return null;
    }
}
