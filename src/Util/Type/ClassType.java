package Util.Type;

import AST.TypeNode;
import Util.CompilationError;
import Util.LocationType;

public class ClassType implements Type {
    private String classID;
    public ClassType(String type) { classID = type; }
    @Override public String getType() { return classID; }

    @Override public int getDim() { return 0; }

    @Override
    public void assignable(Type otherType, LocationType location) {
        if (otherType.getType().equals(classID) || otherType.getType().equals("null")) return;
        throw new CompilationError("Class Type not assignable with " + otherType.getType(), location);
    }

    @Override
    public void comparable(Type otherType, LocationType location) {
        if (otherType.getType().equals(classID) || otherType.getType().equals("null")) return;
        throw new CompilationError("Class Type not comparable with " + otherType.getType(), location);
    }
}
