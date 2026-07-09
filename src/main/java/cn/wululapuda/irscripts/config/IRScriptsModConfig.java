package cn.wululapuda.irscripts.config;

import cam72cam.mod.config.ConfigFile.Comment;
import cam72cam.mod.config.ConfigFile.File;
import cam72cam.mod.config.ConfigFile.Name;

@Comment("IR Scripts configuration")
@Name("IR Scripts")
@File("irscripts.cfg")
public final class IRScriptsModConfig {
    @Comment("Write script print() output to the server log")
    public static boolean scriptPrint = true;

    @Comment("Enable verbose IR Scripts debug logging on the server")
    public static boolean debug = false;

    @Comment("Show BUTTON-mode script buttons in the stock GUI overlay")
    public static boolean showScriptButtons = true;

    @Comment("Max milliseconds per world tick spent running stock scripts (0 = unlimited)")
    public static int tickBudgetMs = 0;

    private IRScriptsModConfig() {
    }
}
