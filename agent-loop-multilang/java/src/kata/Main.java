package kata;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import kata.Model.Message;

public class Main {
    static final int MAX_TURNS = 6;
    static final boolean KEEP_MEMORY = true; // Passer à false pour observer la répétition.

    public static void main(String[] args) throws InterruptedException {
        System.out.println("""
                Saisissez un prompt demandant au modèle une sortie à Lyon, Nice ou Paris.
                C'est une simulation, pas un vrai LLM. Faites une phrase courte contenant le nom de la ville.
                Par exemple : « Je veux sortir à Paris ».""");
        System.out.print("\n\nSaisissez votre prompt : ");
        String prompt = new Scanner(System.in).nextLine();

        var messages = new ArrayList<>(List.of(
                new Message("system", "Tu es un assistant qui aide à choisir une sortie. "
                        + "Utilise les outils pour connaître la météo et proposer une activité adaptée. "
                        + "Réponds en français.", ""),
                new Message("user", prompt, "")));
        var tools = Tools.available();
        // Le modèle reçoit les descriptions, jamais les fonctions exécutables.
        Map<String, Map<String, String>> catalog = new LinkedHashMap<>();
        for (var entry : tools.entrySet()) {
            catalog.put(entry.getKey(), Map.of(
                    "description", entry.getValue().description(),
                    "parameter", entry.getValue().parameter()));
        }
        System.out.println();
        Display.writeLine("Outils disponibles :");
        for (var entry : catalog.entrySet()) {
            Display.writeLine("  - " + entry.getKey() + "(" + entry.getValue().get("parameter")
                    + ") : " + entry.getValue().get("description"));
        }

        int turn = 0;
        while (true) {
            if (turn >= MAX_TURNS) {
                System.out.println("\nArrêt de sécurité : nombre maximal de tours atteint.");
                break;
            }
            turn++;

            System.out.println();
            Display.waiting("> [Itération " + turn + "] Appel du modèle");
            var action = Model.simulateLlm(messages, catalog);
            String argument = action.tool().equals("terminate") ? "réponse finale" : action.argument();
            Display.writeLine("> [Itération " + turn + "] Modèle → " + action.tool() + "(" + argument + ")");
            var tool = tools.get(action.tool());
            if (tool == null) {
                System.out.println("\nArrêt : outil inconnu.");
                break;
            }

            if (!action.tool().equals("terminate")) Display.waiting("                Exécution de " + action.tool());
            String result = tool.execute().apply(action.argument());
            if (!action.tool().equals("terminate")) Display.writeLine("                Tool → " + result);
            if (KEEP_MEMORY) {
                messages.add(new Message("assistant", action.argument(), action.tool()));
                messages.add(new Message("tool", result, action.tool()));
                Display.writeLine("                Mémoire ← appel et résultat conservés");
            }

            if (action.tool().equals("terminate")) {
                System.out.println();
                Display.writeLine("Réponse du LLM : " + result);
                break;
            }
        }
    }
}
