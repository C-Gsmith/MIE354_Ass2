package edu.toronto.dbservice.types;

import java.io.Serializable;

public class ArtRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer account;
	private String item;
	private String medium;
	
	public ArtRequest(Integer pAccount, String pItem, String pMedium) {
		account = pAccount;
		item = pItem;
		medium=pMedium;
	}
	
	public Integer getAccount() {
		return account;
	}
	
	public String getItem() {
		return item;
	}
	public String getMedium() {
		return medium;
	}

}