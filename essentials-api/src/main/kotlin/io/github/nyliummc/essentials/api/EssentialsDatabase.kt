/*
 * MIT License
 *
 * Copyright (c) NyliumMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.nyliummc.essentials.api

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

interface EssentialsDatabase {
    val db: Database

    /**
     * Alternative to Exposed's `transaction`, runs the transaction on a separate thread.
     */
    fun transaction(callback: Consumer<Transaction>) = transaction(callback::accept)
    fun <T> transaction(callback: Transaction.() -> T): CompletableFuture<T>
}
