package net.clashwars.cwcore.config;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

import net.clashwars.cwcore.Book;
import net.clashwars.cwcore.CWCore;

public class BookConfig extends Config {
	private CWCore cwc;
	private YamlConfiguration cfg;
	private ConfigUtil cu;
	private final File			dir		= new File("plugins/CWCore/");
	private final File			file	= new File(dir + "/books.yml");

	public BookConfig(CWCore cwc) {
		this.cwc = cwc;
	}

	@Override
	public void init() {
		try {
			dir.mkdirs();
			file.createNewFile();
			cfg = new YamlConfiguration();
			cu = new ConfigUtil(cfg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void load() {
		try {
			cfg.load(file);
			cwc.getSavedBooks().clear();

			if (!cfg.isConfigurationSection("books")) {
				cfg.createSection("books");
				cfg.save(file);
			}

			for (String b : cfg.getConfigurationSection("books").getKeys(false)) {
				String title = cu.getString("books." + b + ".title", null);
				String author = cu.getString("books." + b + ".author", null);
				List<String> pages = cu.getStringList("books." + b + ".pages");

				cwc.getSavedBooks().put(b, new Book(title, author, pages.toArray(new String[pages.size()])));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void save() {
		try {
			cfg.set("books", null);
			for (Map.Entry<String, Book> entry : cwc.getSavedBooks().entrySet()) {
				String name = entry.getKey();
				Book book = entry.getValue();

				cfg.set("books." + name + ".title", book.getTitle());
				cfg.set("books." + name + ".author", book.getAuthor());
				cfg.set("books." + name + ".pages", Arrays.asList(book.getPages()));
			}
			
			cfg.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}