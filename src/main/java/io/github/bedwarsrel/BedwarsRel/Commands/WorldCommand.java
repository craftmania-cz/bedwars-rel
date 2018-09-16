package io.github.bedwarsrel.BedwarsRel.Commands;

import io.github.bedwarsrel.BedwarsRel.MapReseter.MapReseting;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender.hasPermission("skygiants.admin.worldsave")){
            if(args.length == 0){
                sender.sendMessage("§ePouziti prikazu: §f/world <save,reset>");
                return true;
            }
            if(args.length == 1){
                if ((args[0].equals("save"))){
                    Player p = (Player)sender;
                    MapReseting.saveWorld(p.getWorld());
                    p.sendMessage("§aSvet byl uspesne ulozen.");
                    return true;
                }
            } else {
                if ((args[0].equals("reset"))){
                    Player p = (Player)sender;
                    if(MapReseting.worldSaved(args[1]).booleanValue()){
                        World world = Bukkit.getWorld(args[1]);
                        if(world == null){
                            p.sendMessage("§cNelze vyresetovat tento svet, neni ulozeny!");
                            return false;
                        } else {
                            p.kickPlayer("Reset sveta");
                            MapReseting.resetWorld(world);
                            return true;
                        }

                    } else {
                        p.sendMessage("§cNelze vyresetovat tento svet, neni ulozeny!");
                        return true;
                    }
                }
            }
        } else {
            sender.sendMessage("§cNa tuto akci nemas dostatecna prava!");
            return false;
        }
        return true;
    }
}
