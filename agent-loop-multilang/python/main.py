from display import waiting, write_line
from model import Message, simulate_llm
from tools import TOOLS

MAX_TURNS = 6
KEEP_MEMORY = True  # Passer à False pour observer la répétition des appels.

print("""Saisissez un prompt demandant au modèle une sortie à Lyon, Nice ou Paris.
C'est une simulation, pas un vrai LLM. Faites une phrase courte contenant le nom de la ville.
Par exemple : « Je veux sortir à Paris ».""")
prompt = input("\n\nSaisissez votre prompt : ")

messages = [
    Message("system", "Tu es un assistant qui aide à choisir une sortie. "
            "Utilise les outils pour connaître la météo et proposer une activité adaptée. "
            "Réponds en français."),
    Message("user", prompt),
]
# Le modèle reçoit les descriptions, jamais les fonctions exécutables.
catalog = {
    name: {"description": tool.description, "parameter": tool.parameter}
    for name, tool in TOOLS.items()
}
print()
write_line("Outils disponibles :")
for name, tool in catalog.items():
    write_line(f"  - {name}({tool['parameter']}) : {tool['description']}")

turn = 0
while True:
    if turn >= MAX_TURNS:
        print("\nArrêt de sécurité : nombre maximal de tours atteint.")
        break
    turn += 1

    print()
    waiting(f"> [Itération {turn}] Appel du modèle")
    action = simulate_llm(messages, catalog)
    argument = "réponse finale" if action.tool == "terminate" else action.argument
    write_line(f"> [Itération {turn}] Modèle → {action.tool}({argument})")
    tool = TOOLS.get(action.tool)
    if tool is None:
        print("\nArrêt : outil inconnu.")
        break

    if action.tool != "terminate":
        waiting(f"                Exécution de {action.tool}")
    result = tool.execute(action.argument)
    if action.tool != "terminate":
        write_line(f"                Tool → {result}")
    if KEEP_MEMORY:
        messages.append(Message("assistant", action.argument, action.tool))
        messages.append(Message("tool", result, action.tool))
        write_line("                Mémoire ← appel et résultat conservés")

    if action.tool == "terminate":
        print()
        write_line(f"Réponse du LLM : {result}")
        break
