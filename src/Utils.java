package ca.dioo.java.commons;

public class Utils {
	public static String repeat(String s, int nb) {
		StringBuffer sb = new StringBuffer(s);

		if (nb == 0) return "";

		for (int i = 1; i < nb; i++) {
			sb.append(s);
		}

		return sb.toString();
	}


	public static String join(CharSequence delimiter, CharSequence... elements) {
		if (elements == null || elements.length < 1) {
			return "";
		}

		StringBuffer sb = new StringBuffer(elements[0]);
		for (int i = 1; i < elements.length; i++) {
			sb.append(delimiter);
			sb.append(elements[i]);
		}

		return sb.toString();
	}


	public static String join(CharSequence delim0, CharSequence delim1, CharSequence[]... elements) {
		if (elements == null || elements.length < 1) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < elements.length; i++) {
			StringBuffer sbp = new StringBuffer();

			if (elements[i].length > 0) {
				sbp.append(elements[i][0]);
			}

			for (int j = 1; j < elements[i].length; j++) {
				sbp.append(delim1);
				sbp.append(elements[i][j]);
			}
			sb.append(delim0);
			sb.append(sbp);
		}

		return sb.toString();
	}


	public static int bool2int(boolean b) {
		return b ? 1 : 0;
	}


	public static String getPrettyStackTrace(Throwable t) {
		if (t == null) {
			return "";
		}
		StringBuffer sb = new StringBuffer(t.toString());

		for (StackTraceElement e: t.getStackTrace()) {
			sb.append("\n    at " + e.getClassName() + "." + e.getMethodName() + "(" + e.getFileName() + ":" + e.getLineNumber() + ")");
		}

		return sb.toString();
	}


	public static String getTimeStr(long timestamp) {
		String timeStr = "";
		boolean isEmpty = true;

		int SEC_PER_YEAR = 60 * 60 * 24 * 365;
		if (timestamp > SEC_PER_YEAR) {
			timeStr += timestamp / SEC_PER_YEAR + "y";
			timestamp = timestamp % SEC_PER_YEAR;
		}
		int SEC_PER_MONTH = 60 * 60 * 24 * 30;
		if (timestamp > SEC_PER_MONTH) {
			timeStr += timestamp / SEC_PER_MONTH + "M";
			timestamp = timestamp % SEC_PER_MONTH;
		}
		int SEC_PER_DAY = 60 * 60 * 24;
		if (timestamp > SEC_PER_DAY) {
			timeStr += timestamp / SEC_PER_DAY + "d";
			timestamp = timestamp % SEC_PER_DAY;
		}
		int SEC_PER_HOUR = 60 * 60;
		if (timestamp > SEC_PER_HOUR) {
			timeStr += timestamp / SEC_PER_HOUR + "h";
			timestamp = timestamp % SEC_PER_HOUR;
		}
		if (timestamp > 60) {
			timeStr += timestamp / 60 + "m";
			timestamp = timestamp % 60;
		}
		if (timestamp > 0) {
			timeStr += timestamp + "s";
		} else {
			timeStr += "0s";
		}

		return timeStr;
	}
}
