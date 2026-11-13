package kata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/** Un simulateur à règles : sa décision dépend des observations reçues. */
public class Model {
    public record Message(String role, String content, String tool) {}
    public record Action(String tool, String argument) {}

    public static Action simulateLlm(List<Message> messages, Map<String, Map<String, String>> catalog) {
        String prompt = messages.get(1).content();
        String city = null;
        for (String name : List.of("Lyon", "Nice", "Paris")) {
            if (Pattern.compile("\\b" + name + "\\b", Pattern.CASE_INSENSITIVE).matcher(prompt).find()) {
                city = name;
                break;
            }
        }
        if (city == null) {
            return new Action("terminate", "ce scénario ne fait pas partie de l'exercice");
        }
        Map<String, String> observations = new HashMap<>();
        for (Message message : messages) {
            if (message.role().equals("tool")) observations.put(message.tool(), message.content());
        }
        String weather = observations.get("get_weather");
        String activity = observations.get("find_activity");
        Action action;

        if (weather == null) {
            action = new Action("get_weather", city);
        } else if (weather.equals("inconnue")) {
            action = new Action("terminate", "Je n'ai pas de météo pour " + city + ".");
        } else if (activity == null) {
            String setting = weather.equals("pluie") ? "indoor" : "outdoor";
            action = new Action("find_activity", setting);
        } else {
            action = new Action("terminate", "À " + city + " : " + weather + ". Je propose " + activity + ".");
        }

        // Le simulateur connaît ces outils, mais vérifie leur disponibilité.
        if (!catalog.containsKey(action.tool())) {
            return new Action("terminate", "Outil indisponible : " + action.tool());
        }
        return action;
    }
}
