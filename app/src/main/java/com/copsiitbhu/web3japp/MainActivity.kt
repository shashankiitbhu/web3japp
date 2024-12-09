package com.copsiitbhu.web3japp

import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.copsiitbhu.web3japp.MainActivity.Companion.TAG
import com.copsiitbhu.web3japp.ui.theme.Web3jappTheme
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthGetBalance
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.http.HttpService
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.TransactionManager
import org.web3j.tx.Transfer
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.tx.gas.DefaultGasProvider
import org.web3j.utils.Convert
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.security.SecureRandom

class MainActivity : ComponentActivity() {

    companion object {
        private const val INFURA_URL =
            "https://mainnet.infura.io/v3/36c3476d916a4ad5a89c1836ece4a760"
        const val TAG = "Web3jExample"
    }

    lateinit var walletFileName: String
    lateinit var credentials: Credentials
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        var blockNumber : BigInteger
        StrictMode.setThreadPolicy(policy)
//
//        val web3 = Web3j.build(HttpService("http://10.0.2.2:7545"))

        val ensName = "vitalik.eth"

        try {
            val resolvedAddress = ENSResolver.resolveEnsName(ensName, INFURA_URL)
            Log.d(TAG, "Resolved address for $ensName: $resolvedAddress")
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "Error resolving ENS name: ${e.message}")
        }

        try {
//            try {
//                val clientVersion = web3.web3ClientVersion().send()
//                Log.d(TAG, "Connected to Ethereum client version: ${clientVersion.web3ClientVersion}")
//            } catch (e: Exception) {
//                Log.e(TAG, "Error connecting to Ethereum client: ${e.message}")
//                e.printStackTrace()
//            }
//            blockNumber = BigInteger.ZERO
//            web3.ethBlockNumber().sendAsync().thenAccept { ethBlockNumber ->
//                Log.d(TAG, "Current block number: ${ethBlockNumber.blockNumber}")
//                blockNumber = ethBlockNumber.blockNumber
//            }

            //credentials = Credentials.create("0xc429625ae5e603cd4d2a4233aa88bee8f9d3c788db78e7ee2f7c89817b8b1461")

            //deployContract(web3, credentials)  // 1. Deploy contract - Works


            //testAccountNonce(web3, credentials) // 2. Test Account Nonce - Works

            //testGasPrice(web3) // 3. Test Gas Price - Works

            //testBlockDetails(web3, blockNumber) // 4. Test Block Details - Works




        } catch (e: Exception) {
            Log.d(TAG, "Error: ${e.message}")
            e.printStackTrace()
        }

        setContent {
            Web3jappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //WalletOperations(credentials, web3)
                }
            }
        }
    }




}

