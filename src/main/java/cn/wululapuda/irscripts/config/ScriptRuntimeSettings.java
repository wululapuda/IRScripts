package cn.wululapuda.irscripts.config;

public final class ScriptRuntimeSettings {
    private static volatile boolean scriptPrint = true;
    private static volatile boolean debug = false;
    private static volatile boolean showScriptButtons = true;
    private static volatile int tickBudgetMs = 0;

    private ScriptRuntimeSettings() {
    }

    public static void applyFromConfig() {
        scriptPrint = IRScriptsModConfig.scriptPrint;
        debug = IRScriptsModConfig.debug;
        showScriptButtons = IRScriptsModConfig.showScriptButtons;
        tickBudgetMs = Math.max(0, IRScriptsModConfig.tickBudgetMs);
    }

    public static boolean isDebug() {
        if (Boolean.getBoolean("irscripts.debug")) {
            return true;
        }
        return debug;
    }

    public static boolean isScriptPrint() {
        String property = System.getProperty("irscripts.scriptPrint");
        if (property != null) {
            return !"false".equalsIgnoreCase(property);
        }
        return scriptPrint;
    }

    public static boolean isShowScriptButtons() {
        return showScriptButtons;
    }

    public static long getTickBudgetMs() {
        String property = System.getProperty("irscripts.tickBudgetMs");
        if (property != null) {
            try {
                return Math.max(0L, Long.parseLong(property));
            } catch (NumberFormatException ignored) {
                return 0L;
            }
        }
        return tickBudgetMs;
    }
}
