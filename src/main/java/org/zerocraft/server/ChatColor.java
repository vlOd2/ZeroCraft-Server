package org.zerocraft.server;

public class ChatColor {
	/**
	 * Strips the color codes from the specified string
	 * 
	 * @param str the string to strip
	 * @return the stripped string
	 */
	public static String stripColorCodes(String str, char colorCode) {
		char[] data = str.toCharArray();
		String cleanedStr = "";

		for (int i = 0; i < data.length; i++) {
			if (data[i] == colorCode) {
				i++;
			} else {
				cleanedStr += (char) data[i];
			}
		}

		return cleanedStr;
	}
	
	/**
	 * Sanitizes the color codes in the specified string
	 * 
	 * @param str the string to sanitize
	 * @return the sanitized string
	 */
	public static String sanitizeColorCodes(String str) {
		char[] data = str.toCharArray();
		String cleanedStr = "";

		for (int i = 0; i < data.length; i++) {
			if (data[i] == '&' && (i + 2 > data.length - 1 || data[i + 2] == '&')) {
				break;
			}
			cleanedStr += data[i];
		}

		return cleanedStr;
	}
	
	/**
	 * Returns a formatted translated string using the specified format string arguments
	 * 
	 * @param format the format string
	 * @param args the arguments for the format string
	 * @see String#format(String, Object...)
	 * @return formatted and translated string
	 */
	public static String format(String format, Object... args) {
		return translateSafe(String.format(format, args));
	}
	
	/**
	 * Translates the safe color code to the regular color code
	 * 
	 * @param str the string to translate
	 * @return the translated string
	 */
	public static String translateSafe(String str) {
		char[] data = str.toCharArray();
		String cleanedStr = "";

		for (int i = 0; i < data.length; i++) {
			if (data[i] == '%') {
				if (i + 2 > data.length - 1 || data[i + 2] == '&') {
					break;
				}
				
				i++;

				if (data[i] == '%') {
					cleanedStr += data[i];
					continue;
				} else {
					cleanedStr += "&";
					cleanedStr += data[i];
				}
			} else {
				cleanedStr += data[i];
			}
		}

		return cleanedStr;
	}
}
