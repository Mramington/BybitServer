import com.example.model.PostgresSmaDcaStrategyRepository
import com.example.model.SmaDcaStrategy
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostgresSmaDcaStrategyRepositoryTest {
    private val repository = PostgresSmaDcaStrategyRepository()

    @BeforeAll
    fun setup() {
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", driver = "org.h2.Driver")

        transaction {
            SchemaUtils.create(com.example.db.SmaDcaStrategyTable)
        }
    }

    @AfterEach
    fun cleanup() {
        transaction {
            com.example.db.SmaDcaStrategyTable.deleteAll()
        }
    }

    @Test
    fun `add and get strategy`() =
        runBlocking {
            val strategy =
                SmaDcaStrategy(
                    userId = "user1",
                    apiKey = "key",
                    apiSecret = "secret",
                    lastOrder = "order1",
                    dcaInterval = "5",
                    qta = "10.0",
                    symbol = "BTCUSDT",
                    interval = "1m",
                    limit = "100",
                )

            repository.addSmaDcaStrategy(strategy)
            val loaded = repository.smaDcaStrategyByUserId("user1")

            assertNotNull(loaded)
            assertEquals("user1", loaded.userId)
            assertEquals("BTCUSDT", loaded.symbol)
        }

    @Test
    fun `update strategy last order`() =
        runBlocking {
            val strategy =
                SmaDcaStrategy(
                    userId = "user2",
                    apiKey = "key2",
                    apiSecret = "secret2",
                    lastOrder = "orderOld",
                    dcaInterval = "10",
                    qta = "5.0",
                    symbol = "ETHUSDT",
                    interval = "5m",
                    limit = "50",
                )

            repository.addSmaDcaStrategy(strategy)
            repository.updateSmaDcaStrategy(strategy, "orderNew")
            val updated = repository.smaDcaStrategyByUserId("user2")

            assertNotNull(updated)
            assertEquals("orderNew", updated.lastOrder)
        }

    @Test
    fun `remove strategy`() =
        runBlocking {
            val strategy =
                SmaDcaStrategy(
                    userId = "user3",
                    apiKey = "key3",
                    apiSecret = "secret3",
                    lastOrder = "order",
                    dcaInterval = "15",
                    qta = "3.0",
                    symbol = "XRPUSDT",
                    interval = "15m",
                    limit = "10",
                )

            repository.addSmaDcaStrategy(strategy)
            val removed = repository.removeSmaDcaStrategy("user3")
            val afterRemove = repository.smaDcaStrategyByUserId("user3")

            assertEquals(true, removed)
            assertNull(afterRemove)
        }

    @Test
    fun `get all strategies by id`() =
        runBlocking {
            val strategy1 =
                SmaDcaStrategy(
                    userId = "user3",
                    apiKey = "key3",
                    apiSecret = "secret3",
                    lastOrder = "order",
                    dcaInterval = "15",
                    qta = "3.0",
                    symbol = "XRPUSDT",
                    interval = "15m",
                    limit = "10",
                )

            val strategy2 =
                SmaDcaStrategy(
                    userId = "user3",
                    apiKey = "key2",
                    apiSecret = "secret2",
                    lastOrder = "orderOld",
                    dcaInterval = "10",
                    qta = "5.0",
                    symbol = "ETHUSDT",
                    interval = "5m",
                    limit = "50",
                )

            repository.addSmaDcaStrategy(strategy1)
            repository.addSmaDcaStrategy(strategy2)
            val all = repository.allSmaDcaStrategyByUserId("user3")

            assertEquals(all.count(), 2)
            assertEquals(all[0].userId, "user3")
        }
}
