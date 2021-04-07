package Util.Type;

import Util.CompilationError;
import Util.LocationType;

// void type cannot interact with any other type
public class VoidType implements Type {
    @Override public String getType() { return "null"; }

    @Override public int getDim() { return 0; }

    @Override
    public void assignable(Type otherType, LocationType location) {
        throw new CompilationError("Void Type not assignable with " + otherType.getType(), location);
    }

    @Override
    public void comparable(Type otherType, LocationType location) {
        throw new CompilationError("Void Type not comparable with " + otherType.getType(), location);
    }
}
