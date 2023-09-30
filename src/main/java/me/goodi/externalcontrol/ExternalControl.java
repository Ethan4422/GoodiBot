package me.goodi.externalcontrol;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.function.Consumer;

public class ExternalControl implements ModInitializer {

    public final String TOKEN = "MTE1NjQwNzUwNDY0MTcyNDUzMA.GzDE6R.c1aCteYX9VfGH_1mOf33DKhRaLLbQBdQRbTBTk";
    public static MinecraftClient client = MinecraftClient.getInstance();
    public static final Logger LOGGER = LogManager.getLogger("goodi");


    JDA jda = JDABuilder.createDefault(TOKEN)
            .enableIntents(GatewayIntent.MESSAGE_CONTENT)
            .build();




    @Override
    public void onInitialize() {
        jda.addEventListener(new controls());
    }

    public static Consumer<Text> simpleLogConsumer = value -> {
        LOGGER.info(value);
    };

    public static void serverConnect(String address) {
        client.execute(() -> {
            serverDisconnect();
            MultiplayerScreen mpScreen = new MultiplayerScreen(new TitleScreen());
            ServerAddress serverAddress = ServerAddress.parse(address);
            ServerInfo server = new ServerInfo("", address, false);
            ConnectScreen.connect(mpScreen, client, serverAddress, server, true);
        });
    }

    public static void serverDisconnect() {
        client.execute(() -> {
            if (client.getNetworkHandler() != null) {

                client.getNetworkHandler().onDisconnect(new DisconnectS2CPacket(Text.of("Disconnected")));
            }
        });
    }


    public Guild guild() {
        return jda.getGuildById(1131077050116091954L);
    }


    public JDA getJda() {
        return jda;
    }


}
