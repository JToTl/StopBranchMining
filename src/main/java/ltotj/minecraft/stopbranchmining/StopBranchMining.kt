package ltotj.minecraft.stopbranchmining

import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.plugin.java.JavaPlugin

class StopBranchMining : JavaPlugin(),Listener {

    val worlds=ArrayList<String>()
    var lightLevel:Byte=0
    var enable=true


    override fun onEnable() {
        // Plugin startup logic
        saveDefaultConfig()
        server.pluginManager.registerEvents(this,this)
        loadConfig()
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    fun loadConfig(){
        worlds.clear()
        worlds.addAll(config.getStringList("worlds"))
        enable=config.getBoolean("enable")
        lightLevel=config.getInt("lightLevel",0).toByte()
    }


    @EventHandler
    fun breakBlock(e:BlockBreakEvent){
        if(!enable)return
        if(worlds.contains(e.block.world.name)){
            val player=e.player
            if(player.location.block.lightFromSky<lightLevel) {
                if (e.block.type == Material.STONE) {
                    e.isCancelled = true
                    e.player.sendMessage("§4露天掘り以外の方法で掘ることはできません!")
                    return
                }
            }
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {

        if(!sender.hasPermission("sbm.admin"))return true

        if(args==null)return true
        if(args.isEmpty())return true
        when(args[0]){
            "on"-> {
                if(enable){
                    sender.sendMessage("既にonです")
                }
                else{
                    sender.sendMessage("onにしました")
                    enable=true
                    config.set("enable",true)
                    saveConfig()
                }
                return true
            }
            "off"->{
                if(enable){
                    sender.sendMessage("offにしました")
                    enable=false
                    config.set("enable",false)
                    saveConfig()
                }
                else{
                    sender.sendMessage("既にoffです")
                }
                return true
            }
            "reload"->{
                loadConfig()
                sender.sendMessage("リロードしました")
                return true
            }
        }
        sender.sendMessage("sbm on/offで切り替え、sbm reloadでコンフィグリロード")
        return true
    }

}