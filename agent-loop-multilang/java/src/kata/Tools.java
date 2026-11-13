package kata;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/** Des outils fictifs et leur description publique. Aucun accès réseau. */
public class Tools {
    public record Tool(String description, String parameter, Function<String, String> execute) {}

    public static Map<String, Tool> available() {
        Map<String, String> weather = Map.of("Lyon", "pluie", "Nice", "soleil");
        Map<String, Tool> tools = new LinkedHashMap<>();
        tools.put("get_weather", new Tool(
                "Donne la météo fictive d'une ville.", "city",
                city -> weather.getOrDefault(city, "inconnue")));
        tools.put("find_activity", new Tool(
                "Propose une activité indoor ou outdoor.", "setting",
                setting -> setting.equals("indoor") ? "un musée" : "une promenade au parc"));
        tools.put("terminate", new Tool(
                "Termine la conversation avec une réponse.", "answer",
                answer -> answer));
        return tools;
    }
}
