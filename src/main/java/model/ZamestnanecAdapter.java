package model;

import com.google.gson.*;
import java.lang.reflect.Type;

/**
 * Gson potrebuje vedieť, ktorú podtriedu má vytvoriť pri čítaní JSON-u.
 * Preto ukladáme aj pole "skupina" a podľa neho obnovujeme správny typ.
 */
public class ZamestnanecAdapter implements JsonSerializer<Zamestnanec>, JsonDeserializer<Zamestnanec> {

    @Override
    public JsonElement serialize(Zamestnanec z, Type type, JsonSerializationContext ctx) {
        JsonObject obj = ctx.serialize(z, z.getClass()).getAsJsonObject();
        obj.addProperty("skupina", z.getSkupina());
        return obj;
    }

    @Override
    public Zamestnanec deserialize(JsonElement json, Type type, JsonDeserializationContext ctx)
            throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String skupina = obj.get("skupina").getAsString();
        return switch (skupina) {
            case "Datový analytik"          -> ctx.deserialize(obj, DatovyAnalytik.class);
            case "Bezpečnostný špecialista"  -> ctx.deserialize(obj, BezpecnostnySpecialista.class);
            default -> throw new JsonParseException("Neznáma skupina: " + skupina);
        };
    }
}
