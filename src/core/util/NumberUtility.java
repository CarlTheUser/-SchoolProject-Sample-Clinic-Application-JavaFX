package core.util;

public class NumberUtility {
    public static boolean isNumber(String  number, boolean hasDecimalPoint){
        if(number.trim().length() == 0) return false;
        final char[] chars = number.toCharArray();
        boolean decimalFound = false;
        for(char c : chars){
            if(!(Character.isDigit(c)|| ((hasDecimalPoint) && (!decimalFound && c == '.')))) return false;
        }
        return true;
    }

    public static boolean isNumber(String number){
        return isNumber(number, false);
    }

    public static boolean containsNumber(String argument){
        if(argument.trim().length() == 0) return false;
        int numberCount = 0;
        char[] argumentCharacters = argument.toCharArray();
        for (char c : argumentCharacters) {
            if(Character.isDigit(c)) numberCount++;
        }
        return numberCount > 0;
    }
}
