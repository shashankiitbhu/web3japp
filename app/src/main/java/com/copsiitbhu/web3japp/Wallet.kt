package com.copsiitbhu.web3japp

import java.math.BigDecimal

data class Wallet(
    val userEmail: String,
    val walletAddress: String,
    val privateKey: String,
    val balance: BigDecimal,
    val currencyName: String,
    val currencyAbr: String,
    val walletJsonFilePath: String,
    val walletPassword: String
)

interface WalletRepository {
    fun save(wallet: Wallet)
}
