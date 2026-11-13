// Effets de présentation uniquement : les délais ne viennent pas d'un vrai LLM.
import { setTimeout as sleep } from "node:timers/promises";

const ANIMATE = process.stdout.isTTY && process.env.NO_ANIMATION !== "1";

export async function waiting(label: string): Promise<void> {
  if (!ANIMATE) return;
  for (const frame of "|/-\\|/") {
    process.stdout.write(`\r\x1b[2K${label} [${frame}]`);
    await sleep(120);
  }
  process.stdout.write("\r\x1b[2K");
}

export async function writeLine(text: string): Promise<void> {
  for (const character of text) {
    process.stdout.write(character);
    if (ANIMATE) await sleep(12);
  }
  process.stdout.write("\n");
}
