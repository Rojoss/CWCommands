package net.clashwars.cwcore;

public class Book {
	private String		title	= null;
	private String		author	= null;
	private String[]	pages	= null;
	
	public Book(String title, String author, String... pages) {
		this.title = title;
		this.author = author;
		this.pages = pages;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String[] getPages() {
		return pages;
	}

	public void setPages(String[] pages) {
		this.pages = pages;
	}
}
