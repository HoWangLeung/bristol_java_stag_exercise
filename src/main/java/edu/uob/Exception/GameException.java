package edu.uob.Exception;

public class GameException extends Exception {

    private static final long serialVersionUID = 8061335505061402195L;
    private static String error = "[ERROR]";
    public static String UNKNOWN_COLUMN = "Cannot find the specified column name";
    public static String INVALID_COMMAND_TYPE = "Invalid Command Type";




    public GameException(String message) {

        super(error+message);
        System.out.println(error+message);

    }


    public static class UnspecifiedDabaseException extends GameException {
        private static final long serialVersionUID = 8061335505061402995L;
        public UnspecifiedDabaseException() {
            super("You did not specify the database.");
        }
    }

    public static class ParserException extends GameException {
        private static final long serialVersionUID = 8061335505061402996L;
        public ParserException(String message) {
            super(message);
        }
    }

//    public static class QueryException extends DatabaseException {
//        private static final long serialVersionUID = 8061335505061402916L;
//        public QueryException(String message) {
//            super(message);
//        }
//    }
//
//    public static class MissingSemiColonException extends DatabaseException {
//        private static final long serialVersionUID = 8061335505061402997L;
//        public MissingSemiColonException( ) {
//            super("must end with semi-colon");
//        }
//    }
//
//    public static class DirectoryDoesNotExistException extends DatabaseException {
//        private static final long serialVersionUID = 1061335505061402997L;
//        public DirectoryDoesNotExistException( ) {
//
//            super("The table does not exist.");
//        }
//    }
}
