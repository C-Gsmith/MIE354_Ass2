package mie.ether_example;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCoinbase;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.parity.Parity;
import org.web3j.protocol.parity.methods.response.PersonalUnlockAccount;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.utils.Numeric;

import edu.toronto.dbservice.types.EtherAccount;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;

public class DeployContractServiceTask implements JavaDelegate{
	
	public static final BigInteger FUND_AMOUNT = BigInteger.valueOf(25000000000000000L);
	
	private void fundAccounts(Web3j web3, HashMap<Integer, EtherAccount> accounts, BigInteger amountPerAccount) throws Exception {
		EthCoinbase coinbase = web3.ethCoinbase().sendAsync().get();
		
		for (EtherAccount account : accounts.values()) {
			Credentials credentials = account.getCredentials();
			EthGetTransactionCount transactionCount = web3
					.ethGetTransactionCount(coinbase.getAddress(), DefaultBlockParameterName.LATEST)
					.sendAsync()
					.get();
			
			BigInteger nonce = transactionCount.getTransactionCount();
			
			Transaction aliceMoney = Transaction.createEtherTransaction(
					coinbase.getAddress(), 
					nonce, 
					EtherUtils.GAS_PRICE, 
					EtherUtils.GAS_LIMIT_ETHER_TX.multiply(BigInteger.valueOf(2)), 
					credentials.getAddress(), 
					BigInteger.valueOf(25000000000000000L));
			

			EthSendTransaction sendTransaction = web3.ethSendTransaction(aliceMoney).sendAsync().get();
			TransactionReceipt transactionReceipt =
					web3.ethGetTransactionReceipt(sendTransaction.getTransactionHash()).sendAsync().get().getResult();
			EtherUtils.reportTransaction("Funded account " + credentials.getAddress(), transactionReceipt);
		}
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		// Connect to the blockchain server
		Web3j web3 = Web3j.build(new HttpService());
		
		// Get list of accounts and provide initial funding to each account
		HashMap<Integer, EtherAccount> accounts = (HashMap<Integer, EtherAccount>) execution.getVariable("accounts");
		fundAccounts(web3, accounts, FUND_AMOUNT);
		
		// deploy a new registry contract
		Registry myRegistry = Registry.deploy(web3, accounts.get(0).getCredentials(), EtherUtils.GAS_PRICE, EtherUtils.GAS_LIMIT_CONTRACT_TX, BigInteger.ZERO).get();
		TransactionReceipt deployReceipt = myRegistry.getTransactionReceipt().get();
		EtherUtils.reportTransaction("Contract deployed at " + myRegistry.getContractAddress(), deployReceipt);
		
		// save contract address as a process variable
		execution.setVariable("contractAddress", myRegistry.getContractAddress());
	}

}
