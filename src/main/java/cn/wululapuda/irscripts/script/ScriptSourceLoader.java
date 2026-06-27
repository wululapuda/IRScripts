package cn.wululapuda.irscripts.script;

import cam72cam.mod.resource.Identifier;
import cn.wululapuda.irscripts.IRScripts;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ScriptSourceLoader {
    private static final Map<String, String> SOURCE_CACHE = new ConcurrentHashMap<>();

    private ScriptSourceLoader() {
    }

    public static String getSource(String resourcePath) throws IOException {
        String cached = SOURCE_CACHE.get(resourcePath);
        if (cached != null) {
            return cached;
        }

        Identifier identifier = new Identifier(resourcePath);
        if (!identifier.canLoad()) {
            throw new IOException("Script resource not found: " + resourcePath);
        }

        try (InputStream input = identifier.getResourceStream()) {
            String source = IOUtils.toString(input, StandardCharsets.UTF_8);
            SOURCE_CACHE.put(resourcePath, source);
            IRScripts.logger.debug("Loaded script source: {}", resourcePath);
            return source;
        }
    }

    public static void clearCache() {
        SOURCE_CACHE.clear();
    }
}
