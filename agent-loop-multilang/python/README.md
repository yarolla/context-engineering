# La boucle agentique — Python

Python 3.10+. Aucune dépendance externe.

## L'idée

**Proposer une sortie adaptée à la météo d'une ville.**
Tout est fictif : Lyon est sous la pluie, Nice au soleil. Aucun réseau ni clé API.
Le simulateur choisit une activité intérieure ou extérieure **à partir du résultat
renvoyé par l'outil météo**. Il termine ensuite avec l'outil spécial `terminate`.

## Lancer

Depuis ce dossier :

```sh
python3 main.py
```

Le programme vous invite à saisir un prompt :

> Saisissez un prompt demandant au modèle une sortie à Lyon, Nice ou Paris.
> C'est une simulation, pas un vrai LLM. Faites une phrase courte contenant le nom de la ville.
> Par exemple : « Je veux sortir à Paris ».

Saisir une phrase, puis appuyer sur **Entrée**. Exemples à essayer en relançant le programme :

- `Je veux sortir à Lyon`
- `Propose une sortie à NICE`
- `Je veux sortir à paris`
- `Je veux sortir à Bordeaux`

La détection de Lyon, Nice et Paris ne tient pas compte de la casse.
Sans l'une de ces villes, le simulateur répond : « ce scénario ne fait pas partie de l'exercice ».
La phrase complète est conservée dans le message de rôle `user`.

Lyon donne un musée, Nice une promenade au parc. Paris n'a pas de météo connue.
L'affichage sépare le catalogue d'outils envoyé dans le contexte, les itérations
(appel du modèle puis résultat de l'outil) et la **Réponse du LLM** finale.
Cette réponse est l'argument transmis à `terminate` ; un arrêt de sécurité n'en produit pas.

Dans un terminal interactif, le catalogue, chaque itération (appel, résultat et mémoire)
et la réponse finale s'écrivent progressivement. Un indicateur ASCII anime les attentes
du modèle et des outils. Ces délais sont fictifs : aucun vrai appel LLM ni streaming réseau.
Pour désactiver les effets : `NO_ANIMATION=1 python3 main.py`.
Une sortie redirigée vers un fichier désactive également les animations et les délais.

## Trois fichiers à lire

1. **`main.py`** : la boucle, l'historique des messages et les conditions d'arrêt.
2. **`tools.py`** : trois outils, chacun avec une description, un paramètre et une fonction.
3. **`model.py`** : le simulateur choisit son prochain appel en lisant les observations.

Le fichier `display.py` contient seulement les effets d'affichage ; sa lecture est facultative.

Le modèle reçoit un catalogue de descriptions. Le code exécutable reste dans le harness,
le programme qui pilote la boucle. Chaque outil accepte et renvoie une simple chaîne.

## Questions

1. Où est la boucle infinie ? Qu'est-ce qui l'arrête ?
2. Comment le modèle sait-il quels outils sont disponibles ?
3. Qui choisit l'outil ? Qui l'exécute ?
4. Où le résultat d'un outil devient-il disponible pour le tour suivant ?
5. Pourquoi Lyon et Nice provoquent-elles des arguments différents pour `find_activity` ?
6. Pourquoi faut-il trois tours pour proposer une sortie à Lyon ?
7. Quel est le rôle du message `system` commençant par « Tu es un assistant… » ?
8. Pourquoi le harness s'arrête-t-il sur `terminate`, mais pas sur un texte contenant « fin » ?

## Trois petites expériences

Dans `main.py` :

- Passer `KEEP_MEMORY` à `False` : que redemande le modèle ? Pourquoi finit-on par s'arrêter ?
- Remettre la mémoire, puis fixer `MAX_TURNS` à `2` :
  l'activité est trouvée, mais manque-t-il un tour ?

Dans `tools.py`, changer la météo de Lyon en `soleil` et observer la nouvelle décision.

## Petit exercice

Ajouter `get_opening_hours`, qui reçoit une activité et renvoie un horaire fictif.
Ajouter sa description aux outils, puis adapter le simulateur pour demander cet horaire
avant `terminate`. **La boucle doit rester inchangée.**

## Les simplifications à garder en tête

- Le simulateur repère seulement un nom de ville dans la saisie, puis lit les observations.
  Il connaît les noms des outils, mais ne comprend pas le langage comme un vrai LLM.
- `terminate` termine la conversation ; il ne prouve pas que l'objectif est réussi.
  Une réponse finale structurée serait une autre convention possible.
- `MAX_TURNS` est un garde-fou de boucle, pas un circuit breaker de service complet.
- L'historique est la mémoire de cette conversation. Il est conservé par le harness.
- Le catalogue montre les outils disponibles ; ce n'est pas une découverte MCP.
