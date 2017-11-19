package edu.toronto.dbservice.types;

import java.io.Serializable;

public class ClientQuery implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
	private Integer account;
	private String item;
	
	public ClientQuery(Integer pid, Integer paccount, String pItem) {
		id = pid;
		item = pItem;
		account=paccount;
	}
	
	public Integer getId() {
		return id;
	}
	
	public String getItem() {
		return item;
	}
	public Integer getAccount() {
		return account;
	}

}