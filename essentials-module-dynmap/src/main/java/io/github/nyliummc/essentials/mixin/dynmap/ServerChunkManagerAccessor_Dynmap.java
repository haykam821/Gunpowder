package io.github.nyliummc.essentials.mixin.dynmap;

import com.mojang.datafixers.util.Either;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.concurrent.CompletableFuture;

@Mixin(ServerChunkManager.class)
public interface ServerChunkManagerAccessor_Dynmap {
    @Invoker
    CompletableFuture<Either<Chunk, ChunkHolder.Unloaded>> invokeGetChunkFuture(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create);
}
