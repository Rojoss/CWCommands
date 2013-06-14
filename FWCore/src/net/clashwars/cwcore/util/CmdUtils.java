package net.clashwars.cwcore.util;

public class CmdUtils {
	
	/**
	 * Check if  a command has modifiers in them like -s etc.
	 * @param args (All command arguments)
	 * @param mod (the modifier to check for like "-s")
	 * @return boolean (If it finds the modifier true else false)
	 */
	public static boolean hasModifier(String[] args, String mod) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].toLowerCase().startsWith(mod)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return arguments without the modifier
	 * This is used to remove the modifier so it can be used on any place in the command.
	 * @param args (All command arguments)
	 * @param mod (the modifier to remove like "-s")
	 * @return String[] (New args list without modifiers)
	 */
	public static String[] modifiedArgs(String[] args, String mod) {
		int i;
        int sloc = -1;
        String temp;
        for (i = 0; i < args.length; i++) {
                if (args[i].toLowerCase().startsWith(mod)) {
                        sloc = i;
                }
        }
        if (sloc != -1) {
                for (i = sloc; i < args.length -1; i++) {
                        temp = args[i];
                    args[i] = args[i + 1];
                    args[i + 1] = temp;
                }
        }
       
        String[] args2 = new String[args.length - 1];
        for (i = 0; i < args2.length; i++) {
                args2[i] = args[i];
        }
        return args2;
	}
	
	/**
	 * Return the index in the StringList of the argument given.
	 * This is used to check for optional arguments like "name:&6name"
	 * @param args (All command arguments)
	 * @param argument (The argument to check for like "name:")
	 * @return int (Index of string)
	 */
	public static int getArgIndex(String[] args, String argument) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].toLowerCase().startsWith(argument)) {
				return i;
			}
		}
		return 0;
	}
}
