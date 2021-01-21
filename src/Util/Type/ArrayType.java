package Util.Type;

import Util.CompilationError;
import Util.LocationType;

public class ArrayType implements Type {
    private Type type;
    private int dim;

    public ArrayType(Type _type, int _dim) { type = _type; dim = _dim; }

    @Override public String getType() { return type.getType() + "[" + String.valueOf(dim) + "]"; }

    public Type getBaseType() {return type;}

    public void setType(Type type) { this.type = type; }

    @Override public int getDim() { return dim; }
    public void setDim(int dim) { this.dim = dim; }

    @Override
    public void assignable(Type otherType, LocationType location) {
        // TODO
        throw new CompilationError("Array Type not equable with " + otherType.getType(), location);
    }

    @Override
    public void comparable(Type otherType, LocationType location) {
        // TODO
        throw new CompilationError("Array Type not comparable with " + otherType.getType(), location);
    }
}
