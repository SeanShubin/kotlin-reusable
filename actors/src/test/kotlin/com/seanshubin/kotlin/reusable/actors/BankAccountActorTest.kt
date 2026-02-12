package com.seanshubin.kotlin.reusable.actors

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// Example message types
sealed class BankMessage
data class Deposit(val amount: Int, val replyTo: ActorRef<BankResponse>) : BankMessage()
data class Withdraw(val amount: Int, val replyTo: ActorRef<BankResponse>) : BankMessage()
data class GetBalance(val replyTo: ActorRef<BankResponse>) : BankMessage()

sealed class BankResponse
data class Balance(val amount: Int) : BankResponse()
data class OperationResult(val success: Boolean) : BankResponse()

// Example actor
class BankAccountActor(scope: kotlinx.coroutines.CoroutineScope) : Actor<BankMessage>(scope) {
    private var balance = 0

    override suspend fun onMessage(msg: BankMessage) {
        when (msg) {
            is Deposit -> {
                balance += msg.amount
                msg.replyTo.send(OperationResult(true))
            }
            is Withdraw -> {
                val success = balance >= msg.amount
                if (success) balance -= msg.amount
                msg.replyTo.send(OperationResult(success))
            }
            is GetBalance -> {
                msg.replyTo.send(Balance(balance))
            }
        }
    }
}

// Test client actor that collects responses
class ClientActor(scope: kotlinx.coroutines.CoroutineScope) : Actor<BankResponse>(scope) {
    val responses = mutableListOf<BankResponse>()

    override suspend fun onMessage(msg: BankResponse) {
        responses.add(msg)
    }
}

class BankAccountActorTest {
    @Test
    fun testDepositAndGetBalance() = runBlocking {
        val system = ActorSystem("bank-system-test")

        val client = system.spawn("client") { scope -> ClientActor(scope) }
        val account = system.spawn("account-1") { scope -> BankAccountActor(scope) }

        // Deposit 100
        account.send(Deposit(100, client))
        delay(100) // Allow processing

        // Get balance
        account.send(GetBalance(client))
        delay(100) // Allow processing

        // Verify responses
        val clientActor = system.actorOf<BankResponse>("client") as LocalActorRef<BankResponse>
        val clientActorImpl = clientActor.javaClass.getDeclaredField("actor").apply { isAccessible = true }
            .get(clientActor) as ClientActor

        assertEquals(2, clientActorImpl.responses.size)
        assertTrue(clientActorImpl.responses[0] is OperationResult)
        assertEquals(true, (clientActorImpl.responses[0] as OperationResult).success)
        assertTrue(clientActorImpl.responses[1] is Balance)
        assertEquals(100, (clientActorImpl.responses[1] as Balance).amount)

        system.shutdown()
    }

    @Test
    fun testWithdrawWithSufficientFunds() = runBlocking {
        val system = ActorSystem("bank-system-test-2")

        val client = system.spawn("client") { scope -> ClientActor(scope) }
        val account = system.spawn("account-1") { scope -> BankAccountActor(scope) }

        // Deposit 100
        account.send(Deposit(100, client))
        delay(100)

        // Withdraw 50
        account.send(Withdraw(50, client))
        delay(100)

        // Get balance
        account.send(GetBalance(client))
        delay(100)

        // Verify responses
        val clientActor = system.actorOf<BankResponse>("client") as LocalActorRef<BankResponse>
        val clientActorImpl = clientActor.javaClass.getDeclaredField("actor").apply { isAccessible = true }
            .get(clientActor) as ClientActor

        assertEquals(3, clientActorImpl.responses.size)
        assertEquals(true, (clientActorImpl.responses[1] as OperationResult).success)
        assertEquals(50, (clientActorImpl.responses[2] as Balance).amount)

        system.shutdown()
    }

    @Test
    fun testWithdrawWithInsufficientFunds() = runBlocking {
        val system = ActorSystem("bank-system-test-3")

        val client = system.spawn("client") { scope -> ClientActor(scope) }
        val account = system.spawn("account-1") { scope -> BankAccountActor(scope) }

        // Deposit 30
        account.send(Deposit(30, client))
        delay(100)

        // Attempt to withdraw 50 (should fail)
        account.send(Withdraw(50, client))
        delay(100)

        // Get balance
        account.send(GetBalance(client))
        delay(100)

        // Verify responses
        val clientActor = system.actorOf<BankResponse>("client") as LocalActorRef<BankResponse>
        val clientActorImpl = clientActor.javaClass.getDeclaredField("actor").apply { isAccessible = true }
            .get(clientActor) as ClientActor

        assertEquals(3, clientActorImpl.responses.size)
        assertEquals(false, (clientActorImpl.responses[1] as OperationResult).success)
        assertEquals(30, (clientActorImpl.responses[2] as Balance).amount)

        system.shutdown()
    }
}
