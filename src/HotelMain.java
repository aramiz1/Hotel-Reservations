import java.util.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class HotelMain{
	
	//Global reserved rooms array because the array will be continuously marked
	//true and false through multiple methods as well as constantly being
	//written and read from the customers file.
	public static boolean[] reserveRooms = new boolean[15];
	
	public static void main (String[] args){
		//This must be done first, deserialize will read the list for existing
		//bookings and marked them as already booked in the global reserveRooms
		//array. If it is not done then even if a specific type of room such as
		//single is completely booked, the program will still book it if a 
		//new user requests it because the index of that room in the array 
		//is still marked as false.
		List<HotelClass> markRoomsUponStart = new ArrayList<HotelClass>();
		markRoomsUponStart = deserialize();
		
		//For the sake of convenience, I am printing out the list of all current
		//customers in the case of wanting to modify or play with a specific 
		//already existing one.
		System.out.println(markRoomsUponStart);
		
		
		//Pass scanner into the 'home screen' method for the user to begin.
		Scanner scan = new Scanner(System.in);
		firstScreen(scan);

	}
	
	//Method to serialize the HotelClass objects into a file to keep their info after
	//closing the program
	public static void serialize(List<HotelClass> customers) {
      String filename = "customers.ser";

      //Saves the object to file
	    try {
	        FileOutputStream fileOut = new FileOutputStream(filename);
	        ObjectOutputStream out = new ObjectOutputStream(fileOut);
	        out.writeObject(customers);
	        out.close();
	        fileOut.close();
	    } catch (Exception ex) {
	    	ex.printStackTrace();
	    }
	}

	//Deserializes the objects as a list, puts all the customers inside one list.
	public static List<HotelClass> deserialize() {
	    List<HotelClass> customers = new ArrayList<HotelClass>();
	    try {
	        ObjectInputStream in = new ObjectInputStream(new FileInputStream("customers.ser"));
	        customers = (List<HotelClass>) in.readObject();
	        in.close();
	    }
	    //The NullPointerException returns an empty customer list when the customers
	    //file is completely empty and has no other bookings.
	    catch(Exception NullPointerException) {
	    	return customers;
	    }
	    for(int x = 0; x <customers.size();x++){
	    	int roomNumber = customers.get(x).getRoomNumber() - 1;
	    	reserveRooms[roomNumber]=true;
	    	
	    }
	    return customers;
	}
	
	//Welcomes the user, user states if they're a new or recurring customer and goes
	//to the appropriate method based on the number entered.
	public static void firstScreen(Scanner s){
		System.out.println("\nWelcome to the Leaky Cauldron! Press 1 if you "
				+ "already have a reservation, 2 if you are a new customer or 3 to exit the program:");
		String input = s.nextLine();
		if(input.equals("1")){
			login(s);
		}
		else if(input.equals("2")){
			newUser(s);
		}
		else if(input.equals("3")){
			quit();
		}
	}
	
	//Method for recurring customers to get into the dedicated menu method (lobby)
	public static void login(Scanner sc){
		System.out.println("\nWelcome valued customer! Please enter your confirmation ID: ("
				+ "or type 'quit' to exit to the home welcome screen.) ");
		String ID = sc.nextLine();
		if(ID.equals("quit")){
			firstScreen(sc);
		}
		else{
			//Makes a new HotelClass list by accessing the deserialize method 
			//to read info from the file

			List<HotelClass> customers = deserialize();
			
			//If a confirmation number is find it welcomes the customer 
			//with their name and sends them to the lobby menu
			for(int x = 0; x < customers.size(); x++){
				if(customers.get(x).getConfirmationNumber().equals(ID)){
					System.out.println("\nWelcome back, " + customers.get(x).getName());
					lobby(customers.get(x));
				}
			}
			System.out.println("Sorry, we could not find you in our system. We are sending"
					+ " you back to our home screen at this time.");
			firstScreen(sc);
		}
						
	}
	
	//Creates a new HotelClass object based on the user inputs accordingly, then goes to the
	//login method so the user can enter their info there to access the lobby method
	public static void newUser(Scanner sc){
		if(checkIfFull()){
			System.out.println("Sorry, we're completely booked! Please try at another time.");
			quit();
		}
		else{

		System.out.println("\nWelcome new customer! Please type your name to begin: ("
				+ "or type 'quit' to exit to the main menu) ");
		String name = sc.nextLine();
		if(name.equals("quit")){
			firstScreen(sc);
		}
		System.out.println("How old are you?");
		int age = sc.nextInt();
		sc.nextLine();
		System.out.println("What type of room do you want to reserve? (single, double or deluxe)");
		String roomType = sc.nextLine();
		
		String recieveRoomType = checkRoomType(roomType);

		//New customer with properties the user specified, assign room number
		HotelClass h = new HotelClass(name,age,recieveRoomType);
		int room = randomizeRoomNumber(recieveRoomType,h);
		h.setNewRoomNumber(room);
		
		//Mark the rooms array as true for the room number reserved
		reserveRooms[h.getRoomNumber()-1]=true;
		
		//Save the newly created customer object to the file.
		List<HotelClass> idList = deserialize();
		idList.add(h);
		serialize(idList);
		
		System.out.println("\nSuccess! Your confirmation number is: " + h.getConfirmationNumber() +
				"\nPlease login to our system to manage your booking and enjoy your stay. \n");
		login(sc);
		}
		
	}
	
	//This method is used only if all types of a certain room is booked and other types
	//are available. Asks the user if they want to book the other types and updates
	//their information accordingly.
	public static String checkRoomType(String roomType){
		Scanner scan = new Scanner(System.in);
		
		
		if(roomType.equals("single")){

						//singleBooked(), doubleBooked(), deluxeBooked(), are methods
						//that count the number of rooms of a certain type booked. If one
						//type is booked and the customer tries to book another type that is
						//booked they are looped back around to the start of the method
						//to keep searching until they find an empty room or want to quit.
						if(singleBooked()){
						
						System.out.println("Sorry we have no types of that room available."
								+ " Maybe try checking if another type is available?"
								+ " \n Press 1 to check double \n Press 2 to check deluxe \n "
								+ "Press 3 to quit");
						String option = scan.nextLine();
						
						if(option.equals("1")){
							if(doubleBooked()){
								checkRoomType("double");
							}
							else return "double";
						}
						else if(option.equals("2")){
							if(deluxeBooked()){
								checkRoomType("deluxe");
							}
							else return "deluxe";
						}
						else if(option.equals("3")){
							System.out.println("We're sorry to see you go!");
							quit();
							return "quit";
						}
						}
				
			
		}
		else if(roomType.equals("double")){
						
						if(doubleBooked()){
						System.out.println("Sorry we have no types of that room available."
								+ " Maybe try checking if another type is available?"
								+ " \n Press 1 to check single \n Press 2 to check deluxe \n "
								+ "Press 3 to quit");
						String option = scan.nextLine();
						if(option.equals("1")){
							if(singleBooked()){
								checkRoomType("single");
							}
							else return "single";
						}
						else if(option.equals("2")){
							if(deluxeBooked()){
								checkRoomType("deluxe");
							}
							else return "deluxe";
						}
						else if(option.equals("3")){
							System.out.println("We're sorry to see you go!");
							quit();
							return "quit";
						}
				}
			
		}
		else if(roomType.equals("deluxe")){

						if(deluxeBooked()){
						System.out.println("Sorry we have no types of that room available."
								+ " Maybe try checking if another type is available?"
								+ " \n Press 1 to check single \n Press 2 to check double \n "
								+ "Press 3 to quit");
						String option = scan.nextLine();
						if(option.equals("1")){
							if(singleBooked()){
								checkRoomType("single");
							}
							else return "single";
						}
						else if(option.equals("2")){
							if(doubleBooked()){
								checkRoomType("double");
							}
							else return "double";
						}
						else if(option.equals("3")){
							System.out.println("We're sorry to see you go!");
							quit();
							return "quit";
						}
						}
		}
		return roomType;
		
	}
	
	//Randomizes a room number based on the global rooms array and what slots are true
	//and false so as to avoid assigning multiple customers the same room.
	public static int randomizeRoomNumber(String roomType,HotelClass r){
		
		//Exits the program if all rooms are completely booked
		if(checkIfFull()){
			System.out.println("Sorry, we're completely booked! Please try at another time.");
			quit();
		}
		int newNumber = 0;
		do{
			newNumber = r.changeRoomNumber(roomType);
			
		}while((reserveRooms[newNumber-1]==true));
		
		//Sets the new room number if a new room of the desired type is available.
		r.setNewRoomNumber(newNumber);
		
		reserveRooms[newNumber-1] = true;
		return newNumber;

	}
	
	//Here the user can choose from a variety of options based on their booking.
	public static void lobby(HotelClass passUser){
		Scanner scan = new Scanner(System.in);
		
		//Lists out all possible user options
		System.out.println("\nHello! Please read all of the options carefully: \n Press 0 to"
				+  " view your current information \n Press 1 to make a payment \n "
				+ "Press 2 to view your bill \n Press 3 to change your room  \n "
				+ "Press 4 to cancel your booking \n Press 5 to enter your discount code"
				+ "\n Press 6 to quit");
		int entry = scan.nextInt();
		scan.nextLine();
		
		//Prints out customer name, age, room number/type, bill and confirmation ID#
		if(entry == 0){
			System.out.println(passUser.toString());
			anythingElse(scan, passUser);
			
		}
		
		//Customer pays the bill, checks if bill has already been paid or not and displays
		//the appropriate message
		else if(entry == 1){
			
			if(passUser.checkIfPaid()== false){
				System.out.println("\nYour bill of $" + passUser.getBill() + " has been paid. Thank you!");
				passUser.HasPaidAlready("y");
				passUser.eraseBill();
				
				//Remove room entry from the customer file, write an identical entry
				//with the updated bill back in
				List<HotelClass> changeType = deserialize();
				for(int x = 0; x<changeType.size();x++){
					if(changeType.get(x).getConfirmationNumber().equals(passUser.getConfirmationNumber())){
						changeType.remove(x);
						x--;
					}
				}
				changeType.add(passUser);
				serialize(changeType);
			}
			else{
			System.out.println("\nYou have already paid!");
			}
			
			//The anything else method appears at the end of each option to ask the user
			//if they want to do any other input. If so it sends them back to the start
			//of the lobby menu and displays it accordingly.
			anythingElse(scan, passUser);
		}
		
		//Prints out total bill statement, tells user to pay for it by going back into the 
		//dedicated menu
		else if(entry == 2){
			System.out.println("\nYour total charge for your " + passUser.getRoomType() + " room"
					+ " is $" + passUser.getBill() + ". You may pay for it in our payment option in our dedicated menu.");
			anythingElse(scan, passUser);
		}
		
		//If user wants to change their room. Updates room type and bill accordingly.
		else if(entry == 3){
			String change = "";
			
			//3 large if statements for the 3 room types, asks the user to type in 
			//which of the other types they want, 2 more if statements per each type
			//to update their info and send to the updateRoomInMain method.
			if(passUser.getRoomType().equals("single")){
				//User must pay their bill again
				passUser.HasPaidAlready("n");		
		
				System.out.println("\nWhat type of room would you like, double or deluxe?");
				change = scan.nextLine();		
				
				//Changes the room type based on the current room that the customer has.
				//the upateRoomInMain method will handle all of the actual changing
				//and checking if the type requested is booked or not..etc.
				
				if(change.equals("double")){
					String doubleCheck = checkRoomType("double");
					
					if(doubleCheck.equals("double")){
						
						updateRoomInMain(doubleCheck, passUser);
					
					}

					else if(doubleCheck.equals("deluxe")){
						
						updateRoomInMain(doubleCheck, passUser);
					
					}
				}
				else if(change.equals("deluxe")){
					String deluxeCheck = checkRoomType("deluxe");
					
					if(deluxeCheck.equals("deluxe")){
						
					passUser.changeRoomType(deluxeCheck);
					
					}

					else if(deluxeCheck.equals("double")){
						
					passUser.changeRoomType(deluxeCheck);
					
					}
				}
				
			}
			else if(passUser.getRoomType().equals("double")){
				passUser.HasPaidAlready("n");
				System.out.println("\nWhat type of room would you like, single or deluxe?");
				change = scan.nextLine();
				
				passUser.changeRoomType(change);
				if(change.equals("single")){
					String singleCheck = checkRoomType("single");
					
					if(singleCheck.equals("single")){
					
					updateRoomInMain(singleCheck, passUser);
					
					}
					else if(singleCheck.equals("deluxe")){
						
						updateRoomInMain(singleCheck, passUser);
						
					}
				}
				else if(change.equals("deluxe")){
					String deluxeCheck = checkRoomType("deluxe");
					
					if(deluxeCheck.equals("deluxe")){
						
					updateRoomInMain(deluxeCheck, passUser);

					}
					else if(deluxeCheck.equals("single")){
					
						updateRoomInMain(deluxeCheck, passUser);

					}

				}
				
			}
			else if(passUser.getRoomType().equals("deluxe")){
				passUser.HasPaidAlready("n");
				System.out.println("\nWhat type of room would you like, single or double?");
				change = scan.nextLine();
				passUser.changeRoomType(change);
				if(change.equals("single")){
					String singleCheck = checkRoomType("single");
					
					if(singleCheck.equals("single")){
						
						updateRoomInMain(singleCheck, passUser);
						
					
					}
					else if(singleCheck.equals("double")){
						updateRoomInMain(singleCheck, passUser);
						
					}

				}
				else if(change.equals("double")){
					String doubleCheck = checkRoomType("double");
					
					if(doubleCheck.equals("double")){
						
						updateRoomInMain(doubleCheck, passUser);
					
					}
					else if(doubleCheck.equals("single")){
						
						updateRoomInMain(doubleCheck, passUser);

					}

				}
				
			}
			
			//Remove room entry from the customer file, write an identical entry
			//with the updated room type and number back in.
			List<HotelClass> changeType = deserialize();
			for(int x = 0; x<changeType.size();x++){
				if(changeType.get(x).getConfirmationNumber().equals(passUser.getConfirmationNumber())){
					changeType.remove(x);
					x--;
				}
			}
			changeType.add(passUser);
			serialize(changeType);
			
			System.out.println("Your room has been successfully updated. Please"
					+ " go back to our dedicated menu to view your new room number and price");
			anythingElse(scan, passUser);
		}
		
		//Deletes all the user's info and logs them out
		else if(entry == 4){
			System.out.println("\nWe're sorry to see you go.");
			reserveRooms[passUser.getRoomNumber()-1]= false;
		
			//Reads the file, deletes the user based off the confirmation number and writes
			//it back into the file to permanently erase the user.
			List<HotelClass> idList = deserialize();
			for(int x = 0; x<idList.size();x++){
				if(idList.get(x).getConfirmationNumber().equals(passUser.getConfirmationNumber())){
					idList.remove(x);
					x--;
				}
			}
			
			serialize(idList);

			quit();
		}
		
		//Erases bill to zero if the discount code is correct. (Just paying ode to one of the
		//greatest book series of all time).
		else if(entry == 5){
			System.out.println("\nEnter your discount code (you should know where the"
					+ " name of the hotel (The Leaky Cauldron) comes from)!.");
			String bigHint = scan.nextLine();
			if(bigHint.toLowerCase().equals("harry potter")){
				passUser.eraseBill();
				System.out.println("Your room is complementary. Have a nice day!");
			}
			else{
				System.out.println("...");
				quit();
			}
			anythingElse(scan, passUser);
		}
		
		//Logs the user out if they decide they want to quit on straight from the dedicated menu
		else if(entry == 6){
			quit();
		}
	}

	//If a user decides to change their room, it updates the individual
	//HotelClass info accordingly.
	public static void updateRoomInMain(String roomCheck, HotelClass passUser){
		//Set the room they booked to clear
		reserveRooms[passUser.getRoomNumber()-1]= false;
		
		//Change bill and assign new room number
		passUser.changeRoomType(roomCheck);
		passUser.setBill(roomCheck);
		int newRoomNumber = randomizeRoomNumber(roomCheck,passUser);
		passUser.setNewRoomNumber(newRoomNumber);
		
	}

	//This method displays at the end of each user's selection, asking if they want
	//anything else and sending them back to the lobby menu if they do.
	public static void anythingElse(Scanner scan, HotelClass passUser){
		System.out.println("\nIs there anything else we can do for you today? Press 1"
				+ " to go back to our dedicated menu, or press 2 to quit.");
		String option = scan.nextLine();
		if(option.equals("1")){
			lobby(passUser);
		}
		else if(option.equals("2")){
			quit();
		}
	}

	//Checks if all rooms are completely booked or not, gives informs a new user
	//if they are trying to book a room when all are already reserved.
	public static boolean checkIfFull(){
		int count = 0;
		for(int x = 0; x < reserveRooms.length; x++){
			if(reserveRooms[x] == true){
				count++;
				
			}
		}
		if(count==15){
			
			return true;
		}
		else return false;
	}
	
	//Booleans that return true if all types of a certain room are completely booked
	public static boolean singleBooked(){
		int count=0;
		for(int x = 0;x<5;x++){
			if(reserveRooms[x]==true){
				count++;
			}
		}
		if(count==5){
		return true;
		}
		else return false;
	}
	
	public static boolean doubleBooked(){
		int count=0;
		for(int x = 5;x<10;x++){
			if(reserveRooms[x]==true){
				count++;
			}
		}
		if(count==5){
		return true;
		}
		else return false;
	}
	
	public static boolean deluxeBooked(){
		int count=0;
		for(int x = 10;x<15;x++){
			if(reserveRooms[x]==true){
				count++;
			}
		}
		if(count==5){
		return true;
		}
		else return false;
	}
	
	//Quit the program, display a goodbye message to the user
	public static void quit(){
		System.out.println("\nYou have logged out. Have a wonderful day!");
		System.exit(1);
		
	}
}
