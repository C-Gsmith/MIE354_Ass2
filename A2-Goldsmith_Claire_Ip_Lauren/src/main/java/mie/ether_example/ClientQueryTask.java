package mie.ether_example;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

import edu.toronto.dbservice.config.MIE354DBHelper;
import edu.toronto.dbservice.types.ClientQuery;
import edu.toronto.dbservice.types.ClientRequest;
import edu.toronto.dbservice.types.EtherAccount;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;

public class ClientQueryTask implements JavaDelegate{
	
	Connection dbCon = null;

	public ClientQueryTask() {
		dbCon = MIE354DBHelper.getDBConnection();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		// get current registry request
		ClientQuery currentClientQuery = (ClientQuery) execution.getVariable("currentClientQuery");
		Integer id = currentClientQuery.getId();
		String item = currentClientQuery.getItem();
		
		// connect to the blockchain and load the registry contract
		Web3j web3 = Web3j.build(new HttpService());
		String contractAddress = (String) execution.getVariable("contractAddress");
		HashMap<Integer, EtherAccount> accounts = (HashMap<Integer, EtherAccount>) execution.getVariable("accounts");
		Registry clientRegistry = Registry.load(contractAddress, web3, accounts.get(id).getCredentials(), EtherUtils.GAS_PRICE, EtherUtils.GAS_LIMIT_CONTRACT_TX);
		
		// encode the item key before registering with the contract
		Utf8String encodedItem = new Utf8String(item);
		System.out.println(item + " : " + encodedItem.getValue());
		
		// register the item and report result
		//TransactionReceipt registerReceipt = clientRegistry.register(encodedItem).get();
		//EtherUtils.reportTransaction("Client " + clientNum + " registered " + item, registerReceipt);
		
		//querying back the registered item's owner address and time
		Address ownerAddress = clientRegistry.getOwner(encodedItem).get();
		Uint256 registryTime = clientRegistry.getTime(encodedItem).get();
		
		// decode owner address and time to strings
		String strOwnerAddress = ownerAddress.toString();
		Date decodedTime = new Date(registryTime.getValue().intValue());
		SimpleDateFormat timeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String strTime = timeFormatter.format(decodedTime);
		
		// save record on registration in Registered database table
		String query = "INSERT INTO Results (id, owner) VALUES (?, ?)";
		PreparedStatement preparedStmt = dbCon.prepareStatement(query);
		preparedStmt.setInt (1, id); // field 1 is an int
		preparedStmt.setString (2, strOwnerAddress); // field 2 is a string
		preparedStmt.execute();

	}

}
