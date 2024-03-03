package utils;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import model.AccountList;
import model.Difficulty;
import model.RouteType;
import model.Trail;
import model.TrailList;

public class Utilities {
	public static void importTrails() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("Data/Trails.csv"));
			String row;
			while ((row = br.readLine()) != null) {
				String[] trailInformation = row.split(",");
				String name = trailInformation[0];
				String startAddress = trailInformation[1];
				String endAddress = trailInformation[2];
				double length = Double.parseDouble(trailInformation[3]);
				int elevation = Integer.parseInt(trailInformation[4]);
				Difficulty difficulty = Difficulty.valueOf(trailInformation[5]);
				RouteType routeType = RouteType.valueOf(trailInformation[6]);
				Trail trail = new Trail(name, startAddress, endAddress, length, elevation, difficulty, routeType);
				TrailList.getTrailList().add(trail);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void backup() {
		try {
			ObjectOutputStream oos1 = new ObjectOutputStream(new FileOutputStream("Backup/Accounts.dat"));
			oos1.writeObject(AccountList.getAccountList());
			oos1.close();
			
			ObjectOutputStream oos2 = new ObjectOutputStream(new FileOutputStream("Backup/Trails.dat"));
			oos2.writeObject(TrailList.getTrailList());
			oos2.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void restore() throws IOException, EOFException {
		try {
			ObjectInputStream ois1 = new ObjectInputStream(new FileInputStream("Backup/Accounts.dat"));
			AccountList.setAccountList((AccountList)ois1.readObject());
			ois1.close();
			
			ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream("Backup/Trails.dat"));
			TrailList.setTrailList((TrailList)ois2.readObject());
			ois2.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
