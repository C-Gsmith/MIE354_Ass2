package edu.toronto.dbservice.types;

import java.io.Serializable;

public class EditionsRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer account;
	private String item;
	private Integer specialEds;
	private Integer normalEds;

	public EditionsRequest(Integer pAccount, String pItem, Integer psEds, Integer pnEds) {
		account = pAccount;
		item = pItem;
		specialEds=psEds;
		normalEds=pnEds;
		
	}
	
	public Integer getAccount() {
		return account;
	}
	
	public String getItem() {
		return item;
	}
	public Integer getSpecEds() {
		return specialEds;
	}
	public Integer getNormEds() {
		return normalEds;
	}


}