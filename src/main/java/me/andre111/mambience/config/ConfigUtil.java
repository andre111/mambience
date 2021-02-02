package me.andre111.mambience.config;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import me.andre111.mambience.MALogger;
import me.andre111.mambience.condition.Condition;
import me.andre111.mambience.condition.Parser;

public class ConfigUtil {
	public static List<Condition> loadConditions(MALogger logger, JsonArray array) {
		List<Condition> conditions = new ArrayList<>();
		for(int i=0; i<array.size(); i++) {
			//TODO: remove: ignore toggles
			if(array.get(i).getAsJsonObject().get("condition").getAsString().equals("TOGGLE")) continue;
			
			Condition condition = loadCondition(logger, array.get(i).getAsJsonObject());
			if(condition != null) {
				conditions.add(condition);
			} else {
				logger.log("Warning: Ignored unknown condition: "+array.get(i));
			}
		}
		return conditions;
	}
	
	private static Condition loadCondition(MALogger logger, JsonObject obj) {
		String name = getString(obj, "condition", "");
		String stringValue = getString(obj, "stringValue", "");
		float floatValue = getFloat(obj, "floatValue", 0);
		return Parser.parse(name, stringValue, floatValue);
	}
	
	public static String getString(JsonObject obj, String memberName, String defaultValue) {
		return obj.has(memberName) ? obj.get(memberName).getAsString() : defaultValue;
	}
	public static double getDouble(JsonObject obj, String memberName, float defaultValue) {
		return obj.has(memberName) ? obj.get(memberName).getAsDouble() : defaultValue;
	}
	public static float getFloat(JsonObject obj, String memberName, float defaultValue) {
		return obj.has(memberName) ? obj.get(memberName).getAsFloat() : defaultValue;
	}
	public static int getInt(JsonObject obj, String memberName, int defaultValue) {
		return obj.has(memberName) ? obj.get(memberName).getAsInt() : defaultValue;
	}
	
	public static String[] getStringArray(JsonObject obj, String memberName, String[] defaultValue) {
		if(obj.has(memberName) && obj.get(memberName).isJsonArray()) {
			JsonArray jsonArray = obj.get(memberName).getAsJsonArray();
			String[] stringArray = new String[jsonArray.size()];
			for(int i=0; i<jsonArray.size(); i++) {
				stringArray[i] = jsonArray.get(i).getAsString();
			}
			return stringArray;
		}
		return defaultValue;
	}
}
