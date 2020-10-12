package ir.amingoli.mycoustomer.util;

public class TextUtil {
    public boolean valueIsNumber(String value){
        switch (value){
            case "0":
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
            case "7":
            case "8":
            case "9":
                return true;
            default:
                return false;
        }
    }
}
