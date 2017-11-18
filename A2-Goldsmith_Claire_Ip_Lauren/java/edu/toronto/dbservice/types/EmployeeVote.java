package edu.toronto.dbservice.types;

import java.io.Serializable;

public class EmployeeVote implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer employeeId;
	private Integer vote;
	
	public EmployeeVote(Integer pEmployeeNum, Integer pVote) {
		employeeId = pEmployeeNum;
		vote = pVote;
	}
	
	public Integer getEmployeeNum() {
		return employeeId;
	}
	
	public Integer getVote() {
		return vote;
	}

}