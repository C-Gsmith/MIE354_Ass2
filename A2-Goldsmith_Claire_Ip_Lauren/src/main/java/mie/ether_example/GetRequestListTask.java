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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.web3j.abi.datatypes.Address;
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

import edu.toronto.dbservice.config.MIE354DBHelper;
import edu.toronto.dbservice.types.ClientRequest;
import edu.toronto.dbservice.types.EtherAccount;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;

public class GetRequestListTask implements JavaDelegate{

	Connection dbCon = null;

	public GetRequestListTask() {
		dbCon = MIE354DBHelper.getDBConnection();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		// Loading the contract
		Web3j web3 = Web3j.build(new HttpService());
		String contractAddress = (String) execution.getVariable("contractAddress");
		HashMap<Integer, EtherAccount> accounts = (HashMap<Integer, EtherAccount>) execution.getVariable("accounts");
		Registry myRegistry = Registry.load(contractAddress, web3, accounts.get(0).getCredentials(), EtherUtils.GAS_PRICE, EtherUtils.GAS_LIMIT_CONTRACT_TX);
		
		// Selecting the registry request list from the data table
		Statement statement;
		ResultSet resultSet = null;
		List<ClientRequest> clientRequestList = new ArrayList<>();
		
		statement = dbCon.createStatement();
		resultSet = statement.executeQuery("SELECT * FROM Request");
		while (resultSet.next()) {
			Integer accountId = resultSet.getInt("account");
			String item = resultSet.getString("item");
			ClientRequest clientRequest = new ClientRequest(accountId, item);
			clientRequestList.add(clientRequest);
		}
		resultSet.close();
		
		// Saving the list of registry requests as a process variable
		execution.setVariable("clientRequestList", clientRequestList);
	}

}
