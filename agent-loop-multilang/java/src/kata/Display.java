package kata;

/** Effets de présentation uniquement : les délais ne viennent pas d'un vrai LLM. */
public class Display {
    static final boolean ANIMATE = System.console() != null
            && !"1".equals(System.getenv("NO_ANIMATION"));

    public static void waiting(String label) throws InterruptedException {
        if (!ANIMATE) return;
        for (char frame : "|/-\\|/".toCharArray()) {
            System.out.print("\r\033[2K" + label + " [" + frame + "]");
            System.out.flush();
            Thread.sleep(120);
        }
        System.out.print("\r\033[2K");
        System.out.flush();
    }

    public static void writeLine(String text) throws InterruptedException {
        for (char character : text.toCharArray()) {
            System.out.print(character);
            System.out.flush();
            if (ANIMATE) Thread.sleep(12);
        }
        System.out.println();
    }
}
