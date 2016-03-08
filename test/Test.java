import ca.dioo.java.commons.BinaryWithHeaderStream;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;

public class Test {
	public static void main(String[] args) throws Exception {
		String infile = "test/serv.test";
		if (args.length > 0) {
			infile = args[0];
		}

		File tf = new File(infile);
		BinaryWithHeaderStream sms;
		FileInputStream fis = new FileInputStream(tf);
		sms = new BinaryWithHeaderStream(fis);
		BufferedReader in = new BufferedReader(new InputStreamReader(sms.getHeaderStream()));

		System.out.println("Response:");
		String s;
		while ((s = in.readLine()) != null) {
			System.out.println(s);
		}
		in.close();

		System.out.println("\nData:");

		File tof = new File("test/serv.out");
		FileOutputStream fos = new FileOutputStream(tof);

		InputStream bs = sms.getBinaryStream();
		byte[] b = new byte[32768];
		int len;
		while ((len = bs.read(b)) > 0) {
			fos.write(b, 0, len);
		}

		if (false) {
			boolean first = true;
			for (int i = 0; i < b.length; i++) {
				String space = " ";
				if (first) {
					first = false;
					space = "";
				}
				System.out.print(space + Integer.toHexString(b[i] & 0xFF));
			}
			System.out.print("\n");
		}

		/*
		int len;
		while ((len = fis.read(data)) > -1) {
			fos.write(data, 0, len);
		}
		*/
		fis.close();
		fos.close();
	}
}
