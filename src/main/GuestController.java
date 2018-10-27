package main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.net.URI;
import java.util.HashMap;

public class GuestController {

    private HashMap<String, TextField> textFields; //Hashmap containing all textfields in program

    public GuestController() {

    }

    /**
     * Will run when the page is loaded. Sets up formatting for general use later in program
     */
    @FXML
    private void initialize() {
        textFields = new HashMap<>();
        textFields.put("firstName", firstName);
        textFields.put("lastName", lastName);
        textFields.put("phoneNumber", phoneNumber);
        textFields.put("emailAddress", emailAddress);
        textFields.put("entryDonation", entryDonation);
        textFields.put("additionalDonation", additionalDonation);
        textFields.put("amountPaid", amountPaid);
        textFields.put("changeGiven", changeGiven);

        if (!DataManager.hasLoadedData()) { //If Data from .csv file hasn't been loaded, load the data into the form
            DataManager.loadData();
        }
        if (DataManager.guests.isEmpty()) { //Ensure that the guest list has elements in it
            //TODO: Display Alert To User That No Guest Data To Load
        } else {
            guestSelect.setItems(DataManager.guests); //Populate the selector with the loaded guests
            guestSelect.setValue(DataManager.guests.get(0));
        }
        updateForm(guestSelect.getValue()); //If this value is null, it will load a blank form.
    }

    @FXML
    MenuItem save, load, saveAndExit, newGuest, removeGuest;

    @FXML
    ComboBox<Guest> guestSelect;

    @FXML
    TextField firstName, lastName, phoneNumber, emailAddress, entryDonation, additionalDonation, amountPaid, changeGiven;

    @FXML
    Button manageAddOns, saveButton, switchButton;

    @FXML
    Label totalDue,additionalPaymentInfo;

    @FXML
    VBox itemList;

    //
    // --------------------------------------
    // Menu Bar Interactions
    // --------------------------------------
    //

    /**
     * Saves current program data into .csv file
     */
    @FXML
    private void saveData() {
        if (guestSelect.getValue() != null)
            saveForm(); //Saves all fields in form to the guest object
        DataManager.saveData();
    }

    /**
     * Loads data into program from previous .csv save file.
     */
    @FXML
    private void loadData() {
        DataManager.loadData(); //Loads data from file
        //TODO: Use code from init method to update form.
    }

    /**
     * Saves program data and exits program after saving.
     */
    @FXML
    private void saveDataAndExit() {
        saveData();
        exit();
    }

