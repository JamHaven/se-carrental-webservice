package pacApp.pacModel;

public enum Currency {
    USD, EUR;

    public static int getCurrencyId(String currency) {
        try {
            return Currency.valueOf(currency).ordinal();
        } catch (IllegalArgumentException ex) {
            return -1;
        }
    }
}
