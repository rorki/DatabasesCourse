package de.dis2011;

import de.dis2011.dao.ApartmentJDBC;
import de.dis2011.dao.EntryDAO;
import de.dis2011.dao.EstateAgentDAO;
import de.dis2011.dao.EstateAgentJDBC;
import de.dis2011.dao.HouseJDBC;
import de.dis2011.data.Apartment;
import de.dis2011.data.EstateAgent;
import de.dis2011.data.House;

/**
 * Hauptklasse
 */
public class Main {
	private static EstateAgentDAO agentsDAO;
	private static EntryDAO<House> housesDAO;
	private static EntryDAO<Apartment> aptDAO;

	private static EstateAgent authentifiedAgent = null;
	private static boolean authentifiedAsAdmin = false;

	public static void main(String[] args) {
		agentsDAO = new EstateAgentJDBC();
		housesDAO = new HouseJDBC();
		aptDAO = new ApartmentJDBC();

		showMainMenu();
	}

	/**
	 * Zeigt das Hauptmenü
	 */
	public static void showMainMenu() {
		// Menüoptionen
		final int MENU_AGENT = 0;
		final int MENU_ESTATE = 1;
		final int QUIT = 2;

		// Erzeuge Menü
		Menu mainMenu = new Menu("Main Menu");
		mainMenu.addEntry("Manage Estate Agents", MENU_AGENT);
		mainMenu.addEntry("Manage Estates", MENU_ESTATE);
		mainMenu.addEntry("Quit", QUIT);

		// Verarbeite Eingabe
		while (true) {
			int response = mainMenu.show();

			switch (response) {
			case MENU_AGENT:
				showAgentMenu();
				break;
			case MENU_ESTATE:
				showEstateMenu();
				break;
			case QUIT:
				return;
			}
		}
	}

	public static void showEstateMenu() {
		final int LOGIN = 0;
		final int HOUSES = 1;
		final int APARTMENTS = 2;
		final int BACK = 3;

		Menu maklerMenu = new Menu("Manage Estates");
		maklerMenu.addEntry("Login", LOGIN);
		maklerMenu.addEntry("Manage houses", HOUSES);
		maklerMenu.addEntry("Manage apartments", APARTMENTS);
		maklerMenu.addEntry("Back to main menu", BACK);

		while (true) {
			int response = maklerMenu.show();
			if (response != LOGIN && response != BACK && authentifiedAgent == null) {
				System.out.println("Please, login as agent to manages estates");
				continue;
			}

			switch (response) {
			case LOGIN:
				showAgentLogin();
				break;
			case HOUSES:
				showHousesMenu();
				break;
			case APARTMENTS:
				showApartmentsMenu();
				break;
			case BACK:
				return;
			}
		}
	}

	private static void showAgentLogin() {
		String login = FormUtil.readString("Login");
		String password = FormUtil.readString("Password");

		EstateAgent agent = new EstateAgent();
		agent.setLogin(login);
		agentsDAO.loadByLogin(agent);

		if (password.equals(agent.getPassword())) {
			authentifiedAgent = agent;
			System.out.println("Hello " + authentifiedAgent.getName() + "!");
		} else {
			authentifiedAgent = null;
			System.out.println("Sorry! Bad password or login");
		}
	}

	private static void showApartmentsMenu() {
		final int NEW_APT = 0;
		final int DELETE_APT = 1;
		final int UPDATE_APT = 2;
		final int BACK = 3;

		Menu menu = new Menu("Manage apartments");
		menu.addEntry("New apartment", NEW_APT);
		menu.addEntry("Delete apartment", DELETE_APT);
		menu.addEntry("Update apartment", UPDATE_APT);
		menu.addEntry("Back to estates menu", BACK);

		// Verarbeite Eingabe
		while (true) {
			int response = menu.show();

			switch (response) {
			case NEW_APT:
				newApt();
				break;
			case DELETE_APT:
				deleteApt();
				break;
			case UPDATE_APT:
				updateApt();
				break;
			case BACK:
				return;
			}
		}
	}

	public static void showHousesMenu() {
		final int NEW_HOUSE = 0;
		final int DELETE_HOUSE = 1;
		final int UPDATE_HOUSE = 2;
		final int ALL = 3;
		final int BACK = 4;

		Menu menu = new Menu("Manage houses");
		menu.addEntry("New house", NEW_HOUSE);
		menu.addEntry("Delete house", DELETE_HOUSE);
		menu.addEntry("Update house", UPDATE_HOUSE);
		menu.addEntry("Show all managed by me", ALL);
		menu.addEntry("Back to estates Menu", BACK);

		// Verarbeite Eingabe
		while (true) {
			int response = menu.show();

			switch (response) {
			case NEW_HOUSE:
				newHouse();
				break;
			case DELETE_HOUSE:
				deleteHouse();
				break;
			case UPDATE_HOUSE:
				updateHouse();
				break;
			case ALL:
				showAllMyHouses();
				break;
			case BACK:
				return;
			}
		}
	}

	public static void showAgentMenu() {
		// Menüoptionen
		final int LOGIN = 0;
		final int NEW_AGENT = 1;
		final int DELETE_AGENT = 2;
		final int UPDATE_AGENT = 3;
		final int BACK = 4;

		// Maklerverwaltungsmenü
		Menu menu = new Menu("Manage estate agents");
		menu.addEntry("Login", LOGIN);
		menu.addEntry("New agent", NEW_AGENT);
		menu.addEntry("Delete agent", DELETE_AGENT);
		menu.addEntry("Update agent", UPDATE_AGENT);
		menu.addEntry("Back to main menu", BACK);

		// Verarbeite Eingabe
		while (true) {
			int response = menu.show();
			if (response != LOGIN && response != BACK && !authentifiedAsAdmin) {
				System.out.println("Please, login as root to manage agents");
				continue;
			}

			switch (response) {
			case LOGIN:
				showAdminLogin();
				break;
			case NEW_AGENT:
				newAgent();
				break;
			case DELETE_AGENT:
				deleteAgent();
				break;
			case UPDATE_AGENT:
				updateAgent();
				break;
			case BACK:
				return;
			}
		}
	}

