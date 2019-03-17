package me.andre111.mambience.script;

import java.util.Optional;

import javax.script.Bindings;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.property.block.GroundLuminanceProperty;
import org.spongepowered.api.data.property.block.SkyLuminanceProperty;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.weather.Weathers;

import me.andre111.mambience.player.MAPlayer;
import me.andre111.mambience.scan.BlockScanner;

public class VariablesSponge extends Variables {
	@Override
	public void updateVariables(MAPlayer maplayer) {
		BlockScanner scanner = maplayer.getScanner();
		MAScriptEngine se = maplayer.getScriptEngine();
		
		Optional<Player> optPlayer = Sponge.getServer().getPlayer(maplayer.getPlayerUUID());
		if(optPlayer.isPresent()) {
			Player player = optPlayer.get();
			World world = player.getWorld();
			Location<World> location = player.getLocation();
			int x = location.getBlockX();
			int y = location.getBlockY();
			int z = location.getBlockZ();
			BlockState headBlock = player.getLocation().add(0, 1.62, 0).getBlock();
			
			boolean exposed = fastExposedCheck(location);
			//TODO: Slower more accurate exposed check
			//if(exposed) exposed = slowExposedCheck(location);
			
			//Light values are got by location in Sponge!
			int skylight = 0;
			Optional<SkyLuminanceProperty> sl = location.getProperty(SkyLuminanceProperty.class);
			if(sl.isPresent()) {
				skylight += sl.get().getValue();
			}
			
			int blocklight = 0;
			Optional<GroundLuminanceProperty> bl = location.getProperty(GroundLuminanceProperty.class);
			if(bl.isPresent()) {
				blocklight += bl.get().getValue();
			}
			
			//TODO: There needs to be some way of getting the actual current lightvalue of the block
			int light = blocklight;
			if(world.getProperties().getWorldTime()<12000) {
				if(skylight>light) light = skylight;
			}
			
			String biome = world.getBiome(x, 0, z).getId();
			if(!biome.contains(":")) biome = "minecraft:"+biome;
			
			Bindings bd = se.getEngineScopeBindings();
			//PLAYER
			{
				bd.put("__px", x);
				bd.put("__py", y);
				bd.put("__pz", z);
				//TODO: Player Dimension
				//bd.put("__pdim", );
				bd.put("__psunl", skylight);
				bd.put("__pblockl", blocklight);
				bd.put("__pl", light);
				//TODO: CanSeeSky, CanRainOn
				//bd.put("__pseesky", );
				//bd.put("__prainon", );
				bd.put("__pboat", (player.getVehicle().isPresent() && player.getVehicle().get().getType() == EntityTypes.BOAT));
				bd.put("__psubm", (headBlock.getType()==BlockTypes.WATER || headBlock.getType()==BlockTypes.FLOWING_WATER));
				bd.put("__pexpo", exposed);
			}
			//WORLD
			{
				bd.put("__wt", world.getProperties().getWorldTime()%24000);
				bd.put("__wrain", world.getWeather().equals(Weathers.RAIN) || world.getWeather().equals(Weathers.THUNDER_STORM));
				bd.put("__wmoon", ((world.getProperties().getWorldTime()/24000) % 8));
				bd.put("__wbiome", biome);
			}
			//SCANNER
			{
				bd.put("__sblocks", scanner.getScanBlockCount());
				bd.put("__sbiomes", scanner.getScanBiomeCount());
				bd.put("__savgskylight", scanner.getAverageSkyLight());
				bd.put("__savglight", scanner.getAverageLight());
				bd.put("__savgtemp", scanner.getAverageTemperature());
				bd.put("__savghum", scanner.getAverageHumidity());
			}
			se.invokeFunction("Internal_Variables");
		}
	}
	
	private static boolean fastExposedCheck(Location<World> location) {
		int mx = location.getBlockX() + 1;
        int my = location.getBlockY() + 1;
        int mz = location.getBlockZ() + 1;
        
        for (int cx = mx - 2; cx <= mx; cx++) {
            for (int cy = my - 2; cy <= my; cy++) {
                for (int cz = mz - 2; cz <= mz; cz++) {
                	Location<World> block = location.getExtent().getLocation(cx, cy, cz);
                    
                    Optional<SkyLuminanceProperty> sl = block.getProperty(SkyLuminanceProperty.class);
					if(sl.isPresent()) {
						if(sl.get().getValue()>0.1) 
							return true;
					}
                }
            }
        }
        return false;
	}
	//private static boolean slowExposedCheck(Location location) {
	//	
	//}
}
