package dso.tools.chat.data;

import java.io.StringReader;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.hibernate.annotations.GenericGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

@Entity
@Table(name = "MESSAGE")
public class Message {
	private static final DocumentBuilderFactory xmlFact;
	private static final XPath xpath;

	static {
		xmlFact = DocumentBuilderFactory.newInstance();
		xpath = XPathFactory.newInstance().newXPath();
	}

	public enum DsoMsgTypes {
		Unknown, Trade, TradeClear, MyTrade, MyTradeClear, MyChat, ChatGlobal, ChatHelp
	}

	@Id
	@Column(name = "MESSAGE_ID")
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private long id;

	@Column(name = "SENDER")
	private String sender;
	@Column(name = "RECEIVER")
	private String receiver;
	@Column(name = "MESSAGECONTENT")
	private String messageContent;
	@Column(name = "PLAYERID")
	private int playerID;
	@Column(name = "PLAYERNAME")
	private String playerName;
	@Column(name = "PLAYERTAG")
	private String playerTag;
	@Column(name = "MESSAGETYPE")
	private DsoMsgTypes messageType;
	@Column(name = "TRADEA1")
	private String tradeA1;
	@Column(name = "TRADEA2")
	private int tradeA2;
	@Column(name = "TRADEB1")
	private String tradeB1;
	@Column(name = "TRADEB2")
	private int tradeB2;
	@Column(name = "TIME")
	private Date time;

	public Message() {
	}

	public Message(String sXMLString) {
		// Init
		sender = "";
		receiver = "";
		messageContent = "";
		playerID = 0;
		playerName = "";
		playerTag = "";
		messageType = DsoMsgTypes.Unknown;
		tradeA1 = "";
		tradeA2 = 0;
		tradeB1 = "";
		tradeB2 = 0;
		time = new Date(System.currentTimeMillis());

		// Create XML Parser on input string
		Document dom;
		try {
			dom = xmlFact.newDocumentBuilder().parse(
					new InputSource(new StringReader(sXMLString)));

			Node messageNode;
			if ((messageNode = (Node) xpath.compile("/*/message").evaluate(dom,
					XPathConstants.NODE)) != null) {
				NamedNodeMap attributes = messageNode.getAttributes();
				sender = attributes.getNamedItem("from").getNodeValue();
				receiver = attributes.getNamedItem("to").getNodeValue();
				messageType = sender.startsWith("trade") ? DsoMsgTypes.Trade
						: (receiver.startsWith("trade") ? DsoMsgTypes.MyTrade
								: (sender.contains("global") ? DsoMsgTypes.ChatGlobal
										: (sender.contains("help") ? DsoMsgTypes.ChatHelp
												: (receiver.contains("global") ? DsoMsgTypes.MyChat
														: DsoMsgTypes.Unknown))));

				// Read Message Content
				Node messageBody;
				if ((messageBody = (Node) xpath.compile("./body").evaluate(
						messageNode, XPathConstants.NODE)) != null) {
					messageContent = messageBody.getTextContent();

					// Check for Trade Cancellation
					if (messageContent.toLowerCase().equals("clear")) {
						if (messageType == DsoMsgTypes.Trade) {
							messageType = DsoMsgTypes.TradeClear;
						} else {
							messageType = DsoMsgTypes.MyTradeClear;
						}
					}

					if (messageType == DsoMsgTypes.Trade
							|| messageType == DsoMsgTypes.MyTrade) {
						String[] trade = messageContent.split("\\|");
						tradeA1 = trade[0];
						tradeA2 = new Integer(trade[1]);
						tradeB1 = trade[2];
						tradeB2 = new Integer(trade[3]);
					}
				} else
					return;

				// Read Player
				Node bbmsg;
				if ((bbmsg = (Node) xpath.compile("./bbmsg").evaluate(
						messageNode, XPathConstants.NODE)) != null) {
					playerID = new Integer(bbmsg.getAttributes()
							.getNamedItem("playerid").getNodeValue());
					playerName = bbmsg.getAttributes()
							.getNamedItem("playername").getNodeValue();
					playerTag = bbmsg.getAttributes().getNamedItem("playertag")
							.getNodeValue();
				} else
					return;

				// System.out.println(sender + " " + receiver + " " +
				// messageType
				// + " " + playerID + " " + playerName + " " + playerTag
				// + " " + messageContent);
			} else
				return;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Message: " + sXMLString);
		}
	}

	public long getId() {
		return id;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public int getPlayerID() {
		return playerID;
	}

	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getPlayerTag() {
		return playerTag;
	}

	public void setPlayerTag(String playerTag) {
		this.playerTag = playerTag;
	}

	public DsoMsgTypes getMessageType() {
		return messageType;
	}

	public void setMessageType(DsoMsgTypes messageType) {
		this.messageType = messageType;
	}

	public String getTradeA1() {
		return tradeA1;
	}

	public void setTradeA1(String tradeA1) {
		this.tradeA1 = tradeA1;
	}

	public int getTradeA2() {
		return tradeA2;
	}

	public void setTradeA2(int tradeA2) {
		this.tradeA2 = tradeA2;
	}

	public String getTradeB1() {
		return tradeB1;
	}

	public void setTradeB1(String tradeB1) {
		this.tradeB1 = tradeB1;
	}

	public int getTradeB2() {
		return tradeB2;
	}

	public void setTradeB2(int tradeB2) {
		this.tradeB2 = tradeB2;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
}