package model;

public class InitialAdmin extends Account {
	//there's only one initial admin, hence the use of a singleton
	private static InitialAdmin initialAdmin;
	
	//creates and adds the initial admin to the account list
	public static void createAndAdd() {
		if (initialAdmin == null) {
			initialAdmin = new InitialAdmin("Admin", "SCCC", "(631) 123-4567");
			AccountList.getAccountList().add(initialAdmin);
		}
	}

	private InitialAdmin(String username, String password, String phoneNumber) {
		super(username, password, phoneNumber);
		setAdmin(true);
	}
}
