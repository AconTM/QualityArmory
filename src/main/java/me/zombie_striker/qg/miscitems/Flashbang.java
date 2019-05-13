package me.zombie_striker.qg.miscitems;

import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.zombie_striker.qg.QAMain;
import me.zombie_striker.qg.MaterialStorage;
import me.zombie_striker.qg.guns.utils.WeaponSounds;

public class Flashbang extends Grenades {

	public Flashbang(ItemStack[] ingg, double cost, double damage, double explosionreadius, String name,
			String displayname, List<String> lore, MaterialStorage ms) {
		super(ingg, cost, damage, explosionreadius, name, displayname, lore, ms);
	}


	@Override
	public void onLMB(PlayerInteractEvent e, ItemStack usedItem) {
		Player thrower = e.getPlayer();
		QAMain.grenadeItem.put(thrower.getUniqueId(), thrower.getInventory().getItemInMainHand());
		if (throwItems.containsKey(thrower)) {
			thrower.sendMessage(QAMain.prefix + QAMain.S_GRENADE_PALREADYPULLPIN);
			thrower.playSound(thrower.getLocation(), WeaponSounds.RELOAD_BULLET.getSoundName(), 1, 1);
			return;
		}
		thrower.getWorld().playSound(thrower.getLocation(), WeaponSounds.RELOAD_MAG_IN.getSoundName(), 2, 1);
		final ThrowableHolder h = new ThrowableHolder(thrower.getUniqueId(), thrower);
		h.setTimer(new BukkitRunnable() {
			@Override
			public void run() {
				try {
					h.getHolder().getWorld().spawnParticle(org.bukkit.Particle.EXPLOSION_HUGE,
							h.getHolder().getLocation(), 0);
					h.getHolder().getWorld().playSound(h.getHolder().getLocation(), WeaponSounds.FLASHBANG.getSoundName(),
							3f, 1f);
				} catch (Error e3) {
					h.getHolder().getWorld().playEffect(h.getHolder().getLocation(), Effect.valueOf("CLOUD"), 0);
					h.getHolder().getWorld().playSound(h.getHolder().getLocation(), Sound.valueOf("EXPLODE"), 8, 0.7f);
				}
				try {
					for (Entity e : h.getHolder().getNearbyEntities(radius, radius, radius)) {
						if (e instanceof LivingEntity) {
							QAMain.DEBUG("Flashbaned "+e.getName());
							((LivingEntity) e)
									.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 2));
						}
					}
				} catch (Error e) {
				}
				if (h.getHolder() instanceof Player) {
					QAMain.DEBUG("Blinded player");
					((LivingEntity) h.getHolder())
							.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 2));
				}
				if (h.getHolder() instanceof Item) {
					h.getHolder().remove();
				}
				if(QAMain.grenadeItem.containsKey(thrower.getUniqueId())) {
					thrower.getInventory().removeItem(QAMain.grenadeItem.get(thrower.getUniqueId()));
				}
				throwItems.remove(h.getHolder());
			}
		}.runTaskLater(QAMain.getInstance(), 5 * 20));
		throwItems.put(thrower, h);

	}

}
