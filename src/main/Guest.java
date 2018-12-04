package main;

import main.FXMLAddOn.PaymentContainer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Guest data storage file: Contains all data that is saved/loaded into the
 * .csv file.
 *
 * @author Cody R
 * @version 1.0
 */
public class Guest implements Comparable<Guest> {

    private static ArrayList<Integer> usedNumbers;

    private HashMap<String,String> map = new HashMap<>();
    private ArrayList<Item> items = new ArrayList<>(); //TODO: Implement methods to handle adding and removing items from a guest
    private ArrayList<PaymentContainer> payments = new ArrayList<>();

    private int number = -1; //Identifying number for a Guest. Must be specific.
    private boolean temp; //Whether the current guest is for temporary use. (Temp guests have no item #)

    @SuppressWarnings("StatementWithEmptyBody")
    public Guest() {
        if (usedNumbers == null) usedNumbers = new ArrayList<>();
        for (int num = 0;!setNumber(num);num++) {} //Loop until we can set the number to be a valid number
    }

    /**
     * Creates a temporary guest for use within the program. A temporary guest does not have an item number
     * and is not meant to be saved into the config file
     * @param temp
     */
    @SuppressWarnings("StatementWithEmptyBody")
    public Guest(boolean temp,String name) {
        if (!temp) { //Only run this if the guest is not a temp
            for (int num = 0;!setNumber(num);num++) {} //Loop until we can set the number to be a valid number
        } else {
            map.put("firstName",name);
        }
    }


    /**
     * Sets the guest's identifying number.
     * This number MUST be unique for each guest.
     * @param num Number to set the guest to
     * @return Successful set of guest's number
     */
    public boolean setNumber(int num) {
        if (temp || usedNumbers.contains(num)) {
            return false; //Return false if the guest's desired number is already in use or if guest is temp
        }
        this.number = num;
        usedNumbers.add(num); //Add the new number to the list of current numbers.
        return true;
    }

    /**
     * Provides the guest's unique identifying number.
     * @return Unique number
     */
    public int getNumber() {
        return number;
    }

    /**
     * Adds a value to the Guest class containing information
     * @param type Identifier used to determine data type
     * @param value Value being stored
     */
    public void add(String type, String value) {
        map.put(type,value);
    }

    /**
     * Returns the desired value from the guest's inventory
     * It is possible for this value to be null!
     * @param key Key of item to get value of.
     * @return Value stored in guest's hashmap of inventory items.
     */
    public String get(String key) {
        String toReturn = map.get(key);
        if (toReturn == null) toReturn = "";
        return toReturn;
    }


    /**
     * Returns all values in the guest's inventory, sorted alphabetically by key
     * @return ArrayList containing all values in sorted order of keys
     */
    public ArrayList<String> getAll() {
        ArrayList<String> keys = getHeader(); //Get the sorted header to return all the values in the correct order
        ArrayList<String> toReturn = new ArrayList<>();
        for (int i = 0; i < map.size(); i++) {
            toReturn.add(i,map.get(keys.get(i))); //Put HashMap value in ArrayList to Return.
        }
        return toReturn;
    }

    /**
     * Returns header of all hashmap keys for the guest's inventory items
     * @return ArrayList containing hashmap keys sorted alphabetically
     */
    public ArrayList<String> getHeader() {
        ArrayList<String> keys = new ArrayList<>(map.keySet());
        keys.sort(Comparator.naturalOrder());
        return keys;
    }

    /**
     * Removes a value from the Guest class containing the specified type
     * @param type Type of data to removePayment
     */
    public void remove(String type) {
        if (map.containsKey(type))
            map.remove(type);
    }

    public boolean contains(String type) {
        return map.containsKey(type);
    }

    public void addItem(Item i) {
        if (items.contains(i)) return; //Don't add an item that already is in inventory multiple times
        items.add(i);
    }

    public void removeItem(Item i) {
        if (!items.contains(i)) return; //Don't removePayment an item the guest doesn't have
        items.remove(i);
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    /**
     * Returns the ArrayList containing all of the guest's payments.
     * @return ArrayList Containing All of Guest's Payments
     */
    public ArrayList<PaymentContainer> getPayments() {
        return payments;
    }

    @Override
    public String toString() {
        //Ensure We Have Data For All Fields, And Remove Null Entries
        String firstName = get("firstName");
        if (firstName==null) firstName="";
        String lastName = get("lastName");
        if (lastName==null) lastName="";

        //Only Return Fields That We Have
        if (firstName.equals("") && lastName.equals("")) {
            return "" + number;
        } else if (firstName.equals("")) {
            return ""+number+" | " + lastName;
        } else if (lastName.equals("")) {
            return ""+number+" | " + firstName;
        }
        //Ideal Case: Return:   # | Last Name, First Name
        return ""+number+" | " + lastName + ", " + firstName;
    }

    /**
     * Frees data associated with the guest, the main one being the number.
     */
    public void free() {
        assert usedNumbers != null;
        if (usedNumbers.size()>1 && usedNumbers.contains(number))
            usedNumbers.remove(number);
        else if (usedNumbers.size()==1) {
            usedNumbers.clear();
        }
    }

    /**
     * Compares guests based on number
     * @return 0 if equal, 1 if this is greater than other guest's number, less if this is less than other guest's number
     */
    public int compareTo(Guest g2) {
        Guest g1 = this;
        if (g1.number == g2.number) return 0; //If both are equal
        if (g1.number > g2.number) return 1; //If this is greater than that
        return -1; //If this is less than that
    }

}