	private static void showAdminLogin() {
		String login = FormUtil.readString("Login");
		String password = FormUtil.readString("Password");

		authentifiedAsAdmin = login.equals("root") && password.equals("root");

		if (authentifiedAsAdmin) {
			System.out.println("Congrats! You are logged in now");
		} else {
			System.out.println("Sorry! Bad password or login");
		}
	}

	public static void newAgent() {
		EstateAgent m = new EstateAgent();

		m.setName(FormUtil.readString("Name"));
		m.setAddress(FormUtil.readString("Adresse"));
		m.setLogin(FormUtil.readString("Login"));
		m.setPassword(FormUtil.readString("Passwort"));
		agentsDAO.insert(m);

		System.out.println("Agent with ID " + m.getId() + " was added");
	}
	
	private static void updateAgent() {
		EstateAgent m = new EstateAgent();

		m.setId(FormUtil.readInt("ID"));
		m.setName(FormUtil.readString("Name"));
		m.setAddress(FormUtil.readString("Adresse"));
		m.setLogin(FormUtil.readString("Login"));
		m.setPassword(FormUtil.readString("Passwort"));
		agentsDAO.update(m);

		System.out.println("Agent with ID " + m.getId() + " was updated");
	}

	private static void deleteAgent() {
		EstateAgent m = new EstateAgent();
		m.setId(FormUtil.readInt("ID"));
		agentsDAO.delete(m);
		System.out.println("Agent with ID " + m.getId() + " was deleted");
	}

	public static void newHouse() {
		House m = new House();

		m.setCity(FormUtil.readString("City"));
		m.setPostalCode(FormUtil.readString("Postal Code"));
		m.setStreet(FormUtil.readString("Street"));
		m.setStreetNum(FormUtil.readString("StreetNum"));
		m.setSqArea(FormUtil.readDouble("Area"));
		m.setFloors(FormUtil.readInt("Floors"));
		m.setPrice(FormUtil.readDouble("Price"));
		m.setGarden(FormUtil.readBoolean("Garden (y/n)"));

		housesDAO.insert(m);
		System.out.println("House with ID " + m.getId() + " was added");
	}

	private static void updateHouse() {
		House m = new House();

		m.setId(FormUtil.readInt("ID"));
		m.setCity(FormUtil.readString("City"));
		m.setPostalCode(FormUtil.readString("Postal Code"));
		m.setStreet(FormUtil.readString("Street"));
		m.setStreetNum(FormUtil.readString("StreetNum"));
		m.setSqArea(FormUtil.readDouble("Area"));
		m.setFloors(FormUtil.readInt("Floors"));
		m.setPrice(FormUtil.readDouble("Price"));
		m.setGarden(FormUtil.readBoolean("Garden (y/n)"));

		housesDAO.update(m);
		System.out.println("House with ID " + m.getId() + " was updated");
	}

	private static void deleteHouse() {
		House m = new House();
		m.setId(FormUtil.readInt("ID"));

		housesDAO.delete(m);
		System.out.println("House with ID " + m.getId() + " was deleted");
	}

	private static void showAllMyHouses() {
		// TODO Auto-generated method stub
	}

	private static void updateApt() {
		Apartment m = new Apartment();

		m.setId(FormUtil.readInt("ID"));
		m.setCity(FormUtil.readString("City"));
		m.setPostalCode(FormUtil.readString("Postal Code"));
		m.setStreet(FormUtil.readString("Street"));
		m.setStreetNum(FormUtil.readString("StreetNum"));
		m.setSqArea(FormUtil.readDouble("Area"));
		m.setFloor(FormUtil.readInt("Floor"));
		m.setRent(FormUtil.readDouble("Rent"));
		m.setRooms(FormUtil.readInt("Rooms"));
		m.setBalcony(FormUtil.readBoolean("Balcony(y/n)"));
		m.setBuiltinKitchen(FormUtil.readBoolean("Built-in Kitchen(y/n)"));

		aptDAO.update(m);
		System.out.println("Apartment with ID " + m.getId() + " was updated");

	}

	private static void deleteApt() {
		Apartment m = new Apartment();
		m.setId(FormUtil.readInt("ID"));

		aptDAO.delete(m);
		System.out.println("Appartment with ID " + m.getId() + " was deleted");

	}

	private static void newApt() {
		Apartment m = new Apartment();

		m.setCity(FormUtil.readString("City"));
		m.setPostalCode(FormUtil.readString("Postal Code"));
		m.setStreet(FormUtil.readString("Street"));
		m.setStreetNum(FormUtil.readString("StreetNum"));
		m.setSqArea(FormUtil.readDouble("Area"));
		m.setFloor(FormUtil.readInt("Floor"));
		m.setRent(FormUtil.readDouble("Rent"));
		m.setRooms(FormUtil.readInt("Rooms"));
		m.setBalcony(FormUtil.readBoolean("Balcony(y/n)"));
		m.setBuiltinKitchen(FormUtil.readBoolean("Built-in Kitchen(y/n)"));

		aptDAO.insert(m);
		System.out.println("Apartment with ID " + m.getId() + " was added");
	}
}
