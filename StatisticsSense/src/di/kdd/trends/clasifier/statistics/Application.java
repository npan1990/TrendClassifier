package di.kdd.trends.clasifier.statistics;

/**
 * Created by panos on 2/25/14.
 */
public class Application {

    public static void main (String []args) throws Exception {

        if (args.length < 2) {
            System.err.println("Proccesed tweets file and trends file are needed as arguments");
            return;
        }

        Statistics.sense(args[0], args[1]);
    }
}
