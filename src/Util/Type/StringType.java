package Util.Type;

import Util.CompilationError;
import Util.LocationType;

public class StringType implements Type {
    @Override public String getType() { return "string"; }

    @Override public int getDim() { return 0; }

    @Override
    public void assignable(Type otherType, LocationType location) {
        if (otherType.getType().equals("string") || otherType.getType().equals("null")) return;
        throw new CompilationError("String Type not equable with " + otherType.getType(), location);
    }

    @Override
    public void comparable(Type otherType, LocationType location) {
        if (otherType.getType().equals("string") || otherType.getType().equals("null")) return;
        throw new CompilationError("String Type not comparable with " + otherType.getType(), location);
    }
}
