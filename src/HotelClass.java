import java.util.*;

import java.io.Serializable;

public class HotelClass implements Serializable {

	//Declare initializers
	private String name, roomType, confirmationNumber;
	private int age, roomNumber;
	private double bill;
	private boolean hasPaid;
	
	
	//Rand will be used to decide what room number the customer gets 
	//from a range based on which room type they pick
	Random rand = new Random();
	
	//Set up data for a hotel user
	public HotelClass(String name, int age, String roomType){
		this.name = name;
		
		this.age = age;
	
		this.roomType = roomType;
		
		//Finds the room number
		if(roomType.equals("single")){
			bill = 50;
			roomNumber = 1 + (int)(Math.random() * ((5 - 1) + 1));
		}
		else if(roomType.equals("double")){
			bill = 100;
			roomNumber = 6 + (int)(Math.random() * ((10 - 6) + 1));
		}
		else{
			bill = 150;
			roomNumber = 11 + (int)(Math.random() * ((15 - 11) + 1));
		}	
		
		//Uses the random ID function to assign a confirmation number
		confirmationNumber = UUID.randomUUID().toString();
	}

	//Return name
	public String getName(){
		return name;
	}
	
	//Return age
	public int getAge(){
		return age;
	}
	
	//Checks if the user has already paid or not.
	public boolean checkIfPaid(){
		return hasPaid;
	}
	
	//Changes the boolean hasPaid to true so the customer doesn't get billed more than once.
	public void HasPaidAlready(String doThis){
		if(doThis.equals("y")){
			this.hasPaid= true;
		}
		else if(doThis.equals("n")){
			this.hasPaid= false;
		}
	}
	
	//Changes the type of room
	public void changeRoomType(String newRoom){
		this.roomType = newRoom;
		
		//Changes room number based on type of room selected
		if(roomType.equals("single")){
			bill = 50;
			roomNumber = 1 + (int)(Math.random() * ((5 - 1) + 1));
		}
		else if(roomType.equals("double")){
			bill = 100;
			roomNumber = 6 + (int)(Math.random() * ((10 - 6) + 1));
		}
		else{
			bill = 150;
			roomNumber = 11 + (int)(Math.random() * ((15 - 11) + 1));
		}	
	}
	
	//Randomizes the room number if the program accidently picks a number for
	//a room that is already booked
	public int changeRoomNumber(String roomType){
		if(roomType.equals("single")){
			return 1 + (int)(Math.random() * ((5 - 1) + 1));
		}
		else if(roomType.equals("double")){
			
			return 6 + (int)(Math.random() * ((10 - 6) + 1));
		}
		else{
			return 11 + (int)(Math.random() * ((15 - 11) + 1));
		}	
	}
	
	//Actually sets the room number as a property of the HotelClass user itself
	public void setNewRoomNumber(int newNumber){
		this.roomNumber = newNumber;
	}
	
	//Return room type
	public String getRoomType(){
		return roomType;
	}
	
	//Change bill
	public void setBill(String roomType){
		if(roomType.equals("single")){
			this.bill = 50;
		}
		else if(roomType.equals("double")){
			this.bill=100;
		}
		else if(roomType.equals("deluxe")){
			this.bill=150;
		}
	}
	
	//Set bill to zero if the discount code was correctly entered
	public void eraseBill(){
		bill = 0;
	}
	
	//Return room number
	public int getRoomNumber(){
		return roomNumber;
	}
	
	//Return bill amount
	public double getBill(){
		return bill;
	}
	
	//Return confirmation number
	public String getConfirmationNumber(){
		return confirmationNumber;
	}
	
	//Sets all properties of the HotelClass customer to 0 and erases them
	public void cancel(){
		
		this.roomNumber = 0;
		this.roomType = "";
		this.bill = 0;
	}
	
	//Prints out a toString of all the customer's information
	public String toString(){
		return ("Hotel customer: " + name + "\n" + "Room Number: " + roomNumber + "\n" +
				"Age: " + age + "\n" + "Room Type: "+ roomType + "\n" + "Bill: "+ bill + "\n" + "Confirmation number: "+ confirmationNumber);
	}
}