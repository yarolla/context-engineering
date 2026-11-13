"""Un simulateur à règles : sa décision dépend des observations reçues."""

import re
from dataclasses import dataclass


@dataclass
class Message:
    role: str
    content: str
    tool: str = ""


@dataclass
class Action:
    tool: str
    argument: str


def simulate_llm(messages: list[Message], catalog: dict) -> Action:
    prompt = messages[1].content
    city = next((name for name in ("Lyon", "Nice", "Paris")
                 if re.search(rf"\b{name}\b", prompt, re.IGNORECASE)), None)
    if city is None:
        return Action("terminate", "ce scénario ne fait pas partie de l'exercice")
    observations = {m.tool: m.content for m in messages if m.role == "tool"}
    weather = observations.get("get_weather")
    activity = observations.get("find_activity")

    if weather is None:
        action = Action("get_weather", city)
    elif weather == "inconnue":
        action = Action("terminate", f"Je n'ai pas de météo pour {city}.")
    elif activity is None:
        setting = "indoor" if weather == "pluie" else "outdoor"
        action = Action("find_activity", setting)
    else:
        action = Action("terminate", f"À {city} : {weather}. Je propose {activity}.")

    # Le simulateur connaît ces outils, mais vérifie leur disponibilité.
    if action.tool not in catalog:
        return Action("terminate", f"Outil indisponible : {action.tool}")
    return action
