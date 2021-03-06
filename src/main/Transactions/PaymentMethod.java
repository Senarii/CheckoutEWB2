package main.Transactions;

import main.PaymentController;

public enum PaymentMethod { //Ways that a user can pay
    CASH,CHECK,ONLINE,OTHER;

    /**
     * Returns a nicely formatted string containing the name of the Payment Method.
     * If unable to parse, return "[INVALID PAYMENT METHOD]"
     * @return String with payment method
     */
    @Override
    public String toString() {
        if (this == PaymentMethod.CASH) return "Cash";
        if (this == PaymentMethod.CHECK) return "Check";
        if (this == PaymentMethod.ONLINE) return "Online";
        return "Other";
    }

    /**
     * Given a string, returns the correct enumerated type matching the string
     * @param s String to parse
     * @return Correct Type that string is describing
     */
    public static PaymentMethod stringToPaymentMethod(String s) {

        if (s.equalsIgnoreCase("cash")) return PaymentMethod.CASH;
        if (s.equalsIgnoreCase("check")) return PaymentMethod.CHECK;
        if (s.equalsIgnoreCase("online")) return PaymentMethod.ONLINE;
        if (s.equalsIgnoreCase("other")) return PaymentMethod.OTHER;

        return PaymentMethod.OTHER; //Default to this type if not able to parse string
    }

}
