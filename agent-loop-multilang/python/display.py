"""Effets de présentation uniquement : les délais ne viennent pas d'un vrai LLM."""

import os
import sys
import time

ANIMATE = sys.stdout.isatty() and os.environ.get("NO_ANIMATION") != "1"


def waiting(label: str) -> None:
    if not ANIMATE:
        return
    for frame in "|/-\\|/":
        print(f"\r\033[2K{label} [{frame}]", end="", flush=True)
        time.sleep(0.12)
    print("\r\033[2K", end="", flush=True)


def write_line(text: str) -> None:
    for character in text:
        print(character, end="", flush=True)
        if ANIMATE:
            time.sleep(0.012)
    print()
