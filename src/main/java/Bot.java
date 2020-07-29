import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Bot {
    static HashMap<Guild, String> prefixes = new HashMap<Guild, String>();

    public static void main(String[] args) throws LoginException, IOException {
        JDABuilder builder = new JDABuilder(AccountType.BOT);        
        String token = new String(Files.readAllBytes(Paths.get("token.txt")));
        builder.setToken("NzM3NTk4MDk1ODg5NTMwOTgx.Xx_rwQ.0Jz2GhsHkT9gaXbWBu3eDXYYSDE");
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("@Game Bot Nut for info"));
        builder.addEventListeners(new Commands());
        builder.build();
    }

    static void setPrefix(Guild guild, String prefix)
    {
        prefixes.put(guild, prefix);
    }

    static String getPrefix(Guild guild)
    {
        if (!prefixes.containsKey(guild))
        {
            return "!";
        }
        return prefixes.get(guild);
    }
}