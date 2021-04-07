package Util.Type;

import Util.CompilationError;
import Util.LocationType;

// null type cannot be as left value
public class NullType implements Type {
    @Override public String getType() { return "null"; }

    @Override public int getDim() { return 0; }

    @Override
    public void assignable(Type otherType, LocationType location) {
        throw new CompilationError("Null Type not assignable with " + otherType.getType(), location);
    }

    @Override
    public void comparable(Type otherType, LocationType location) {
        if (otherType.getType().equals("null")) return;
        throw new CompilationError("Null Type not comparable with " + otherType.getType(), location);
    }
}
