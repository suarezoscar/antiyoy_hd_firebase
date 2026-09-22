package yio.tro.onliyoy.net.firebase;

/**
 * Construcción de los cuerpos JSON de Firestore REST.
 * <p>
 * Los valores van "tipados": { "fields": { "campo": { "stringValue": "..." } } }.
 * Métodos estáticos mínimos para componer strings (sin librería JSON externa,
 * los números/timestamps se pasan ya formateados por quien llama).
 */
public final class FirestoreJson {

    private FirestoreJson() {
    }

    public static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /** {"key":{"stringValue":".."}} sin comas; úsalo con join(). */
    public static String str(String key, String value) {
        return "\"" + key + "\":{\"stringValue\":\"" + escape(value) + "\"}";
    }

    public static String num(String key, String value) {
        return "\"" + key + "\":{\"integerValue\":\"" + escape(value) + "\"}";
    }

    public static String bool(String key, boolean value) {
        return "\"" + key + "\":{\"booleanValue\":" + value + "}";
    }

    /** {"key":{"mapValue":{"fields":{...}}}} */
    public static String map(String key, String innerFieldsJson) {
        return "\"" + key + "\":{\"mapValue\":{\"fields\":{" + innerFieldsJson + "}}}";
    }

    /** {"key":{"arrayValue":{"values":[...]}}} */
    public static String array(String key, String valuesJson) {
        return "\"" + key + "\":{\"arrayValue\":{\"values\":[" + valuesJson + "]}}";
    }

    /** Esqueleto de un elemento de mapa dentro de un array. */
    public static String mapElement(String innerFieldsJson) {
        return "{\"mapValue\":{\"fields\":{" + innerFieldsJson + "}}}";
    }

    /** Envuelve los campos dentro de "fields":{...}. */
    public static String fields(String fieldsJson) {
        return "{\"fields\":{" + fieldsJson + "}}";
    }

    public static String join(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(parts[i]);
        }
        return sb.toString();
    }
}