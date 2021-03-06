package com.wormhole_xtreme.permissions;

import java.util.concurrent.ConcurrentHashMap;


import org.bukkit.entity.Player;

import com.wormhole_xtreme.config.ConfigManager;
import com.wormhole_xtreme.config.ConfigManager.ConfigKeys;
import com.wormhole_xtreme.model.Stargate;
import com.wormhole_xtreme.model.StargateDBManager;


/** 
 * WormholeXtreme Built in Permissions Manager 
 * @author Ben Echols (Lologarithm) 
 */ 
public class PermissionsManager 
{
	private static ConcurrentHashMap<String, PermissionLevel> player_general_permission = new ConcurrentHashMap<String, PermissionLevel>();

	//private static HashMap<String, PermissionLevel> group_general_permission = new HashMap<String, PermissionLevel>();
	//private static Object group_lock = new Object();
	
	public static PermissionLevel getPermissionLevel( Player p, Stargate s)
	{
		if ( !ConfigManager.getBuiltInPermissionsEnabled() )
		{
			return PermissionLevel.WORMHOLE_FULL_PERMISSION;
		}

		// 1. Check for individual network rights
		if ( s != null )
		{
			
		}
		// 2. Check for individual general rights
		PermissionLevel lvl =  GetIndividualPermissionLevel(p.getName());
		if ( lvl != PermissionLevel.NO_PERMISSION_SET )
			return lvl;
		// 3. Check for group network rights
		if ( s != null )
		{
			
		}
		
		// 4. Check for group general rights
		
		// 5. Check for default network rights
		if ( s != null )
		{
			
		}
		
		// 5. Check for default general rights
		if ( p.isOp() )
		{
			return PermissionLevel.WORMHOLE_FULL_PERMISSION;
		}
		else
		{
			return ConfigManager.getBuiltInDefaultPermissionLevel();
		}
	}
	
	public static void SetIndividualPermissionLevel( String player, PermissionLevel lvl )
	{
		String pl_lower = player.toLowerCase();
		player_general_permission.put(pl_lower, lvl);
		StargateDBManager.StoreIndividualPermissionInDB(pl_lower, lvl);
	}
	
	public static PermissionLevel GetIndividualPermissionLevel( String player )
	{
		String pl_lower = player.toLowerCase();
		if (player_general_permission.containsKey(pl_lower))
			return player_general_permission.get(pl_lower);
		else
			return PermissionLevel.NO_PERMISSION_SET;
	}
	
	public static void LoadPermissions()
	{
		player_general_permission = StargateDBManager.GetAllIndividualPermissions();
		// StargateDBManager.GetAllGroupPermissions();
	}
	
	public enum PermissionLevel
	{
		NO_PERMISSION_SET,
		WORMHOLE_FULL_PERMISSION,
		WORMHOLE_CREATE_PERMISSION,
		WORMHOLE_USE_PERMISSION,
		WORMHOLE_NO_PERMISSION
	}
	// 0         1     2        3
	// 0         1     2        3
	// /stargate perms indiv    <USERNAME>     <OPTIONAL_SET> (else its a get)
	// /stargate perms group    <GROUPNAME>    <OPTIONAL_SET> (else its a get)
	// /stargate perms default <OPTIONAL_SET> (else a get)
	// /stargate perms active  <OPTIONAL_SET> (else a get)
	public static void HandlePermissionRequest(Player p, String[] message_parts) 
	{
		if ( p.isOp() )
		{
			if ( message_parts.length > 2 )
			{
				if ( message_parts[2].equalsIgnoreCase("active") )
				{
					if ( message_parts.length == 4)
					{
						try
						{
							boolean active = Boolean.parseBoolean(message_parts[3]);
							ConfigManager.setConfigValue(ConfigKeys.BUILT_IN_PERMISSIONS_ENABLED, active);
						}
						catch ( Exception e)
						{
							p.sendMessage("Invalid format - only true and false allowed.");
						}
					}
					p.sendMessage("Permissions active is: " + ConfigManager.getBuiltInPermissionsEnabled() );
				}
				else if ( message_parts[2].equalsIgnoreCase("indiv") )
				{
					if ( message_parts.length == 5)
					{
						try
						{
							PermissionsManager.SetIndividualPermissionLevel(message_parts[3].toLowerCase(), PermissionsManager.PermissionLevel.valueOf(message_parts[4]));
						}
						catch ( Exception e)
						{
							p.sendMessage("Invalid format - /wormhole perms indiv <username> <perm>.");
							p.sendMessage("Valid Permission Levels: ");
							for( PermissionsManager.PermissionLevel level : PermissionsManager.PermissionLevel.values() )
								p.sendMessage(" " + level.toString());
						}
					}
					
					p.sendMessage("Permissions for " + message_parts[3] + ": " + PermissionsManager.GetIndividualPermissionLevel(message_parts[3].toLowerCase()));
				}	
				else if ( message_parts[2].equalsIgnoreCase("default") )
				{
					if ( message_parts.length == 4)
					{
						try
						{
							ConfigManager.setConfigValue(ConfigKeys.BUILT_IN_PERMISSIONS_ENABLED, PermissionLevel.valueOf(message_parts[3]) );
							p.sendMessage("Default Permission is now: " + ConfigManager.getBuiltInDefaultPermissionLevel());
						}
						catch ( Exception e)
						{
							p.sendMessage("Invalid format - /wormhole perms default <perm>");
							p.sendMessage("Valid Permission Levels: ");
							for( PermissionsManager.PermissionLevel level : PermissionsManager.PermissionLevel.values() )
								p.sendMessage(" " + level.toString());
						}
					}
				}
			}
			else
			{
				// /stargate perms indiv    <USERNAME>     <OPTIONAL_SET> (else its a get)
				// /stargate perms group    <GROUPNAME>    <OPTIONAL_SET> (else its a get)
				// /stargate perms default <OPTIONAL_SET> (else a get)
				// /stargate perms active  <OPTIONAL_SET> (else a get)
				p.sendMessage("/wormhole perms indiv    <USERNAME>     <OPTIONAL_SET>");
				//p.sendMessage("/stargate perms indiv    <USERNAME>     <OPTIONAL_SET>");
				p.sendMessage("/wormhole perms default <OPTIONAL_SET>");
				p.sendMessage("/wormhole perms active  <OPTIONAL_SET> (else a get)");
			}
		}
		else
			p.sendMessage("Unable to set permissions unless you are OP. Try \"op <name>\"");
	}
}
