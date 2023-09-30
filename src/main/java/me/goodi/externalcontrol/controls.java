package me.goodi.externalcontrol;

import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import static me.goodi.externalcontrol.ExternalControl.*;

public class controls extends ListenerAdapter {

    MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getAuthor().isBot()) return;
        if(!e.getChannel().getName().toLowerCase().equals("ingame-chat") || !e.getChannel().asTextChannel().getType().isGuild())return;

        String msg = e.getMessage().getContentRaw();
        String[] args = msg.split(" ");

        if (args[0].equalsIgnoreCase("jump")) {
            jump();
        }
        if (args[0].equalsIgnoreCase("guildid")) {
            e.getChannel().sendMessage(e.getGuild().getId()).queue();
        }
        if (e.getMessage().getContentRaw().equals("holdjump")) {
            mc.options.jumpKey.setPressed(true);
        }

        if (e.getMessage().getContentRaw().equals("random")) {
            assert mc.player != null;
            mc.player.getActiveItem().use(mc.player.getWorld(), mc.player, mc.player.getActiveHand());
        }

        if(args[0].equalsIgnoreCase("back")){
            mc.player.closeScreen();
        }

        if(args[0].equalsIgnoreCase("respawn")){
            mc.player.requestRespawn();
        }


        if (args[0].equalsIgnoreCase("chat")) {
            String chatmsg = e.getMessage().getContentRaw().substring(args[0].length() + 1);
            mc.player.networkHandler.sendChatMessage(chatmsg);
        }

        if (args[0].equalsIgnoreCase("command")) {
            String command = e.getMessage().getContentRaw().substring(args[0].length() + 1);
            mc.player.networkHandler.sendCommand(command);
        }

        if (args[0].equalsIgnoreCase("disconnect")) {
            String reason = e.getMessage().getContentRaw().substring(args[0].length() + 1);
            mc.getNetworkHandler().getConnection().disconnect(Text.of(reason));
        }

        if (args[0].equalsIgnoreCase("join")) {
            String ip = e.getMessage().getContentRaw().substring(args[0].length() + 1);
            serverConnect(ip);
        }

        if (args[0].equalsIgnoreCase("ss")) {
            screenShot(e.getChannel());

        }

        if(args[0].equalsIgnoreCase("raytrace")){
            double distance = Integer.parseInt(args[1]);
            boolean b = Boolean.getBoolean(args[2]);
            assert mc.player != null;
            HitResult result = mc.player.raycast(distance, 1, b);
            if(result.getType() == HitResult.Type.MISS)return;
            if(result.getType() == HitResult.Type.ENTITY)return;
            if(result.getType() == HitResult.Type.BLOCK){
                e.getChannel().sendMessage(mc.player.getWorld().getBlockState(BlockPos.ofFloored(result.getPos())).getBlock().asItem().getName().getString()).queue();
            }
        }



        if(args[0].equals("back")){
            mc.options.jumpKey.setPressed(true);
        }

        if(args.length == 4){
            if(args[0].equalsIgnoreCase("getBlock")){
                assert mc.player != null;
                int x = mc.player.getBlockX() + Integer.parseInt(args[1]);
                int y = mc.player.getBlockY() + Integer.parseInt(args[2]);
                int z = mc.player.getBlockZ() + Integer.parseInt(args[3]);
                BlockPos blockPos = BlockPos.ofFloored(x,y,z);
                e.getChannel().sendMessage(mc.player.getWorld().getBlockState(blockPos).getBlock().asItem().getName().getString()).queue();
            }
        }
    }


    public void screenShot(MessageChannelUnion channel){
        Random random = new Random();
        Framebuffer framebuffer = client.getFramebuffer();
        int filecode = random.nextInt(99999);
        ScreenshotRecorder.saveScreenshot(client.runDirectory, "File" + filecode+".png", framebuffer, simpleLogConsumer);

        File f = new File(client.runDirectory + "\\screenshots\\File" + filecode + ".png");

        new Thread(() ->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException iex) {
                throw new RuntimeException(iex);
            }
            channel.sendMessage("ImGoodi").addFiles(FileUpload.fromData(f)).queue();
            if(f.exists()){
                try {
                    f.delete();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }).start();
    }


        public void jump(){
        if(mc.getServer()==null) return;
        mc.player.jump();
    }

}
