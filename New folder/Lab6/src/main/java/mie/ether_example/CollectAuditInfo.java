package mie.ether_example;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Utf8String;
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

public class CollectAuditInfo implements JavaDelegate{

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		// Connect to blockchain server
		Web3j web3 = Web3j.build(new HttpService());
		
		// load the list of accounts
		HashMap<Integer, EtherAccount> accounts = (HashMap<Integer, EtherAccount>) execution.getVariable("accounts");
		
		// load registry contract based on the process variable contractAddress
		String contractAddress = (String) execution.getVariable("contractAddress");
		Registry myRegistry = Registry.load(contractAddress, web3, accounts.get(0).getCredentials(), EtherUtils.GAS_PRICE, EtherUtils.GAS_LIMIT_CONTRACT_TX);
		
		// Get audited item details
		String selectedAuditItem = (String) execution.getVariable("aAuditedItem");
		Utf8String encodedAuditedItem = new Utf8String(selectedAuditItem);
		Address ownerAddress = myRegistry.getOwner(encodedAuditedItem).get();
		Uint256 registryTime = myRegistry.getTime(encodedAuditedItem).get();
		
		// Convert data to strings
		String strOwnerAddress = ownerAddress.toString();
		Date decodedTime = new Date(registryTime.getValue().intValue());
		SimpleDateFormat timeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String strTime = timeFormatter.format(decodedTime);
		
		// Save audited item details as variable
		execution.setVariable("auditedItemOwner", strOwnerAddress);
		execution.setVariable("auditedItemTime", strTime);
		
		// Print audited item
		System.out.println(" Auditing item" + selectedAuditItem + ": registered by " + ownerAddress.toString() + " at " + strTime);
		
	}
}
