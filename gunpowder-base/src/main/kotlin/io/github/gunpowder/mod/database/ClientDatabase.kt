package io.github.gunpowder.mod.database

import io.github.gunpowder.api.GunpowderMod
import net.minecraft.util.WorldSavePath
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.sql.Connection

class ClientDatabase : GunpowderDatabaseImpl(), KoinComponent {
    private val mod by inject<GunpowderMod>()

    override fun getDatabase() = Database.connect("jdbc:sqlite:${mod.server.getSavePath(WorldSavePath.ROOT).toFile().absolutePath}/gunpowder.db", "org.sqlite.JDBC")

    init {
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }
}
