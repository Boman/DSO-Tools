package dso.tools.chat;

public class Main {
	public static void main(String[] args) {
		ChatSniffer sniffer = new ChatSniffer();
		sniffer.start();
		StatCalculator stats = new StatCalculator();
		stats.start();
	}
}
