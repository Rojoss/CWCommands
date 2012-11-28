package com.pqqqqq.fwcore;

public class Mail extends Book {
	private String	sender;
	private String	recipient;

	public Mail(String title, String author, String... pages) {
		super(title, author, pages);
	}

	public Mail(String sender, String recipient, String title, String author, String... pages) {
		super(title, author, pages);
		this.sender = sender;
		this.recipient = recipient;
	}

	public Mail(String sender, String recipient, Book book) {
		this(sender, recipient, book.getTitle(), book.getAuthor(), book.getPages());
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getRecipient() {
		return recipient;
	}

	public void setRecipient(String recipient) {
		this.recipient = recipient;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Mail) {
			Mail m = (Mail) obj;

			String mA = m.getAuthor() == null ? "" : m.getAuthor();
			String tA = getAuthor() == null ? "" : getAuthor();

			String mT = m.getTitle() == null ? "" : m.getTitle();
			String tT = getTitle() == null ? "" : getTitle();

			String[] mP = m.getPages() == null ? new String[0] : m.getPages();
			String[] tP = getPages() == null ? new String[0] : getPages();

			return mA.equals(tA) && mT.equals(tT) && mP.equals(tP);
		}
		return false;
	}
}
