package model;

import java.io.Serializable;
import java.util.TreeMap;

public class AccountList implements Serializable {
	//username is used as the key value for inserting and searching for accounts
	private TreeMap<String, Account> accounts;
	
	private static AccountList accountList;
	
	public static AccountList getAccountList() {
		if (accountList == null) {
			accountList = new AccountList();
		}
		return accountList;
	}
	
	public static void setAccountList(AccountList accountList) {
		AccountList.accountList = accountList;
	}
	
	private AccountList() {
		accounts = new TreeMap<>();
	}
	
	public boolean add(Account account) {
		//if the account was added successfully, aka username was unique
		if(accounts.putIfAbsent(account.getUsername().toLowerCase(), account) == null) {
			return true;
		}
		return false;
	}
	
	public Account search(String username) {
		return accounts.get(username);
	}
	
	public boolean isEmpty() {
		return accounts.isEmpty();
	}
	
	//for debugging
	public void display() {
		System.out.println(accounts.toString());
	}
}
