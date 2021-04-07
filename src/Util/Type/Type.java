package Util.Type;

import Util.LocationType;

public interface Type {
    String getType();
    int getDim();
    void assignable(Type otherType, LocationType location);
    void comparable(Type otherType, LocationType location);
}
