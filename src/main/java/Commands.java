import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import com.vdurmont.emoji.EmojiManager;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Commands extends ListenerAdapter {
    HashMap<User, Game> games = new HashMap<User, Game>();
    ArrayList<String> commandsPrefix = new ArrayList<String>(Arrays.asList("play", "continue", "stop"));
    ArrayList<String> commandsNoPrefix = new ArrayList<String>(Arrays.asList("w", "a", "s", "d", "up", "left", "down", "right", "r"));
    public void onGuildLeave(GuildLeaveEvent event) //removes bot's stored prefix for a server if removed from that server
    {
        if (Bot.prefixes.containsKey(event.getGuild()))
        {
            Bot.prefixes.remove(event.getGuild());
        }
    }
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        if (event.getAuthor().isBot() && event.getMessage().getEmbeds().get(0).getTitle().charAt(0) == 'L')
        {
            event.getMessage().addReaction("U+2B05").queue();
            event.getMessage().addReaction("U+27A1").queue();
            event.getMessage().addReaction("U+2B06").queue();
            event.getMessage().addReaction("U+2B07").queue();
            event.getMessage().addReaction("U+1F504").queue();
            if (games.containsKey(event.getJDA().getUserById(event.getMessage().getEmbeds().get(0).getFields().get(0).getValue().substring(10, event.getMessage().getEmbeds().get(0).getFields().get(0).getValue().length() - 1))))
            {
                games.get(event.getJDA().getUserById(event.getMessage().getEmbeds().get(0).getFields().get(0).getValue().substring(10, event.getMessage().getEmbeds().get(0).getFields().get(0).getValue().length() - 1))).setGameMessage(event.getMessage());
            }
            return;
        }

        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if (args[0].toLowerCase().equals(Bot.getPrefix(event.getGuild()) + "prefix"))
        {
            if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                if (args.length == 2 && args[1].length() == 1) {
                    Bot.setPrefix(event.getGuild(), args[1].toLowerCase());
                    event.getChannel().sendMessage("Prefix successfully changed to ``" + Bot.getPrefix(event.getGuild()) + "``.").queue();
                } else {
                    event.getChannel().sendMessage("``" + args[1] + "`` is not a valid prefix!").queue();
                }
                event.getMessage().delete().queue();
            }
            else
            {
                event.getChannel().sendMessage(event.getAuthor().getAsMention() + ", you do not have permission to use this command.").queue();
            }
        }
        else if ((commandsNoPrefix.contains(args[0].toLowerCase())) || (Character.toString(args[0].toLowerCase().charAt(0)).equals(Bot.getPrefix(event.getGuild())) && commandsPrefix.contains(args[0].toLowerCase().substring(1))))
        {
            if (!games.containsKey(event.getAuthor()))
            {
                games.put(event.getAuthor(), new Game(event.getAuthor()));
            }
            String userInput = args[0].toLowerCase();
            if (Character.toString(userInput.charAt(0)).equals(Bot.getPrefix(event.getGuild())))
            {
                userInput = userInput.substring(1, userInput.length());
            }
            if (!games.get(event.getAuthor()).gameActive && userInput.equals("play") && args.length == 2 && EmojiManager.isEmoji(args[1]))
            {
                System.out.println(args[1]);
                games.get(event.getAuthor()).setPlayerEmote(args[1]);
            }
            games.get(event.getAuthor()).run(event.getGuild(), event.getChannel(), userInput);
            if (userInput.equals("stop")) //remove game from hashmap when player quits
            {
                if (games.containsKey(event.getAuthor()))
                {
                    games.remove(event.getAuthor());
                }
            }
            event.getMessage().delete().queue();
        }
        else if (args[0].toLowerCase().equals(Bot.getPrefix(event.getGuild()) + "info") || event.getMessage().getMentionedUsers().get(0).equals(event.getJDA().getSelfUser()))
        {
            event.getChannel().sendMessage(info(event.getGuild()).build()).queue();
            event.getMessage().delete().queue();
        }
    }
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if (event.getMember().getUser().isBot())
        {
            return;
        }
        if (!games.containsKey(event.getMember().getUser()))
        {
            games.put(event.getMember().getUser(), new Game(event.getMember().getUser()));
        }
        boolean reactionCommand = true;
        String userInput = "";
        switch (event.getReactionEmote().toString())
        {
            case "RE:U+2b05":
                userInput = "left";
                break;
            case "RE:U+27a1":
                userInput = "right";
                break;
            case "RE:U+2b06":
                userInput = "up";
                break;
            case "RE:U+2b07":
                userInput = "down";
                break;
            case "RE:U+1f504":
                userInput = "r";
                break;
            default:
                reactionCommand = false;
        }
        if (reactionCommand)
        {
            games.get(event.getMember().getUser()).run(event.getGuild(), event.getChannel(), userInput);
        }
        event.getReaction().removeReaction(event.getUser()).queue();
    }
    EmbedBuilder info(Guild guild)
    {
        EmbedBuilder info = new EmbedBuilder();
        info.setTitle("Game Bot Nut");
        info.setThumbnail("https://cdn.discordapp.com/avatars/737598095889530981/ae71e431015b82d3210fb9b2cafc0f37.png?size=128");
        info.setDescription("Game Bot Nut is Bot that lets you use disabled people to push your car!");
        info.setColor(0xdd2e53);
        info.addField("How to Play", "You are a **Wheelchair Man** :man_in_motorized_wheelchair:.\nYour job is to push **Cars** :race_car: on top of their **destinations** :checkered_flag:.", false);
        info.addField("Features", ":white_small_square:**Infinite levels**\nThe maps in Game Bot Nut are randomly generated, increasing in difficulty as you play.\n:white_small_square:**Varied controls**\nGame Bot Nut has to be played by pressing the reactions to move.\n:white_small_square:**Custom prefixes**\nTo prevent Sokobot from conflicting with other bots, admins can choose any single-character prefix to preface Game Bot Nuts commands.", false);
        info.addField("Commands", ("``" + Bot.getPrefix(guild) + "play`` can be used to start a game if you are not currently in one.\n``" + Bot.getPrefix(guild) + "stop`` can be used to stop your active game at any time.\n``" + Bot.getPrefix(guild) + "info`` provides some useful details about the bot and rules of the game.\n``"+ Bot.getPrefix(guild) + "prefix [character]`` can be used to change the prefix the bot responds to."), false);
        return info;
    }
    public static void sendGameEmbed(MessageChannel channel, String level, String game, User user)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Level " + level);
        embed.setDescription(game);
        embed.addField("Enter direction (``up``, ``down``, ``left``, ``right``/``wasd``) or ``r`` to reset", "Player: " + user.getAsMention(), false);
        channel.sendMessage(embed.build()).queue();
    }
    public static void updateGameEmbed(Message message, String level, String game, User user)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Level " + level);
        embed.setDescription(game);
        embed.addField("Enter direction (``up``, ``down``, ``left``, ``right``/``wasd``) or ``r`` to reset", "Player: " + user.getAsMention(), false);
        message.editMessage(embed.build()).queue();
    }
    public static void sendWinEmbed(Guild guild, Message message, String level)
    {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("You win!");
        embed.setDescription("Type ``" + Bot.getPrefix(guild) + "continue`` to continue to Level " + level + " or ``" + Bot.getPrefix(guild) + "stop`` to quit ");
        embed.setFooter("You can also press any reaction to continue.");
        message.editMessage(embed.build()).queue();
    }
}
