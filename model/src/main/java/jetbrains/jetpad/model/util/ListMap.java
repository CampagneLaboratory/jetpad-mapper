package jetbrains.jetpad.model.util;

import com.google.common.base.Objects;

import java.util.*;

public class ListMap<K, V> {
  private static final Object[] EMPTY_ARRAY = new Object[0];

  private Object[] myData = EMPTY_ARRAY;

  public ListMap() {
  }
  
  public boolean containsKey(K key) {
    return findByKey(key) >= 0;
  }
  
  public V remove(K key) {
    int index = findByKey(key);
    if (index >= 0) {
      V value = (V) myData[index + 1];
      removeAt(index);
      return value;
    } else {
      return null;
    }
  }
  
  public Set<K> keySet() {
    return new AbstractSet<K>() {
      @Override
      public Iterator<K> iterator() {
        return mapIterator(false);
      }

      @Override
      public int size() {
        return ListMap.this.size();
      }
    };
  }
  
  public boolean isEmpty() {
    return size() == 0;
  }
  
  public Collection<V> values() {
    return new AbstractCollection<V>() {
      @Override
      public Iterator<V> iterator() {
        return mapIterator(true);
      }

      @Override
      public int size() {
        return ListMap.this.size();
      }
    };
  }
  
  public int size() {
    return myData.length / 2;
  }

  public V put(K key, V value) {
    int index = findByKey(key);
    if (index >= 0) {
      V oldValue = (V) myData[index + 1];
      myData[index + 1] = value;
      return oldValue;
    }
    Object[] newArray = new Object[myData.length + 2];
    System.arraycopy(myData, 0, newArray, 0, myData.length);
    newArray[myData.length] = key;
    newArray[myData.length + 1] = value;
    myData = newArray;
    return null;
  }

  public V get(K key) {
    int index = findByKey(key);
    if (index == -1) {
      return null;
    }
    return (V) myData[index + 1];    
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("{");
    for (int i = 0; i < myData.length; i += 2) {
      K k = (K) myData[i];
      V v = (V) myData[i + 1];
      if (i != 0) {
        builder.append(",");
      }
      builder.append(k).append("=").append(v);
    }
    builder.append("}");

    return builder.toString();
  }

  private<T> Iterator<T> mapIterator(final boolean valueIterator) {
    return new Iterator<T>() {
      int index = 0;
      boolean nextCalled = false;
      @Override
      public boolean hasNext() {
        return index < myData.length;
      }

      @Override
      public T next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        nextCalled = true;
        T value = (T) myData[index + (valueIterator ? 1 : 0)];
        index += 2;
        return value;
      }

      @Override
      public void remove() {
        if (!nextCalled) {
          throw new IllegalStateException();
        }
        index -= 2;
        removeAt(index);
        nextCalled = false;
      }
    };
  }
  
  private int findByKey(K key) {
    for (int i = 0; i < myData.length; i += 2) {
      K k = (K) myData[i];
      if (Objects.equal(key, k)) {
        return i;
      }
    }
    return -1;
  }

  //index is doubled(0, 2, 4...)
  private void removeAt(int index) {
    if (myData.length == 2) {
      myData = EMPTY_ARRAY;
      return;
    }
    Object[] newArray = new Object[myData.length - 2];
    System.arraycopy(myData, 0, newArray, 0, index);
    System.arraycopy(myData, index + 2, newArray, index, myData.length - index - 2);
    myData = newArray;
  }
}