    /**
     * Creates a new guest object, adds it to the list of all guests, and sets
     * the current form to be that of the guest.
     */
    @FXML
    private void newGuest() {
        Guest g = new Guest();
        DataManager.guests.add(g); //Add guest to total guest list
        guestSelect.getItems().add(g);
        guestSelect.getItems().sorted();
        guestSelect.setValue(g);
        newGuest.setDisable(true);
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        newGuest.setDisable(false);
                    }}, 500);
        saveData(); //Force save data to ensure headers are loaded into Hashmap when saving
    }

    /**
     * Removes a guest object from the list of loaded guests. If the guest to delete is
     * the currently loaded guest in the form, the form will be cleared and a blank page will be loaded.
     */
    @FXML
    private void removeGuest() {
        Guest g = guestSelect.getValue();
        if (g == null) return; //If guestSelect has no items selected, don't try to remove nothing
        g.free();
        DataManager.guests.remove(g); //Remove guest from master list
        guestSelect.getItems().remove(g); //Remove guest from combo box
        if (!DataManager.guests.isEmpty()) //Try to update next value to display
            guestSelect.setValue(DataManager.guests.get(0));
        else guestSelect.getItems().clear();
        updateForm(guestSelect.getValue()); //Update fields in form
        removeGuest.setDisable(true);
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        removeGuest.setDisable(false);
                    }}, 500);
    }

    /**
     * Exits program and closes all windows.
     * Note: This WILL NOT save any data loaded into the program at the
     * time of exiting.
     */
    @FXML
    private void exit() {
        System.exit(0);
    }

    /**
     * Directs user to documentation page with instructions on program. Requires internet access for
     * the readme.md page to load.
     */
    @FXML
    private void showDocumentation() {
        try {
            java.awt.Desktop.getDesktop().browse(new URI("https://github.com/Senarii/CheckoutEWB/blob/master/readme.md"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error In Method: showDocumentation(). Unable to load Github readme.md page");
        }
    }

    /**
     * Loads the guest that has been clicked on in the Guest Selector combobox
     * into the form, and populates all of the fields accordingly.
     */
    @FXML
    private void showGuestFromSelector() {
        updateForm(guestSelect.getValue());
    }

    //
    // --------------------------------------
    // Item Management
    // --------------------------------------
    //

    /**
     * Opens new FXML menu window for add-ons to Guest's Auction Visit.
     * Some possible items may be: T-Shirt, Glass, etc... This method will support all wildcard
     * entries that may not be classified elsewhere.
     */
    @FXML
    private void openAddOnMenu() {
        //TODO: Implement Method
    }

    //
    // --------------------------------------
    // Payment
    // --------------------------------------
    //

    /**
     * Updates the indicator for total amount of money that is due. This will dynamically update as the user
     * inputs the amount of money that the person has paid. This will also alert the user if they haven't given
     * sufficient change.
     */
    @FXML
    private void updateTotal() {
        double total = 0;
        for (Item i : guestSelect.getValue().getItems()) {
            double val = 0;
            if (i.get("itemPrice") != null)
                try {
                    val = Double.parseDouble(i.get("itemPrice"));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error Loading in Price Of Item: " + i.get("itemName"));
                }
            total+=val;
        }

        try {
            if (!entryDonation.getText().isEmpty())
                total += Double.parseDouble(entryDonation.getText());
            if (!additionalDonation.getText().isEmpty())
                total += Double.parseDouble(additionalDonation.getText());
        } catch (Exception ignored) {} //Don't respond to exception from user input

        totalDue.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        totalDue.setText(""+total);
        //TODO: Add in costs for add-on items
    }


    /**
     * Will Update The additionalPaymentInfo Label with change due
     * Has Minor Error Checking Built In To Prevent Errors In Making Change
     * Dynamically Updates As Inputs Are Put Into Forms
     */
    @FXML
    private void getChangeNeeded() {
        if (guestSelect.getValue()==null) return;
        double paid = 0, change = 0, total=0;

        try {
            if (amountPaid.getText().equals("")) paid=0;
            else paid = Double.parseDouble(amountPaid.getText());
        } catch (Exception ignored) { }

        try {
            if (changeGiven.getText().equals("")) change=0;
            else change = Double.parseDouble(changeGiven.getText());
        } catch (Exception ignored) { }

        try {
            if (totalDue.getText().equals("")) total=0;
            else total = Double.parseDouble(totalDue.getText());
        } catch (Exception ignored) { }

        double amountChange = paid - total - change;
        System.out.println("Amount of Change: " + amountChange);
        if (total >= 0) {
            if ((paid <= total && change > 0) || (amountChange < 0 && paid >= total)) {
                additionalPaymentInfo.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
                additionalPaymentInfo.setTextFill(Color.DARKRED);
                additionalPaymentInfo.setText("*ERROR IN PAYMENT*");
            } else if (amountChange > 0) {
                additionalPaymentInfo.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
                additionalPaymentInfo.setTextFill(Color.GREEN);
                additionalPaymentInfo.setText("*Change Needed: $" + amountChange + "*");
            } else if (paid < total) {
                additionalPaymentInfo.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
                additionalPaymentInfo.setTextFill(Color.RED);
                additionalPaymentInfo.setText("*Payment Required: $" + (total-paid)+"*");
            } else additionalPaymentInfo.setText("");
        } else additionalPaymentInfo.setText("");
    }
    //
    // --------------------------------------
    // Utility Methods
    // --------------------------------------
    //

    /**
     * Updates all fields in form to match the data of the current guest.
     * If the value passed to this method is null, the form will be wiped.
     *
     * @param g Guest's data to load. (null to clear form)
     */
    private void updateForm(Guest g) {
        itemList.getChildren().clear();
        if (g == null) { //If the guest that is being loaded is null, clear the form and reset it to default state
            for (TextField t : textFields.values()) { //Iterate through all textFields and wipe them
                t.setText("");
                t.setDisable(true);
            }
            totalDue.setText("0");
            additionalPaymentInfo.setText("");
            manageAddOns.setDisable(true);
            saveButton.setDisable(true);

            //TODO: Remove currently loaded guest from GuestSelect Menu.
            //TODO: Correctly handle items that are loaded and need to be cleared
            return;
        }

        for (String s : textFields.keySet()) {
            TextField t = textFields.get(s); //Load desired TextField from HashMap
            t.setDisable(false);
            t.setText(g.get(s)); //Set Value of TextField to Guest's Value For That Field. NullPointer will be caught in Guest Class if exists.
        }
        manageAddOns.setDisable(false);
        saveButton.setDisable(false);
        updateTotal();
        getChangeNeeded();
        //TODO: Ensure Guest is selected in GuestSelect Menu

        for (Item i : g.getItems()) {
            Label l = new Label();
            l.setText("$" + i.get("itemPrice") + " : " + i.get("itemName"));
            itemList.getChildren().add(l);
        }
        if (g.getItems().isEmpty()) itemList.getChildren().clear();
    }

    /**
     * Saves all values currently loaded into the form to the Guest's data class file.
     * This method must run before exiting/loading new window, or all changes made will be lost
     * from lastSave->present.
     */
    @FXML
    private void saveForm() {
        Guest g = guestSelect.getValue();
        for (String s : textFields.keySet()) {
            g.add(s, textFields.get(s).getText()); //Puts Each TextField Into Guest's HashMap
        }
        g.add("totalDue", totalDue.getText());

        //TODO: Handle Adding Items To Guest. Should likely be through Item class rather than Guest class.

        guestSelect.getItems().sort(Guest::compareTo);
    }


    /**
     * Loads the FXML page for Items, and switches the current window to that.
     */
    @FXML
    public void changePages() {
        try {
            Stage stage = (Stage) firstName.getScene().getWindow();
            stage.setMinHeight(300);
            stage.setMinWidth(700);
            Parent root = FXMLLoader.load(getClass().getResource("Item.fxml"));
            Scene scene = new Scene(root, 700, 300);
            stage.setHeight(300);
            stage.setWidth(700);
            stage.setTitle("Checkout-EWB Version II: Item Page");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error Loading Page: Item.fxml.");
            System.out.println("Program Will Continue To Run To Allow Data Saving. Restart As Soon As Possible.");
        }
    }
}
