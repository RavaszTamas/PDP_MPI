package ro.ubb;

public class Pair<K,V>{
    K key;

    V value;

    @Override
    public String toString() {
        return "Pair{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }

    // Constructor
    public Pair(K key, V value) {
        this.key = key;
        this.value=value;
    }
}