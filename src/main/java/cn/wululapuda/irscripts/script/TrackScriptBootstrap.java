package cn.wululapuda.irscripts.script;

import cam72cam.immersiverailroading.registry.DefinitionManager;
import cam72cam.immersiverailroading.util.DataBlock;
import cam72cam.mod.resource.Identifier;
import cn.wululapuda.irscripts.util.ScriptLog;

public final class TrackScriptBootstrap {
    private static boolean scanned = false;

    private TrackScriptBootstrap() {
    }

    public static void scanAllTracks() {
        if (DefinitionManager.getTrackIDs() == null || DefinitionManager.getTrackIDs().isEmpty()) {
            ScriptLog.trackBootstrapWaiting("DefinitionManager has no tracks yet");
            return;
        }

        TrackScriptRegistry.clear();
        int tracksChecked = 0;
        int tracksWithScripts = 0;

        for (String trackId : DefinitionManager.getTrackIDs()) {
            tracksChecked++;
            if (scanTrack(trackId)) {
                tracksWithScripts++;
            }
        }

        scanned = true;
        ScriptLog.trackBootstrapComplete(tracksChecked, tracksWithScripts, TrackScriptRegistry.registeredTrackCount());
    }

    public static void ensureScanned() {
        if (!scanned) {
            scanAllTracks();
        }
    }

    private static boolean scanTrack(String trackId) {
        try {
            Identifier identifier = new Identifier(trackId);
            if (!identifier.canLoad()) {
                identifier = new Identifier(identifier.getDomain(), identifier.getPath().replace(".json", ".caml"));
            }
            if (!identifier.canLoad()) {
                ScriptLog.trackBootstrapMissing(trackId);
                return false;
            }

            int before = TrackScriptRegistry.registeredTrackCount();
            DataBlock block = DataBlock.load(identifier);
            TrackScriptRegistry.parseFromDefinition(trackId, block);
            return TrackScriptRegistry.registeredTrackCount() > before;
        } catch (Exception error) {
            ScriptLog.trackBootstrapFailed(trackId, error);
            return false;
        }
    }
}
