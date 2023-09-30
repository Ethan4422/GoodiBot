package me.goodi.externalcontrol.mixin;


import me.goodi.externalcontrol.ExternalControl;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class Death {

    @Shadow public abstract Text getName();

    @Unique
    ExternalControl control;

    @Inject(method="onDeath", at=@At("TAIL"))
    public void death(DamageSource damageSource, CallbackInfo ci){
        TextChannel channel = control.guild().getTextChannelsByName("ingame-chat", true).get(0);
        channel.sendMessage(this.getName().getString() + " Death by" + damageSource).queue();
    }

}