@Composable
fun WalletOperations(
    credentials: Credentials,
    web3: Web3j,
    modifier: Modifier = Modifier
) {
    var balance by remember { mutableStateOf(BigDecimal.ZERO) }
    var recipientAddress by remember { mutableStateOf("") }
    var sendAmount by remember { mutableStateOf("") }
    var transactionHash by remember { mutableStateOf<String?>(null) }
    var transactionHistory by remember { mutableStateOf(listOf<String>()) }

    fun refreshBalanceAndHistory() {
        balance = getBalance(credentials, web3)
        transactionHistory = getTransactionHistory(credentials)
    }
    LaunchedEffect(Unit) {
        refreshBalanceAndHistory()
    }
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        WalletAddressDisplay(walletAddress = credentials.address)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Balance: $balance ETH")

        Spacer(modifier = Modifier.height(16.dp))

        // Refresh Button
        Button(onClick = { refreshBalanceAndHistory() }) {
            Text("Refresh Balance")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = recipientAddress,
            onValueChange = { recipientAddress = it },
            label = { Text("Recipient Address") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = sendAmount,
            onValueChange = { sendAmount = it },
            label = { Text("Amount to Send (ETH)") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            transactionHash = sendEther(
                credentials,
                web3,
                recipientAddress,
                BigDecimal(sendAmount)
            )
        }) {
            Text("Send Ether")
        }

        transactionHash?.let {
            Text("Transaction Hash: $it")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Transaction History:")
        transactionHistory.forEach { tx ->
            Text(tx)
        }
    }
}

private fun deployContract(web3: Web3j, credentials: Credentials) {
    try {
        val gasProvider = object : ContractGasProvider {
            override fun getGasPrice(contractFunc: String?): BigInteger {
                return DefaultGasProvider.GAS_PRICE
            }

            override fun getGasPrice(): BigInteger {
                return DefaultGasProvider.GAS_PRICE
            }

            override fun getGasLimit(contractFunc: String?): BigInteger {
                return BigInteger.valueOf(300_000) // Set to a safe value for Ganache
            }

            override fun getGasLimit(): BigInteger {
                return BigInteger.valueOf(300_000) // Set to a safe value for Ganache
            }
        }

        val contract: SimpleStorage = SimpleStorage.deploy(
            web3,
            credentials,
            gasProvider,
            BigInteger.valueOf(42) // Initial value for the constructor
        ).send()

        val contractAddress: String = contract.contractAddress
        Log.d(TAG, "Contract deployed at address: $contractAddress")
        Log.d(TAG, "Is contract valid: ${contract.isValid}")
        val transactionManager = createTransactionManager(web3, credentials)

        val loadedContract = loadContractWithTransactionManager(web3, transactionManager, contractAddress)
        Log.d(TAG, "Contract loaded at address: ${loadedContract.contractAddress}")

        // Interact with the loaded contract
        val storedValue = loadedContract.get().send()
        Log.d(TAG, "Stored value in contract: $storedValue")

        val transactionReceipt: TransactionReceipt = contract.set(BigInteger.valueOf(100)).send()
        Log.d(TAG, "Transaction hash: ${transactionReceipt.transactionHash}")

        val storedData: BigInteger = contract.get().send()
        Log.d(TAG, "Stored data is: $storedData")

    } catch (e: Exception) {
        Log.e(TAG, "Error deploying contract: ${e.message}")
        e.printStackTrace()
    }
}


fun getBalance(credentials: Credentials, web3: Web3j): BigDecimal {
    return try {
        val ethGetBalance: EthGetBalance = web3.ethGetBalance(credentials.address, DefaultBlockParameterName.LATEST).send()
        Convert.fromWei(ethGetBalance.balance.toString(), Convert.Unit.ETHER)
    } catch (e: Exception) {
        Log.e(MainActivity.TAG, "Error getting balance: ${e.message}")
        BigDecimal.ZERO
    }
}

fun sendEther(credentials: Credentials, web3: Web3j, toAddress: String, amount: BigDecimal): String? {
    return try {
        val transactionReceipt = Transfer.sendFunds(
            web3, credentials, toAddress, amount, Convert.Unit.ETHER
        ).send()
        transactionReceipt.transactionHash
    } catch (e: Exception) {
        Log.e(MainActivity.TAG, "Error sending Ether: ${e.message}")
        null
    }
}

fun testAccountNonce(web3: Web3j, credentials: Credentials) {
    try {
        val ethGetTransactionCount = web3.ethGetTransactionCount(
            credentials.address,
            DefaultBlockParameterName.LATEST
        ).send()
        val nonce = ethGetTransactionCount.transactionCount
        Log.d(TAG, "Account Nonce: $nonce")
    } catch (e: Exception) {
        Log.e(TAG, "Error retrieving account nonce: ${e.message}")
    }
}

fun testGasPrice(web3: Web3j) {
    try {
        val gasPrice = web3.ethGasPrice().send().gasPrice
        Log.d(TAG, "Current Gas Price: $gasPrice wei")
    } catch (e: Exception) {
        Log.e(TAG, "Error getting gas price: ${e.message}")
    }
}

fun testBlockDetails(web3: Web3j, blockNumber: BigInteger) {
    try {
        val block = web3.ethGetBlockByNumber(
            DefaultBlockParameter.valueOf(blockNumber),
            true
        ).send()

        Log.d(TAG, "Block Number: ${block.block.number}")
        Log.d(TAG, "Block Hash: ${block.block.hash}")
        Log.d(TAG, "Transactions in Block: ${block.block.transactions.size}")
    } catch (e: Exception) {
        Log.e(TAG, "Error retrieving block details: ${e.message}")
    }
}

fun createWallet(userEmail: String, walletDirectory: String, walletRepository: WalletRepository) {
    try {

        val random = SecureRandom()
        val walletPassword = "${userEmail}${random.nextInt(500)}" // Generate a secure password
        val walletFile = File(walletDirectory)
        if (!walletFile.exists()) {
            walletFile.mkdirs()
        }


        val walletName = WalletUtils.generateNewWalletFile(walletPassword, walletFile, false)
        val walletFilePath = "${walletFile.absolutePath}/$walletName"


        val credentials = WalletUtils.loadCredentials(walletPassword, walletFilePath)


        val walletAddress = credentials.address
        val privateKey = credentials.ecKeyPair.privateKey.toString(16) // Convert to hexadecimal


        val walletPasswordEncrypted = EncryptDecrypt.encrypt(walletPassword)
        val privateKeyEncrypted = EncryptDecrypt.encrypt(privateKey)


        val balance = BigDecimal.ZERO
        val currencyName = "Ethereum"
        val currencyAbr = "ETH"


        val wallet = Wallet(
            userEmail = userEmail,
            walletAddress = walletAddress,
            privateKey = privateKeyEncrypted,
            balance = balance,
            currencyName = currencyName,
            currencyAbr = currencyAbr,
            walletJsonFilePath = walletFilePath,
            walletPassword = walletPasswordEncrypted
        )


        walletRepository.save(wallet)

        Log.d(TAG, "Wallet created and saved successfully.")

    } catch (e: Exception) {
        e.printStackTrace()
        Log.e(TAG, "Error creating wallet: ${e.message}")
    }
}



fun getTransactionHistory(credentials: Credentials): List<String> {
    //TODO: I'll implement this later
    return listOf("Tx1", "Tx2", "Tx3") // Placeholder
}

@Composable
fun WalletAddressDisplay(walletAddress: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Log.d(MainActivity.TAG, "Wallet Address: $walletAddress")
        Text(text = "Wallet Address:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = walletAddress,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1
        )
    }
}



private fun createTransactionManager(
    web3: Web3j,
    credentials: Credentials
): TransactionManager {
    return RawTransactionManager(web3, credentials)
}


private fun loadContractWithTransactionManager(
    web3: Web3j,
    transactionManager: TransactionManager,
    contractAddress: String
): SimpleStorage {
    val gasProvider = object : ContractGasProvider {
        override fun getGasPrice(contractFunc: String?): BigInteger {
            return DefaultGasProvider.GAS_PRICE
        }

        override fun getGasPrice(): BigInteger {
            return DefaultGasProvider.GAS_PRICE
        }

        override fun getGasLimit(contractFunc: String?): BigInteger {
            return BigInteger.valueOf(300_000)
        }

        override fun getGasLimit(): BigInteger {
            return BigInteger.valueOf(300_000)
        }
    }

    return SimpleStorage.load(
        contractAddress,
        web3,
        transactionManager,
        gasProvider
    )
}

//TODO : Resolving ENS Names
//TODO : Release web3j android libraries

