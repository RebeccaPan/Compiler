package parser;

import Util.CompilationError;
import Util.LocationType;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class MxErrorListener extends BaseErrorListener {
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int ln, int col, String msg, RecognitionException e) {
        throw new CompilationError(msg, new LocationType(ln, col));
    }
}