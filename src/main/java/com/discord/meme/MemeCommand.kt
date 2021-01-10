package com.discord.meme

import com.beust.klaxon.Klaxon
import com.discord.BaseCommand
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import org.json.JSONObject

class MemeCommand : BaseCommand() {


    override fun execute(event: GuildMessageReceivedEvent, a: Array<String>): Boolean {

        val args = a.joinToString(" ").substring(5).split(";");

        val formData = mutableMapOf(
                "template_id" to (Meme.forIdentity(args[0]) ?: Meme.BRAIN).id.toString(),
                "username" to "ginormousboi",
                "password" to "canidoit"
        );
        for(i in 1 until args.size) {
            formData["boxes[" + (i-1) + "][text]"] = args[i];
        }

        val obj = khttp.post(url = "https://api.imgflip.com/caption_image",
                data = formData).jsonObject;

        val data = obj["data"] as JSONObject;
        val url = data["url"];

        textChannel.sendMessage(url.toString()).queue();

        return true
    }

    fun trial(): String {
        return when("you're dog") {
            "you're dog" -> "when you're dog, you're dog"
            else -> "when you're dog, you're not dog"
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

        }
    }
}

enum class Meme(val identifier: String, val id: Int) {
    BRAIN("brain", 93895088),
    DRAKE("drake", 181913649);

    companion object {
        fun forIdentity(identifier: String) : Meme? {
            return enumValues<Meme>().filter { identifier == it.identifier }.firstOrNull();
        }
    }
}
