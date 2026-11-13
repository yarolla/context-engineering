/** biome-ignore-all lint/suspicious/noConsole: affichage du kata en ligne de commande */
import { createInterface } from "node:readline/promises";
import { waiting, writeLine } from "./display.js";
import { type Message, simulateLlm } from "./model.js";
import { tools } from "./tools.js";

const MAX_TURNS = 6;
const KEEP_MEMORY = true; // Passer à false pour observer la répétition des appels.

console.log(`Saisissez un prompt demandant au modèle une sortie à Lyon, Nice ou Paris.
C'est une simulation, pas un vrai LLM. Faites une phrase courte contenant le nom de la ville.
Par exemple : « Je veux sortir à Paris ».`);
const terminal = createInterface({
  input: process.stdin,
  output: process.stdout,
});
const prompt = await terminal.question("\n\nSaisissez votre prompt : ");
terminal.close();

const messages: Message[] = [
  {
    role: "system",
    content:
      "Tu es un assistant qui aide à choisir une sortie. Utilise les outils pour connaître la météo et proposer une activité adaptée. Réponds en français.",
  },
  { role: "user", content: prompt },
];
// Le modèle reçoit les descriptions, jamais les fonctions exécutables.
const catalog = Object.fromEntries(
  Object.entries(tools).map(([name, tool]) => [
    name,
    {
      description: tool.description,
      parameter: tool.parameter,
    },
  ]),
);
console.log();
await writeLine("Outils disponibles :");
for (const [name, tool] of Object.entries(catalog)) {
  await writeLine(`  - ${name}(${tool.parameter}) : ${tool.description}`);
}

let turn = 0;
while (true) {
  if (turn >= MAX_TURNS) {
    console.log("\nArrêt de sécurité : nombre maximal de tours atteint.");
    break;
  }
  turn++;

  console.log();
  await waiting(`> [Itération ${turn}] Appel du modèle`);
  const action = simulateLlm(messages, catalog);
  const argument =
    action.tool === "terminate" ? "réponse finale" : action.argument;
  await writeLine(`> [Itération ${turn}] Modèle → ${action.tool}(${argument})`);
  const tool = tools[action.tool];
  if (!Object.hasOwn(tools, action.tool)) {
    console.log("\nArrêt : outil inconnu.");
    break;
  }

  if (action.tool !== "terminate")
    await waiting(`                Exécution de ${action.tool}`);
  const result = tool.execute(action.argument);
  if (action.tool !== "terminate")
    await writeLine(`                Tool → ${result}`);
  if (KEEP_MEMORY) {
    messages.push({
      role: "assistant",
      content: action.argument,
      tool: action.tool,
    });
    messages.push({ role: "tool", content: result, tool: action.tool });
    await writeLine("                Mémoire ← appel et résultat conservés");
  }

  if (action.tool === "terminate") {
    console.log();
    await writeLine(`Réponse du LLM : ${result}`);
    break;
  }
}
