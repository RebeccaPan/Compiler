package Util;

// why I cannot find Pair in Java
public class Pair<K, V> {
    private K key;
    private V val;

    public K getK() { return key; }
    public V getV() { return val; }
    public Pair(K _key, V _val) { key = _key; val = _val; }
}
