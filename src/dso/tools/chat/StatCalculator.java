package dso.tools.chat;

import java.sql.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import dso.tools.chat.data.Message;
import dso.tools.chat.data.Message.DsoMsgTypes;
import dso.tools.chat.db.DatabaseManager;

public class StatCalculator extends Thread {
	private Date lastUpdate;

	static final String[] sarResourceNames = new String[] { "Beer", "Bow",
			"Bread", "Bronze", "BronzeOre", "BronzeSword", "Cannon",
			"Carriage", "Coal", "Coin", "Corn", "Crossbow", "ExoticPlank",
			"ExoticWood", "Fish", "Flour", "Gold", "GoldOre", "Granite",
			"GunPowder", "Horse", "Iron", "IronOre", "IronSword", "Longbow",
			"Marble", "Meat", "Plank", "RealPlank", "RealWood", "Salpeter",
			"Sausage", "Steel", "SteelSword", "Stone", "Titanium",
			"TitaniumOre", "TitaniumSword", "Tool", "Water", "Wheel", "Wood",
			"EventResource" };

	public void run() {
		lastUpdate = new Date(System.currentTimeMillis());
		while (true) {
			Date newUpdate = new Date(System.currentTimeMillis() - 100);
			List<Message> newMessages = getMessages(lastUpdate, newUpdate);
			lastUpdate = newUpdate;

			for (Message message : newMessages) {
				if (message.getMessageType() == DsoMsgTypes.Trade) {
					if (message.getTradeB1().equals("Water")) {
						System.out.println(message.getMessageContent());
					}
				}
			}

			Session session = DatabaseManager.currentSession();
			Criteria crit = session.createCriteria(Message.class);
			System.out.println(crit.list().size() + " Datasets fetched");
			try {
				Thread.sleep(30 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<Message> getMessages(Date from, Date to) {
		Session session = DatabaseManager.currentSession();
		Criteria crit = session.createCriteria(Message.class);
		crit.add(Restrictions.between("time", from, to));
		return (List<Message>) crit.list();
	}
}
