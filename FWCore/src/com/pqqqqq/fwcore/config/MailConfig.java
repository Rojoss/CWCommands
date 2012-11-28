package com.pqqqqq.fwcore.config;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import com.pqqqqq.fwcore.Book;
import com.pqqqqq.fwcore.FWCore;
import com.pqqqqq.fwcore.Mail;

public class MailConfig extends Config {
	private FWCore				fwc;
	private YamlConfiguration	cfg;
	private final File			dir		= new File("plugins/FWCore/");
	private final File			file	= new File(dir + "/mail.yml");

	public MailConfig(FWCore fwc) {
		this.fwc = fwc;
	}

	@Override
	public void init() {
		try {
			dir.mkdirs();
			file.createNewFile();
			cfg = new YamlConfiguration();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void load() {
		try {
			cfg.load(file);
			fwc.getMail().clear();

			if (!cfg.isConfigurationSection("mail")) {
				cfg.createSection("mail");
				cfg.save(file);
			}

			for (String m : cfg.getConfigurationSection("mail").getKeys(false)) {
				String sender = ConfigUtil.getString(cfg, file, "mail." + m + ".sender", null);
				String recipient = ConfigUtil.getString(cfg, file, "mail." + m + ".recipient", null);

				if (sender == null || recipient == null)
					continue;

				String title = ConfigUtil.getString(cfg, file, "mail." + m + ".title", null);
				String author = ConfigUtil.getString(cfg, file, "mail." + m + ".author", null);
				List<String> pages = ConfigUtil.getStringList(cfg, file, "mail." + m + ".pages");

				Book book = new Book(title, author, pages.toArray(new String[pages.size()]));
				fwc.getMail().add(new Mail(sender, recipient, book));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void save() {
		try {
			cfg.set("mail", null);

			for (int i = 0; i < fwc.getMail().size(); i++) {
				Mail mail = fwc.getMail().get(i);

				if (mail.getSender() == null || mail.getRecipient() == null)
					continue;

				cfg.set("mail." + Integer.toString(i) + ".sender", mail.getSender());
				cfg.set("mail." + Integer.toString(i) + ".recipient", mail.getRecipient());
				cfg.set("mail." + Integer.toString(i) + ".title", mail.getTitle());
				cfg.set("mail." + Integer.toString(i) + ".author", mail.getAuthor());
				cfg.set("mail." + Integer.toString(i) + ".pages", Arrays.asList(mail.getPages()));
			}

			cfg.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
