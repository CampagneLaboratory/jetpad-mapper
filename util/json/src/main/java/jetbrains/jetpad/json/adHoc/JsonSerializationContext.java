package jetbrains.jetpad.json.adHoc;

import jetbrains.jetpad.json.JsonArray;
import jetbrains.jetpad.json.JsonBoolean;
import jetbrains.jetpad.json.JsonNull;
import jetbrains.jetpad.json.JsonNumber;
import jetbrains.jetpad.json.JsonObject;
import jetbrains.jetpad.json.JsonString;
import jetbrains.jetpad.json.JsonValue;

import java.util.HashMap;
import java.util.Map;

public class JsonSerializationContext {
  private Map<Class, Byte> myRegistrations = new HashMap<Class, Byte>();
  private Map<Byte, Serializer> mySerializers = new HashMap<Byte, Serializer>();

  public JsonSerializationContext() {
    Serializers.IntegerSerializer integerSerializer = new Serializers.IntegerSerializer();
    register(Integer.class, integerSerializer);

    Serializers.StringSerializer stringSerializer = new Serializers.StringSerializer(integerSerializer);
    register(String.class, stringSerializer);

    register(JsonNull.class, new Serializers.NullSerializer());
    register(JsonBoolean.class, new Serializers.BooleanSerializer());

    register(JsonString.class, new Serializers.JsonStringSerializer(stringSerializer));
    register(JsonNumber.class, new Serializers.NumberSerializer(integerSerializer, new Serializers.DoubleSerializer()));

    register(JsonObject.class, new Serializers.ObjectSerializer(this));
    register(JsonArray.class, new Serializers.ArraySerializer(this));
  }

  <T> void register(Class<T> type, Serializer<T> serializer) {
    if (myRegistrations.containsKey(type)) throw new IllegalStateException("double register serializer for " + type);

    byte id = serializer.hasId() ? serializer.getId() : (byte) myRegistrations.size();
    if (mySerializers.containsKey(id)) throw new IllegalStateException("duplicate serializer id " + id);

    myRegistrations.put(type, id);
    mySerializers.put(id, serializer);
  }

  public JsonValue read(byte[] input) {
    Serializer serializer = getSerializer(input, 0);
    JsonValue value = (JsonValue) serializer.read(input, 1);
    if (serializer.getPosition() != input.length) throw new JsonSerializationException();
    return value;
  }

  public byte[] write(JsonValue value) {
    Byte type = myRegistrations.get(value.getClass());
    if (type == null) throw new JsonSerializationException("unregistered class " + value.getClass());
    return mySerializers.get(type).write(value);
  }

  Serializer getSerializer(byte[] input, int offset) {
    byte type = input[offset];
    if (!mySerializers.containsKey(type)) throw new JsonSerializationException("offset=" + offset + ", type=" + type);
    return mySerializers.get(type);
  }

  <T> Serializer<T> getSerializer(Class<T> type) {
    return mySerializers.get(myRegistrations.get(type));
  }
}
