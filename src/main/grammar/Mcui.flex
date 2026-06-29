package fr.hardel.yu.idea.lang.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import fr.hardel.yu.idea.lang.psi.McuiTypes;

%%

%public
%class McuiLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

%state TAG_NAME_STATE
%state TAG_BODY
%state INTERP_STATE

WHITE_SPACE=[ \t\r\n]+
TAG_NAME_TOKEN=[A-Za-z][A-Za-z0-9_-]*(:[a-z][a-z0-9_./-]*)?
ATTR_NAME_TOKEN=[A-Za-z][A-Za-z0-9_-]*
STRING_DQ=\"[^\"<]*\"
STRING_SQ=\'[^\'<]*\'
ENTITY=&[a-zA-Z]+;
COMMENT=<!--~-->
TEXT_CHUNK=[^<&{} \t\r\n]+

%%

<YYINITIAL> {
  {COMMENT}        { return McuiTypes.COMMENT; }
  "</"             { yybegin(TAG_NAME_STATE); return McuiTypes.L_ANGLE_SLASH; }
  "<"              { yybegin(TAG_NAME_STATE); return McuiTypes.L_ANGLE; }
  "{{"             { yybegin(INTERP_STATE); return McuiTypes.INTERP_START; }
  {ENTITY}         { return McuiTypes.ENTITY; }
  {WHITE_SPACE}    { return TokenType.WHITE_SPACE; }
  {TEXT_CHUNK}     { return McuiTypes.TEXT; }
  "&"              { return McuiTypes.TEXT; }
  "{"              { return McuiTypes.TEXT; }
  "}"              { return McuiTypes.TEXT; }
}

<TAG_NAME_STATE> {
  {WHITE_SPACE}      { return TokenType.WHITE_SPACE; }
  {TAG_NAME_TOKEN}   { yybegin(TAG_BODY); return McuiTypes.TAG_NAME; }
}

<TAG_BODY> {
  {WHITE_SPACE}      { return TokenType.WHITE_SPACE; }
  "/>"               { yybegin(YYINITIAL); return McuiTypes.R_ANGLE_SLASH; }
  ">"                { yybegin(YYINITIAL); return McuiTypes.R_ANGLE; }
  "="                { return McuiTypes.EQ; }
  {STRING_DQ}        { return McuiTypes.STRING; }
  {STRING_SQ}        { return McuiTypes.STRING; }
  {ATTR_NAME_TOKEN}  { return McuiTypes.ATTR_NAME; }
}

<INTERP_STATE> {
  "}}"             { yybegin(YYINITIAL); return McuiTypes.INTERP_END; }
  [^}]+            { return McuiTypes.INTERP_EXPR; }
  "}"              { return McuiTypes.INTERP_EXPR; }
}

[^]              { return TokenType.BAD_CHARACTER; }
