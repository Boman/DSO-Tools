package dso.tools.chat;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dso.tools.chat.data.Message;
import dso.tools.chat.db.DatabaseManager;

public class ChatSniffer extends Thread {
	static final Pattern xmlPattern = Pattern
			.compile("<body.*<message.*</message>.*</body>");

	public void run() {
		String networkInterface = "wlan0";
		int snapLen = 512;
		String[] command = new String[] { "" };

		String os = System.getProperty("os.name").toLowerCase();
		if ((os.indexOf("win") >= 0)) {
			command = new String[] { "windump -i " + networkInterface + " -s "
					+ snapLen + " -A 'net 87.119.203.0/24 and tcp[13] & 8!=0'" };

		} else {
			command = new String[] {
					"gksu",
					"tcpdump -i " + networkInterface + " -s " + snapLen
							+ " -A -Z " + System.getProperty("user.name")
							+ " 'net 87.119.203.0/24 and tcp[13] & 8!=0'" };
		}

		Process process;
		InputStream inStream = null;
		try {
			process = Runtime.getRuntime().exec(command);
			inStream = process.getInputStream();
			// inStream = new FileInputStream("tmp.xml");

			InputStreamReader in = new InputStreamReader(inStream);
			int inchar;
			String line = "";
			while ((inchar = in.read()) >= 0) {
				// System.out.print((char) inchar);
				if (inchar == '\n') {

					// Extract clean xml message part
					Matcher matcher = xmlPattern.matcher(line);
					if (matcher.matches()) {
						Message message = new Message(matcher.group());
						DatabaseManager.save(message);
					}

					line = "";
				} else
					line += (char) inchar;
			}
		} catch (IOException ee) {
			System.err.println(ee);
		} finally {
			try {
				inStream.close();
				process = null;
			} catch (IOException e) {
				System.err.println(e);
			}
		}
		DatabaseManager.close();
	}
}