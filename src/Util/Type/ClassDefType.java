package Util.Type;

import Util.CompilationError;
import Util.LocationType;

public class ClassDefType implements Type {
    @Override public String getType() { return "class"; }

    @Override public int getDim() { return 0; }

    @Override
    public void assignable(Type otherType, LocationType location) {
        throw new CompilationError("ClassDef Type not assignable with " + otherType.getType(), location);
    }

    @Override
    public void comparable(Type otherType, LocationType location) {
        throw new CompilationError("ClassDef Type not comparable with " + otherType.getType(), location);
    }
}
