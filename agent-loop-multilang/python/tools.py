"""Des outils fictifs et leur description publique. Aucun accès réseau."""

from dataclasses import dataclass
from typing import Callable


@dataclass
class Tool:
    description: str
    parameter: str
    execute: Callable[[str], str]


TOOLS = {
    "get_weather": Tool(
        "Donne la météo fictive d'une ville.", "city",
        lambda city: {"Lyon": "pluie", "Nice": "soleil"}.get(city, "inconnue"),
    ),
    "find_activity": Tool(
        "Propose une activité indoor ou outdoor.", "setting",
        lambda setting: "un musée" if setting == "indoor" else "une promenade au parc",
    ),
    "terminate": Tool(
        "Termine la conversation avec une réponse.", "answer",
        lambda answer: answer,
    ),
}
