package hashmap;

/**
 * A hashmap with initial capacity 16
 * load factor 0.75
 * threshold = loadfactor*capacity
 * Methods: {@code get}, {@code put}, {@code remove}, {@code contains}, {@code size}
 * */
public class MyHashMap<K, V> {

    static final int DEFAULT_CAPACITY = 16;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    static class Node<K,V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> nextNode;

        public Node(int hash, K key, V value, Node<K, V> nextNode) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.nextNode = nextNode;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

    }

    static <k> int hash(k key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }


    private int threshold;
    private int size;
    private Node<K,V>[] table;
    private int capacity;

    public MyHashMap() {

    }

    public int size(){
        return size;
    }

    public V remove(K key){
        if (table == null) {
            return null;
        }
        Node<K,V> node;
        int hash = hash(key);
        int position = hash & (table.length -1);
        Node<K,V> nextNode;

        if(table.length > 0
                &&(node = table[position]) != null) {
            if (node.hash == hash && node.key != null && node.key.equals(key)){
                V value = node.getValue();
                if (node.nextNode != null) {
                    table[position] = node.nextNode;
                }
                table[position] = null;
                --size;
                return value;
            } else if ((nextNode = node.nextNode) != null) {
                Node<K,V> prevNode = node;
                while (nextNode != null) {
                    if (nextNode.hash == hash && nextNode.key != null && nextNode.key.equals(key)) {
                        V value =  nextNode.getValue();
                        prevNode.nextNode = nextNode.nextNode;
                        --size;
                        return value;
                    }
                    prevNode = nextNode;
                    nextNode = nextNode.nextNode;
                }
            }
        }
        --size;
        return null;
    }

    public boolean contains(K key) {
        if (table == null) {
            return false;
        }
        Node<K,V> node, nextNode;
        int hash = hash(key);
        int position = (table.length - 1) & hash;
        if ((node = table[position]) != null
                && (node.key == key || node.key.equals(key))) {
            return true;
        } else if (node != null) {
            if ((nextNode = node.nextNode) != null) {
                while (nextNode != null) {
                    if (nextNode.key == key || node.key.equals(key)) {
                        return true;
                    } else
                        nextNode = nextNode.nextNode;
                }
            }
        }
        return false;
    }

    public V get(K key) {
        if (table == null) {
            return null;
        }
        Node<K,V> node, nextNode;
        int hash = hash(key);
        int position = hash & (table.length - 1);
        if(table.length > 0
                &&(node = table[position]) != null) {
            if (node.hash == hash && node.key != null && node.key.equals(key)){
                return node.getValue();
            } else if ((nextNode = node.nextNode) != null) {
                while (nextNode != null) {
                    if (nextNode.hash == hash && nextNode.key != null && nextNode.key.equals(key)) {
                        return nextNode.getValue();
                    }
                    nextNode = nextNode.nextNode;
                }
            }
        }
        return null;
    }

    public V put(K key, V value) {
        return putValue(hash(key), key, value);
    }

    private V putValue(int hash, K key, V value) {
        Node<K,V>[] midTable;
        int n;
        Node<K,V> node;
        V oldValue = null;

        //if table is null - create table
        if((midTable = table) == null || (n = table.length) == 0){
            n = (midTable = resize()).length;
        }
        //find the correct bucket
        int position = (n-1) & hash;

        //if it is empty create new node and put it there
        if ((node = midTable[position]) == null) {
            midTable[position] = new Node<K,V>(hash, key, value, null);
        } else { // if not -
            Node<K,V> midNode = node;
            while (true) {
                if (node.hash == hash && (node.key != null && node.key.equals(key))) {
                    oldValue = node.getValue();
                    node = new Node<K,V>(hash, key, value, node.nextNode);
                    break;
                }
                if (midNode.nextNode == null) {
                    node.nextNode = new Node<K,V>(hash, key, value, null);
                    break;
                }
                midNode = midNode.nextNode;
            }
        }
        if (++size > threshold) {
            resize();
        }
        return oldValue;
    }

    private Node<K,V>[] resize() {
        Node<K,V>[] oldTable = table;
        int newCapacity = 0, newThreshold = 0;
        int oldCapacity = table == null ? 0 : table.length;


        if (oldCapacity == 0) {
            threshold = (int) (DEFAULT_CAPACITY * DEFAULT_LOAD_FACTOR);
            newCapacity = DEFAULT_CAPACITY;
        } else if (oldCapacity > 0) {
            newCapacity = oldCapacity * 2;
            threshold = (int) (newCapacity * DEFAULT_LOAD_FACTOR);
        }

        Node<K,V>[] newTab = (Node<K,V>[])new Node[newCapacity];
        table = newTab;

        if (oldTable != null) {
            for (int j = 0; j < oldCapacity; ++j) {
                Node<K,V> e;
                if ((e = oldTable[j]) != null) {
                    oldTable[j] = null;
                    if (e.nextNode == null)
                        newTab[e.hash & (newCapacity - 1)] = e;
                    else {
                        Node<K,V> loHead = null, loTail = null;
                        Node<K,V> hiHead = null, hiTail = null;
                        Node<K,V> next;
                        do {
                            next = e.nextNode;
                            if ((e.hash & oldCapacity) == 0) {
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.nextNode = e;
                                loTail = e;
                            }
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.nextNode = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        if (loTail != null) {
                            loTail.nextNode = null;
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.nextNode = null;
                            newTab[j + oldCapacity] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }
}
