package Util.Type;

import Util.CompilationError;
import Util.LocationType;

// int type interacts only with itself
public class IntType implements Type {
    @Override public String getType() { return "int"; }

    @Override public int getDim() { return 0; }

    @Override
    public void assignable(Type otherType, LocationType location) {
        if (otherType.getType().equals("int")) return;
        throw new CompilationError("Int Type not equable with " + otherType.getType(), location);
    }

    @Override
    public void comparable(Type otherType, LocationType location) {
        if (otherType.getType().equals("int")) return;
        throw new CompilationError("Int Type not comparable with " + otherType.getType(), location);
    }
}
