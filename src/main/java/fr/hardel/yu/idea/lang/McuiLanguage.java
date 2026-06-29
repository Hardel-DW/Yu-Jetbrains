package fr.hardel.yu.idea.lang;

import com.intellij.lang.Language;

public final class McuiLanguage extends Language {

    public static final McuiLanguage INSTANCE = new McuiLanguage();

    private McuiLanguage() {
        super("Mcui");
    }
}
