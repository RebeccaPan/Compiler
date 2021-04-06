package Util.Type;

import Util.CompilationError;
import Util.LocationType;

// base of anything
public class BaseType implements Type {
    @Override
    public String getType() { return null; }

    @Override
    public int getDim() { return 0; }

    @Override
    public void assignable(Type otherType, LocationType location) {
        throw new CompilationError("Base Type not assignable with " + otherType.getType(), location);
    }

    @Override
    public void comparable(Type otherType, LocationType location) {
        throw new CompilationError("Base Type not comparable with " + otherType.getType(), location);
    }
}
