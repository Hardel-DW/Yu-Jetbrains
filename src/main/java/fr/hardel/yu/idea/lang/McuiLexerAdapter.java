package fr.hardel.yu.idea.lang;

import com.intellij.lexer.FlexAdapter;
import fr.hardel.yu.idea.lang.lexer.McuiLexer;

public final class McuiLexerAdapter extends FlexAdapter {

    public McuiLexerAdapter() {
        super(new McuiLexer(null));
    }
}
