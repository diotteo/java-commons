import ca.dioo.java.commons.BinaryWithHeaderStream;

import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Test {
	public static void main(String[] args) throws Exception {
		File tf = new File("test/serv.test");
		BinaryWithHeaderStream sms;
		FileInputStream fis = new FileInputStream(tf);
		sms = new BinaryWithHeaderStream(fis);
		BufferedReader in = new BufferedReader(new InputStreamReader(sms));

		System.out.println("Response:");
		String s;
		while ((s = in.readLine()) != null) {
			System.out.println(s);
		}
		in.close();

		System.out.println("\nData:");
		byte[] data = sms.getBuffer();

		File tof = new File("test/serv.out");
		FileOutputStream fos = new FileOutputStream(tof);
		fos.write(data);

		if (false) {
			boolean first = true;
			for (int i = 0; i < data.length; i++) {
				String space = " ";
				if (first) {
					first = false;
					space = "";
				}
				System.out.print(space + Integer.toHexString(data[i] & 0xFF));
			}
			System.out.print("\n");
		}

		int len;
		while ((len = fis.read(data)) > -1) {
			fos.write(data, 0, len);
		}
		fis.close();
		fos.close();
	}
}
