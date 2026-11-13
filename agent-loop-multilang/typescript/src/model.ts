// Un simulateur à règles : sa décision dépend des observations reçues.
export interface Message {
  role: "system" | "user" | "assistant" | "tool";
  content: string;
  tool?: string;
}

export interface Action {
  tool: string;
  argument: string;
}

export function simulateLlm(
  messages: Message[],
  catalog: Record<string, { description: string; parameter: string }>,
): Action {
  const prompt = messages[1].content;
  const city = ["Lyon", "Nice", "Paris"].find((name) =>
    new RegExp(`\\b${name}\\b`, "i").test(prompt),
  );
  if (!city) {
    return {
      tool: "terminate",
      argument: "ce scénario ne fait pas partie de l'exercice",
    };
  }
  const observations = new Map(
    messages.filter((m) => m.role === "tool").map((m) => [m.tool, m.content]),
  );
  const weather = observations.get("get_weather");
  const activity = observations.get("find_activity");
  let action: Action;

  if (weather === undefined) {
    action = { tool: "get_weather", argument: city };
  } else if (weather === "inconnue") {
    action = {
      tool: "terminate",
      argument: `Je n'ai pas de météo pour ${city}.`,
    };
  } else if (activity === undefined) {
    const setting = weather === "pluie" ? "indoor" : "outdoor";
    action = { tool: "find_activity", argument: setting };
  } else {
    action = {
      tool: "terminate",
      argument: `À ${city} : ${weather}. Je propose ${activity}.`,
    };
  }

  // Le simulateur connaît ces outils, mais vérifie leur disponibilité.
  if (!Object.hasOwn(catalog, action.tool)) {
    return {
      tool: "terminate",
      argument: `Outil indisponible : ${action.tool}`,
    };
  }
  return action;
}
