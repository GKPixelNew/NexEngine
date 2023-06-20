package su.nexmedia.engine;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class NexEngineLoader implements PluginLoader {
    @Override
    public void classloader(final @NotNull PluginClasspathBuilder builder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addDependency(new Dependency(new DefaultArtifact("com.zaxxer:HikariCP:5.0.1"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("it.unimi.dsi:fastutil:8.5.11"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.mongodb:mongodb-driver-sync:4.9.1"), null));
        resolver.addRepository(new RemoteRepository.Builder("central", "default", "https://repo1.maven.org/maven2/").build());
        builder.addLibrary(resolver);
    }
}
