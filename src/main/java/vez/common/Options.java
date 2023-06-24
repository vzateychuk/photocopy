package vez.common;

/**
 * This class implements the Singleton pattern
 * to handle command-line option processing.
 */
public class Options {

    /**
     * The singleton {@link Options} instance.
     */
    private static Options mUniqueInstance = null;

    /**
     * Source folder
     */
    private String source = "";

    /**
     * Destination folder
     */
    private String dest = "";

    /**
     * @return The one and only singleton uniqueInstance
     */
    public static Options getInstance() {
        if (mUniqueInstance == null) {
            mUniqueInstance = new Options();
        }
        return mUniqueInstance;
    }

    /**
     * Make the constructor private for a singleton.
     */
    private Options() {}

    public String getSource() {
        return source;
    }

    public String getDest() {
        return dest;
    }

    /**
     * Parse command-line arguments and set the appropriate values.
     */
    public boolean parseArgs(String[] argv) {

        if (argv == null) {
            return false;
        }

        for (int argc = 0; argc < argv.length; argc += 2)
            switch (argv[argc]) {
                case "-s" -> source = argv[argc + 1];
                case "-d" -> dest = argv[argc + 1];
                default -> {
                    printUsage();
                    return false;
                }
            }
        return true;
    }

    /**
     * Print out usage and default values.
     */
    private void printUsage() {
        System.out.println("Application params not defined, exit.");
        System.out.println("Usage: ");
        System.out.println("-d [destination folder]");
        System.out.println("-s [source folder]");
    }

}
