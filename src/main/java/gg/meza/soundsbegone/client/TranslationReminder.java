package gg.meza.soundsbegone.client;

import gg.meza.soundsbegone.SoundsBeGoneConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

import java.net.URI;
import java.util.Objects;

public class TranslationReminder {
    public static void notify(Minecraft client) {
        String languageCode = client.getLanguageManager().getSelected().toLowerCase();
            if (languageCode.equals("en_us")) {
            return;
        }

            if(Objects.equals(SoundsBeGoneClient.config.lastVersionSeen(), SoundsBeGoneConfig.VERSION)) {
            return;
        }

        LanguageInfo language = client.getLanguageManager().getLanguage(languageCode);
            if (language == null) {
            return;
        }

            if (client.player != null) {
                URI crowdinUri = URI.create("https://crowdin.com/project/soundsbegone");
                MutableComponent crowdinTooltip = Component.translatable("soundsbegone.cmd.crowdin.tooltip");

                /*? if > 1.21.4 {*/
                ClickEvent.OpenUrl clickEvent = new ClickEvent.OpenUrl(crowdinUri);
                HoverEvent.ShowText showText = new HoverEvent.ShowText(crowdinTooltip);
                /*?} else {*/
                /*ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, crowdinUri.toString());
                HoverEvent showText = new HoverEvent(HoverEvent.Action.SHOW_TEXT, crowdinTooltip);
                *//*?}*/

                Component message = Component.translatable("soundsbegone.cmd.translate", Component.literal("Sounds Be Gone").withStyle(style -> style.withBold(true).withColor(ChatFormatting.GOLD))).withStyle(style -> style.withColor(ChatFormatting.AQUA))
                        .append(Component.literal("\n\n"))
                        .append(Component.translatable("soundsbegone.cmd.crowdin").withStyle(style -> style.withBold(true)
                                .withColor(ChatFormatting.BLUE)
                                .withUnderlined(true)
                                .withHoverEvent(showText)
                                .withClickEvent(clickEvent)));

                //? >= 26.1 {
                client.player.sendSystemMessage(message);
                //?} else
                //client.player.displayClientMessage(message, false);
            SoundsBeGoneClient.config.setLastVersionSeen(SoundsBeGoneConfig.VERSION);
        }
    }
}
