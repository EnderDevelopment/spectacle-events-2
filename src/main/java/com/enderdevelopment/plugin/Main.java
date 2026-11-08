package com.enderdevelopment.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.FireworkEffect;

public final class Main extends JavaPlugin implements CommandExecutor {
    private BukkitTask showTask;
    private Location center;
    private long tick;
    private boolean previousDaylightCycle;
    private World eventWorld;

    @Override
    public void onEnable() {
        if (getCommand("event") != null) getCommand("event").setExecutor(this);
        getLogger().info("EventShow enabled. Use /event start.");
    }

    @Override
    public void onDisable() {
        stopShow(false);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Component.text("Gebruik /event start of /event stop.", NamedTextColor.YELLOW));
            return true;
        }
        if (args[0].equalsIgnoreCase("start")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Component.text("Alleen een speler kan een event starten.", NamedTextColor.RED));
                return true;
            }
            if (showTask != null) {
                player.sendMessage(Component.text("Er draait al een event. Gebruik /event stop.", NamedTextColor.RED));
                return true;
            }
            startShow(player);
            return true;
        }
        if (args[0].equalsIgnoreCase("stop")) {
            if (showTask == null) sender.sendMessage(Component.text("Er draait geen event.", NamedTextColor.RED));
            else { stopShow(true); sender.sendMessage(Component.text("Het event is gestopt.", NamedTextColor.YELLOW)); }
            return true;
        }
        sender.sendMessage(Component.text("Gebruik /event start of /event stop.", NamedTextColor.YELLOW));
        return true;
    }

    private void startShow(Player starter) {
        center = starter.getLocation().getBlock().getLocation().add(0.5, 1.2, 0.5);
        eventWorld = center.getWorld();
        tick = 0;
        previousDaylightCycle = Boolean.TRUE.equals(eventWorld.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE));
        eventWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        starter.sendMessage(Component.text("Het lange event begint op jouw locatie!", NamedTextColor.LIGHT_PURPLE));
        Bukkit.broadcast(Component.text("✦ Een magisch event begint! ✦", NamedTextColor.GOLD));
        showTask = Bukkit.getScheduler().runTaskTimer(this, this::showStep, 0L, 5L);
    }

    private void stopShow(boolean announce) {
        if (showTask == null) return;
        showTask.cancel();
        showTask = null;
        if (eventWorld != null && eventWorld.isChunkLoaded(center.getBlockX() >> 4, center.getBlockZ() >> 4)) {
            eventWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, previousDaylightCycle);
            eventWorld.setTime(12000L);
            eventWorld.setStorm(false);
            eventWorld.setThundering(false);
        }
        if (announce) Bukkit.broadcast(Component.text("Het event is afgelopen. Bedankt voor het kijken!", NamedTextColor.GREEN));
        center = null;
        eventWorld = null;
    }

    private void showStep() {
        if (center == null || eventWorld == null) { stopShow(true); return; }
        tick += 5;
        if (tick == 5) title("✦ ✦ ✦", "Het spektakel begint!", NamedTextColor.GOLD);
        if (tick < 140) openingParticles();
        else if (tick < 300) potionRain();
        else if (tick < 460) appleAndBook();
        else if (tick < 620) heartGarden();
        else if (tick < 780) waterAndIce();
        else if (tick < 940) forest();
        else if (tick < 1100) stardust();
        else if (tick < 1280) darkInterlude();
        else if (tick < 1480) rainbowFinale();
        else {
            title("✦ Einde ✦", "Bedankt voor het vieren!", NamedTextColor.GREEN);
            stopShow(true);
            return;
        }
        if (tick % 100 == 0) playSound();
    }

    private void openingParticles() {
        ring(Particle.HAPPY_VILLAGER, 3.2, 28, 0.55);
        ring(Particle.END_ROD, 2.2, 18, 1.0);
        if (tick % 20 == 0) title("✦ WELKOM ✦", "Een magische avond begint", NamedTextColor.GOLD);
    }

    private void potionRain() {
        ring(Particle.SPLASH, 2.8, 20, 1.1);
        ring(Particle.WITCH, 3.8, 24, 0.65);
        ring(Particle.DRAGON_BREATH, 1.8, 14, 0.45);
        if (tick % 30 == 0) dropDisplay(Material.POTION);
        if (tick % 50 == 0) title("⚗ Drankjesregen ⚗", "Een bruisende wolk vol magie", NamedTextColor.LIGHT_PURPLE);
    }

    private void appleAndBook() {
        spiral(Particle.COMPOSTER, 2.8, 12);
        ring(Particle.ENCHANT, 3.5, 24, 0.8);
        if (tick % 25 == 0) dropDisplay(tick % 50 == 0 ? Material.ENCHANTED_BOOK : Material.GOLDEN_APPLE);
        if (tick % 50 == 0) title("❖ Appels & boeken ❖", "Kennis en lekkers dwarrelen neer", NamedTextColor.GREEN);
    }

    private void heartGarden() {
        hearts();
        ring(Particle.HEART, 2.0, 12, 1.2);
        ring(Particle.CHERRY_LEAVES, 4.0, 28, 0.7);
        if (tick % 50 == 0) title("♥ Hartjestuin ♥", "Een vrolijke bloesemshow", NamedTextColor.RED);
    }

    private void waterAndIce() {
        ring(Particle.SNOWFLAKE, 3.2, 28, 0.7);
        ring(Particle.BUBBLE_POP, 2.0, 20, 1.3);
        ring(Particle.FALLING_WATER, 4.0, 30, 1.0);
        if (tick % 50 == 0) title("❄ Kristalwaterval ❄", "Glittersneeuw en fonkelend water", NamedTextColor.AQUA);
    }

    private void forest() {
        ring(Particle.HAPPY_VILLAGER, 3.5, 24, 0.6);
        ring(Particle.FALLING_SPORE_BLOSSOM, 4.5, 28, 1.0);
        spiral(Particle.WAX_ON, 2.5, 14);
        if (tick % 50 == 0) title("❀ Betoverd bos ❀", "Lichtjes dansen tussen de bladeren", NamedTextColor.GREEN);
    }

    private void stardust() {
        spiral(Particle.END_ROD, 4.0, 24);
        ring(Particle.FIREWORK, 3.0, 20, 1.0);
        ring(Particle.PORTAL, 2.0, 16, 1.3);
        if (tick % 50 == 0) title("✧ Sterrenstof ✧", "De nacht licht op met magie", NamedTextColor.LIGHT_PURPLE);
        if (tick % 80 == 0) launchFirework();
    }

    private void darkInterlude() {
        if (tick == 1100) {
            eventWorld.setTime(18000L);
            eventWorld.setStorm(true);
            eventWorld.setThundering(false);
            title("☾ De grote verduistering ☾", "Even wordt de wereld stil...", NamedTextColor.DARK_PURPLE);
            Bukkit.broadcast(Component.text("De lucht wordt donker rond het event!", NamedTextColor.DARK_PURPLE));
        }
        ring(Particle.SOUL, 4.0, 20, 0.8);
        ring(Particle.SOUL_FIRE_FLAME, 2.0, 12, 0.8);
        ring(Particle.ASH, 5.0, 24, 0.5);
        if (tick % 70 == 0) launchFirework();
    }

    private void rainbowFinale() {
        if (tick == 1280) {
            eventWorld.setStorm(false);
            eventWorld.setTime(6000L);
            title("☀ De finale begint! ☀", "Licht en kleuren keren terug", NamedTextColor.YELLOW);
        }
        hearts();
        spiral(Particle.END_ROD, 4.5, 28);
        ring(Particle.FIREWORK, 4.0, 28, 1.3);
        ring(Particle.HAPPY_VILLAGER, 3.0, 20, 0.8);
        if (tick % 45 == 0) launchFirework();
        if (tick % 50 == 0) title("✦ GROTE FINALE ✦", "Een explosie van licht en kleur!", NamedTextColor.GOLD);
    }

    private void ring(Particle particle, double radius, int count, double y) {
        double phase = tick * 0.045;
        for (int i = 0; i < count; i++) {
            double angle = (Math.PI * 2 * i / count) + phase;
            Location point = center.clone().add(Math.cos(angle) * radius, y + Math.sin(phase + i * 0.18) * 0.4, Math.sin(angle) * radius);
            eventWorld.spawnParticle(particle, point, 1, 0.08, 0.12, 0.08, 0.01);
        }
    }

    private void spiral(Particle particle, double radius, int count) {
        double phase = tick * 0.12;
        for (int i = 0; i < count; i++) {
            double angle = phase + (Math.PI * 2 * i / count);
            double height = 0.4 + (i % 8) * 0.35;
            Location point = center.clone().add(Math.cos(angle) * radius, height, Math.sin(angle) * radius);
            eventWorld.spawnParticle(particle, point, 1, 0.04, 0.05, 0.04, 0.01);
        }
    }

    private void hearts() {
        double phase = tick * 0.06;
        for (int i = 0; i < 16; i++) {
            double angle = phase + i * Math.PI / 8;
            double x = 16 * Math.pow(Math.sin(angle), 3) / 16;
            double z = -(13 * Math.cos(angle) - 5 * Math.cos(2 * angle) - 2 * Math.cos(3 * angle) - Math.cos(4 * angle)) / 16;
            eventWorld.spawnParticle(Particle.HEART, center.clone().add(x * 3.1, 2.3, z * 3.1), 1);
        }
    }

    private void dropDisplay(Material material) {
        double angle = ThreadLocalRandom.current().nextDouble(Math.PI * 2);
        Location at = center.clone().add(Math.cos(angle) * 2.0, 2.0 + ThreadLocalRandom.current().nextDouble(1.8), Math.sin(angle) * 2.0);
        eventWorld.dropItem(at, new ItemStack(material)).setVelocity(new Vector(0, 0.08, 0));
    }

    private void launchFirework() {
        Location at = center.clone().add(0, 1.0, 0);
        Firework firework = eventWorld.spawn(at, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        Color[] colors = {Color.FUCHSIA, Color.AQUA, Color.LIME, Color.YELLOW, Color.ORANGE};
        Color chosen = colors[ThreadLocalRandom.current().nextInt(colors.length)];
        meta.addEffect(FireworkEffect.builder().withColor(chosen).withFade(Color.WHITE).with(FireworkEffect.Type.BALL_LARGE).trail(true).flicker(true).build());
        meta.setPower(1);
        firework.setFireworkMeta(meta);
    }

    private void playSound() {
        for (Player player : eventWorld.getPlayers()) {
            if (player.getLocation().distanceSquared(center) <= 1600) {
                player.playSound(center, org.bukkit.Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.7f, 0.8f + ThreadLocalRandom.current().nextFloat() * 0.8f);
            }
        }
    }

    private void title(String heading, String subtitle, NamedTextColor color) {
        Title title = Title.title(Component.text(heading, color), Component.text(subtitle, NamedTextColor.WHITE), Title.Times.times(java.time.Duration.ofMillis(250), java.time.Duration.ofMillis(1800), java.time.Duration.ofMillis(400)));
        for (Player player : eventWorld.getPlayers()) {
            if (player.getLocation().distanceSquared(center) <= 2500) player.showTitle(title);
        }
    }
}
