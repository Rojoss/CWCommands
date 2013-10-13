package net.clashwars.cwcore.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import net.clashwars.cwcore.CWCore;
import net.clashwars.cwcore.commands.internal.CommandClass;
import net.clashwars.cwcore.util.CmdUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.plugin.Plugin;

public class CmdsCmd implements CommandClass {
	
	private HashMap<String, String> modifiers = new HashMap<String, String>();
	private HashMap<String, String> optionalArgs = new HashMap<String, String>();
	private String[] args;
	
	public CmdsCmd(CWCore cwc) {
	}

	@Override
	public boolean execute(CommandSender sender, Command cmd, String lbl, String[] cmdArgs) {
		int page = 1;
		String plugin = null;
		
		args = CmdUtils.getCmdArgs(cmdArgs, optionalArgs, modifiers);
		
		if (CmdUtils.hasModifier(args,"-h", true)) {
			CmdUtils.commandHelp(sender, lbl, optionalArgs, modifiers);
			return true;
		}
		
		
		//Args
		if (args.length > 0) {
			try {
	            page = Integer.parseInt(args[0]);
	        } catch (Throwable e) {
	        }
		}
		
		if (args.length > 1) {
			for(Plugin plug : Bukkit.getPluginManager().getPlugins()){
        		if (plug.getName().toLowerCase().contains(args[1].toLowerCase())) {
					plugin = plug.getName();
				}
			}
			if (args[1].equalsIgnoreCase("all")) {
				plugin = "all";
			}
			if (plugin == null) {
				sender.sendMessage(CWCore.pf + ChatColor.RED + "No plugin found with the name " + ChatColor.GRAY + args[1]);
				return true;
			}
		} else {
			plugin = "all";
		}
        
		
		//Action
        SortedMap<Integer, String> commands = new TreeMap<Integer, String>(Collections.reverseOrder());
        
        
        if (plugin.equalsIgnoreCase("all")) {
        	for (int a = 0; a < CWCore.cmdList.size(); a++) {
        		commands.put(a, CWCore.cmdList.get(a).getName());
        	}
        } else {
        	for(Plugin plug : Bukkit.getPluginManager().getPlugins()){
        		if (plug.getName().toLowerCase().contains(plugin.toLowerCase())) {
        			List<Command> cmdList = PluginCommandYamlParser.parse(plug);
            		for(int i = 0; i < cmdList.size(); i++){
            	        commands.put(i,cmdList.get(i).getLabel());
            	    }
        		}
            }
        }
        
        int pages = (((commands.size() % 8) == 0) ? commands.size() / 8 : (commands.size() / 8) + 1);
		
		if (page > pages || page <= 0) {
			if (page == 1) {
				sender.sendMessage(CWCore.pf + ChatColor.RED + "No commands found for this plugin.");
	            return true;
			}
            sender.sendMessage(CWCore.pf + ChatColor.RED + "page " + ChatColor.GRAY + page + ChatColor.RED + " is not a valid help page.");
            return true;
        }
		
		sender.sendMessage(ChatColor.DARK_GRAY + "=====  " + ChatColor.DARK_RED + "CW Commands list for " + ChatColor.GRAY + plugin + ChatColor.RED + " page: " 
				+ ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + page + ChatColor.DARK_GRAY + "/" + ChatColor.DARK_PURPLE + pages + ChatColor.GRAY + "] " + ChatColor.DARK_GRAY + "  =====");
	    int i = 0;
	    int k = 0;
	    page--;
	    for (final Entry<Integer, String> e : commands.entrySet()) {
	        k++;
	        if ((((page * 8) + i + 1) == k) && (k != ((page * 8) + 8 + 1))) {
	            i++;
	            sender.sendMessage(ChatColor.GOLD + CmdUtils.syntaxFromName(e.getValue()).replace("<command>", e.getValue().toLowerCase())
	            		+ ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + CmdUtils.descFromName(e.getValue()));
	        }
	    }
	    page++;
	    if (page < pages) {
	    	page++;
	    	sender.sendMessage(ChatColor.DARK_GRAY + "===  " + ChatColor.DARK_RED + "Next page: " + ChatColor.RED + "/" + lbl + " " + page
	    			+ ChatColor.GRAY + " Use " + ChatColor.DARK_GRAY + "/<cmd> -h" + ChatColor.GRAY + " for cmd help!" + ChatColor.DARK_GRAY + "  ===");
	    } else {
	    	sender.sendMessage(ChatColor.DARK_GRAY + "===  " + ChatColor.DARK_RED + "~Last Page~" 
	    			+ ChatColor.GRAY + " Use " + ChatColor.DARK_GRAY + "/<cmd> -h" + ChatColor.GRAY + " for cmd help!" + ChatColor.DARK_GRAY + "  ===");
	    }
		return true;
	}

	@Override
	public String[] permissions() {
		return new String[] { "cwcore.cmd.cmds", "cwcore.cmd.*", "cwcore.*" };
	}
	
	/*
	
	*/
}
