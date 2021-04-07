package Util.Type;

import Util.CompilationError;
import Util.LocationType;

// bool type interacts only with itself
public class BoolType implements Type {
    @Override public String getType() { return "bool"; }

    @Override public int getDim() { return 0; }

    @Override
    public void assignable(Type otherType, LocationType location) {
        if (otherType.getType().equals("bool")) return;
        throw new CompilationError("Bool Type not assignable with " + otherType.getType(), location);
    }

    @Override
    public void comparable(Type otherType, LocationType location) {
        if (otherType.getType().equals("bool")) return;
        throw new CompilationError("Bool Type not comparable with " + otherType.getType(), location);
    }
}
