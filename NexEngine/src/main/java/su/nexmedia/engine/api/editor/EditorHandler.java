package su.nexmedia.engine.api.editor;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

@Deprecated
public interface EditorHandler {

    boolean handle(@NotNull AsyncPlayerChatEvent event);
}
