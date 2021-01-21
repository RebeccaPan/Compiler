package Util;

public class CompilationError extends RuntimeException {
    private String msg;
    private LocationType loc;
    public CompilationError(String _msg) {
        msg = _msg;
        loc = null;
    }
    public CompilationError(String _msg, LocationType _loc) {
        msg = _msg;
        loc = _loc;
    }
    @Override
    public String getMessage() {
        if (loc == null)
            return ("CE " + msg + "; " + super.getMessage());
        else
            return ("CE @" + loc.toString() + "; " + msg + "; " + super.getMessage());
    }
}
