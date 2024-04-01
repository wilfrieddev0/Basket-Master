package variables;
public abstract class months{
    public static final int JANVIER = 31;
    public static final int FEVRIER = 28; // Pour une année non bissextile
    public static final int MARS = 31;
    public static final int AVRIL = 30;
    public static final int MAI = 31;
    public static final int JUIN = 30;
    public static final int JUILLET = 31;
    public static final int AOUT = 31;
    public static final int SEPTEMBRE = 30;
    public static final int OCTOBRE = 31;
    public static final int NOVEMBRE = 30;
    public static final int DECEMBRE = 31;
    public  static int daysOfMonths(int i,boolean bissectrices){
        if ( i == 1 || i == 3 ||i == 5 || i == 7 || i == 8 || i == 10 || i ==12){
            return 31;
        }else if (i == 2){
            return 30;
        }else if (bissectrices) return 28; else return 27;
    }
}
