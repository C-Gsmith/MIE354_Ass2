package mie.ether_example;

import java.io.Serializable;
import java.math.BigInteger;
import java.nio.file.Files;
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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.toronto.dbservice.config.MIE354DBHelper;
import edu.toronto.dbservice.exceptions.SQLExceptionHandler;
import edu.toronto.dbservice.types.ClientRequest;
import edu.toronto.dbservice.types.EtherAccount;

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
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;

public class SetupServiceTask implements JavaDelegate{
	private static String testRpcCmdTemplate = "cmd /c start /MIN \"TestRpc\" %%AppData%%\\npm\\testrpc.cmd -d --db %s";
	
	Connection dbCon = null;
	
	public static String createBlockchainDir() throws Exception {
		return Files.createTempDirectory("blockchain_").toAbsolutePath().toString();
	}
	
	public static void startTestRpc(String blockchainDirPath) throws Exception {
		String testRpcCmd = String.format(testRpcCmdTemplate, blockchainDirPath);
		Runtime.getRuntime().exec(testRpcCmd);
		Thread.sleep(4000); // sleep to allow server to start
	}

	public SetupServiceTask() {
		dbCon = MIE354DBHelper.getDBConnection();
	}
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		//run TestRpc
		String blockchainDir = createBlockchainDir();
		startTestRpc(blockchainDir);
		System.out.println("TestRPC started");
		System.out.println("Blockchain Directory: " + blockchainDir);
		
		Statement statement;
		ResultSet resultSet = null;
		HashMap<Integer, EtherAccount> accounts = new HashMap<>();
		
		// load accounts from database to a HashMap and store it as a process variable
		statement = dbCon.createStatement();
		resultSet = statement.executeQuery("SELECT * FROM Account");
		while (resultSet.next()) {
			Integer accountId = resultSet.getInt("accountId");
			String privateKey = resultSet.getString("privateKey");
			EtherAccount account = new EtherAccount(privateKey);
			accounts.put(accountId, account);
		}

		execution.setVariable("accounts", accounts);
		
	}

}
