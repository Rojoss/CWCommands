package net.clashwars.cwcore.commands.internal;

import org.bukkit.command.CommandSender;

public interface CommandClass {

	public boolean execute(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args);

	public String[] permissions();

	//public String[] aliases();
}
