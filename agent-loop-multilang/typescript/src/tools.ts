// Des outils fictifs et leur description publique. Aucun accès réseau.
interface Tool {
  description: string;
  parameter: string;
  execute: (argument: string) => string;
}

const weather = new Map([
  ["Lyon", "pluie"],
  ["Nice", "soleil"],
]);

export const tools: Record<string, Tool> = {
  get_weather: {
    description: "Donne la météo fictive d'une ville.",
    parameter: "city",
    execute: (city) => weather.get(city) ?? "inconnue",
  },
  find_activity: {
    description: "Propose une activité indoor ou outdoor.",
    parameter: "setting",
    execute: (setting) =>
      setting === "indoor" ? "un musée" : "une promenade au parc",
  },
  terminate: {
    description: "Termine la conversation avec une réponse.",
    parameter: "answer",
    execute: (answer) => answer,
  },
};
