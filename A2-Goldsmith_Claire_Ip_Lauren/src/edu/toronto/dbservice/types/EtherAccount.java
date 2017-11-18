package edu.toronto.dbservice.types;

import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;

public final class EtherAccount implements Serializable {
	private static final long serialVersionUID = 7526472295622776148L;
	
	private String privateKey;
	
	public EtherAccount(String aPrivateKey) {
		privateKey = aPrivateKey;
	}
	
	public Credentials getCredentials(){
		return Credentials.create(privateKey);
	}
}
