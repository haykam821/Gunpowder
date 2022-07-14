package io.github.gunpowder.mod

import com.martmists.commons.logging.logger
import io.github.gunpowder.api.*
import io.github.gunpowder.api.builders.CommandBuilderContext
import io.github.gunpowder.api.builders.SidebarBuilderContext
import io.github.gunpowder.api.builders.TextBuilderContext
import io.github.gunpowder.api.events.DatabaseEvents
import io.github.gunpowder.api.ext.ChestGUIOpener
import io.github.gunpowder.api.ext.ColumnHandler
import io.github.gunpowder.builders.CommandBuilderContextImpl
import io.github.gunpowder.builders.SidebarBuilderContextImpl
import io.github.gunpowder.builders.TextBuilderContextImpl
import io.github.gunpowder.exposed.ColumnHandlerImpl
import io.github.gunpowder.gui.ChestGUIOpenerImpl
import io.github.gunpowder.mod.database.GunpowderDatabaseImpl
import io.github.gunpowder.mod.tables.ModuleTable
import net.fabricmc.loader.api.FabricLoader
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

abstract class GunpowderModImpl(database: () -> GunpowderDatabase) : GunpowderMod, KoinComponent {
    private val logger by logger()
    override val modules = mutableListOf<GunpowderModule>()
    override val registry = GunpowderRegistryImpl

    private val koinModule = module {
        // Gunpowder internals
        single<GunpowderMod> { this@GunpowderModImpl }
        singleOf(database)
        single<GunpowderRegistry> { GunpowderRegistryImpl }
        single<GunpowderScheduler> { GunpowderSchedulerImpl(8) }

        // Handlers
        single<ColumnHandler> { ColumnHandlerImpl }
        single<ChestGUIOpener> { ChestGUIOpenerImpl }

        // Builders
        single<CommandBuilderContext> { CommandBuilderContextImpl }
        single<SidebarBuilderContext> { SidebarBuilderContextImpl }
        single<TextBuilderContext> { TextBuilderContextImpl }
    }

    private val database by inject<GunpowderDatabase>()
    private val scheduler by inject<GunpowderScheduler>()

    protected fun onInitialize() {
        startKoin {
            modules(koinModule)
        }

        DatabaseEvents.DATABASE_READY.register {
            database.transaction {
                SchemaUtils.createMissingTablesAndColumns(
                    ModuleTable,
                )
            }

            for (module in modules) {
                if (module.toggleable) {
                    val enabled = database.transactionWait {
                        val entry = ModuleTable.select { ModuleTable.id eq module.name }.firstOrNull()

                        if (entry == null) {
                            ModuleTable.insert {
                                it[id] = module.name
                                it[enabled] = true
                            }
                            true
                        } else {
                            entry[ModuleTable.enabled]
                        }
                    }

                    if (enabled) {
                        module.enabled = true
                        module.onEnable()
                    } else {
                        logger.debug("Module ${module.name} is marked as disabled, not enabling...")
                    }
                } else {
                    module.enabled = true
                    module.onEnable()
                }
            }
        }

        DatabaseEvents.DATABASE_CLOSED.register {
            for (module in modules) {
                if (module.toggleable) {
                    module.enabled = false
                    module.onDisable()
                }
            }
        }

        for (container in FabricLoader.getInstance().getEntrypointContainers("gunpowder:module", GunpowderModule::class.java).sortedBy { it.entrypoint.priority }) {
            val module = container.entrypoint
            modules.add(module)

            println("Loading module ${module.name}")
            logger.info("Loading module ${module.name} from mod ${container.provider.metadata.name} (${container.provider.metadata.id})")
            module.onLoad()
        }

        (database as GunpowderDatabaseImpl).setupListeners()
        (scheduler as GunpowderSchedulerImpl).setupListeners()
    }
}
