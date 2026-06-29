package fr.hardel.yu.idea;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

public final class McuiBundle extends DynamicBundle {

    @NonNls
    private static final String BUNDLE = "messages.McuiBundle";

    private static final McuiBundle INSTANCE = new McuiBundle();

    private McuiBundle() {
        super(BUNDLE);
    }

    public static @Nls String message(@PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        return INSTANCE.getMessage(key, params);
    }
}
