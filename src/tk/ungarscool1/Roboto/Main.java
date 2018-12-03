package tk.ungarscool1.Roboto;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.server.ServerUpdater;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.user.UserStatus;
import org.javacord.api.util.logging.ExceptionLogger;
import tk.ungarscool1.Roboto.eval.Evaluate;
import tk.ungarscool1.cachet2bot.Stats;
import tk.ungarscool1.cachet2bot.Status;

import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    public static boolean containsIgnoreCase(String str, String searchStr)
    {
        if ((str == null) || (searchStr == null)) {
            return false;
        }
        int len = searchStr.length();
        int max = str.length() - len;
        for (int i = 0; i <= max; i++) {
            if (str.regionMatches(true, i, searchStr, 0, len)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws Exception {


        AtomicBoolean maintenance = new AtomicBoolean(false);

        final Properties[] propritete = {new Properties()};
        propritete[0].load(new FileInputStream("bot.properties"));
        String token = propritete[0].getProperty("token");
        
        // Vote system
        HashMap<User,Integer> inCreateVote = new HashMap<>();
        HashMap<User,String> voteName = new HashMap<>();
        HashMap<User,String> voteContent = new HashMap<>();
        HashMap<User,String> voteType = new HashMap<>();
        HashMap<User,String> voteOpt1 = new HashMap<>();
        HashMap<User,String> voteOpt2 = new HashMap<>();
        HashMap<User,String> voteOpt3 = new HashMap<>();
        // End of vote sys
        DiscordAnex DiscordAnex = new DiscordAnex();
        int startyMdhms[] = {DiscordAnex.year(),DiscordAnex.month(),DiscordAnex.day(),DiscordAnex.hours(),DiscordAnex.minutes(),DiscordAnex.seconds()};
        LocalDate start = LocalDate.now();
        String version = "3.6 Alpha";
        // Statistics
        String statis[] = {propritete[0].getProperty("statistic"), propritete[0].getProperty("apiToken"), propritete[0].getProperty("statisticUrl"), propritete[0].getProperty("metricId")};

        Stats stats = new Stats(statis);

        new DiscordApiBuilder().setToken(token).login().thenAccept((DiscordApi api) -> {
            Collection<Server> nbrServ= api.getServers();
            final String[] lastGamePlayed = {""};

            if (maintenance.get()) {
                api.updateActivity("Maintenance en cours !");
                api.updateStatus(UserStatus.DO_NOT_DISTURB);
            } else {
                api.updateActivity(ActivityType.WATCHING, nbrServ.size()+" servers");
            }
            stats.setBotStatus(Status.OPERATIONAL);
            api.addMessageCreateListener(event -> {
                String message = event.getMessage().getContent();
                MessageAuthor author = event.getMessage().getAuthor();
                // Message
                boolean isPrivate = false;
                Server msgSrv = null;
                if (event.getChannel().asPrivateChannel().isPresent()) {
                    isPrivate = true;
                } else {
                    Optional<Server> msgSrv32 = event.getServer();
                    msgSrv = msgSrv32.get();
                }
                EmbedBuilder embed = new EmbedBuilder();
                /**
                 *
                 * If Player is Ban / Mute
                 *
                 */
                if (!isPrivate&&msgSrv.getRolesByName("RobMute").get(0).getUsers().toString().contains(author.getName())||!isPrivate&&msgSrv.getRolesByName("RobBan").get(0).getUsers().toString().contains(author.getName())) {
                    event.deleteMessage();
                    return;
                }

                embed.setFooter("Powered by Roboto v."+version,api.getYourself().getAvatar());
                boolean isAdmin = false;
                Role adminRole = api.getRoleById("412671208199553035").get();

                if (adminRole.getUsers().toString().contains(author.getName())) isAdmin=true;
                if (message.equalsIgnoreCase("@maintenance")&&isAdmin) {
                    embed.setTitle("Maintenance");
                    if (maintenance.get()) {
                        embed.setColor(Color.GREEN).setDescription("Maintenance désactivée");
                        api.updateStatus(UserStatus.ONLINE);
                        api.updateActivity(lastGamePlayed[0]);
                        maintenance.set(false);
                    } else {
                        embed.setColor(Color.RED).setDescription("Maintenance activée");
                        lastGamePlayed[0] = api.getActivity().get().getName();
                        api.updateStatus(UserStatus.DO_NOT_DISTURB);
                        api.updateActivity("Maintenance en cours !");
                        maintenance.set(true);
                    }
                    event.getChannel().sendMessage(embed);
                    return;
                }
                if (maintenance.get()) {
                    System.out.println("Message: "+message+" (length: "+message.length()+")");
                    if (message.indexOf("!")==0) {
                        String funfact;
                        int funfactnbr = 1 + (int)(Math.random() * 15.9D);
                        if (funfactnbr==1) {
                            funfact = "qu'un developpeur qui ne veut pas apprendre n'est pas un bon dev !";
                        } else if (funfactnbr==2) {
                            funfact = "que le JavaScript est le language le plus utilisé en 2018 !";
                        }  else if (funfactnbr==3) {
                            funfact = "que ce bot discord est fait en Java !";
                        }
                        else if (funfactnbr==4) {
                            funfact = "que mon créateur est un peu con sur les bords !";
                        }  else if (funfactnbr==5) {
                            funfact = "que le plus vieux calendrier du monde date d’il y a 10 000 ans !";
                        } else if (funfactnbr==6) {
                            funfact = "que le relais de la flamme olympique a été inventé par les nazis !";
                        } else if (funfactnbr==7) {
                            funfact = "que les lapins n'aiment pas les carottes !";
                        } else {
                            funfact = "que chaque maintenance améliore un programme";
                        }

                        embed.setColor(Color.ORANGE).setTitle("Maintenance en cours").setDescription("Une maintenance est en cours... Mais elle n'est jamais trop longue !\nLe saviez-vous "+funfact);
                        event.getChannel().sendMessage(embed);
                        return;
                    }
                }
                if (message.equalsIgnoreCase("@ping")&&isAdmin) {
                    stats.sendStats();
                    embed.setTitle("Ping");
                    embed.setColor(Color.GREEN);
                    embed.addField("Bot response: ","Pong");

                    event.getChannel().sendMessage(embed);
                }

                if (!isPrivate&&message.equalsIgnoreCase("@info")&&isAdmin) {
                    stats.sendStats();
                    embed.setColor(Color.GREEN);
                    embed.setTitle(msgSrv.getName()+" - Information");
                    List<User> members = (List<User>) msgSrv.getMembers();
                    List<Role> roles = msgSrv.getRoles();
                    ArrayList memb = new ArrayList();
                    ArrayList role = new ArrayList();
                    for (int i=1; i<members.size(); i++) {
                        memb.add(members.get(i).getName());
                    }
                    String membreSTR = memb.toString();
                    membreSTR = membreSTR.substring(1,membreSTR.indexOf("]"));
                    embed.addField("Membres:",membreSTR);
                    for (int i=1; i<roles.size(); i++) {
                        role.add(roles.get(i).getName());
                    }
                    membreSTR = role.toString();
                    membreSTR = membreSTR.substring(1,membreSTR.indexOf("]"));
                    embed.addField("Rôles:",membreSTR);
                    event.getChannel().sendMessage(embed);
                }

                if (message.equalsIgnoreCase("!todo")) {
                    stats.sendStats();
                    embed.setTitle("To do List");
                    embed.setColor(Color.GREEN);
                    embed.addField("Lien","https://trello.com/b/IAKVpxXC");
                    event.getChannel().sendMessage(embed);
                }

                if (containsIgnoreCase(message,"@game")&&isAdmin) {
                    stats.sendStats();
                    String arg = message;
                    String type = null;
                    embed.setTitle("Changer le jeu");
                    System.out.println("Length:"+arg.length());
                    if (message.equalsIgnoreCase("@game")||message.equalsIgnoreCase("@game ")||message.length()==5) {
                        embed.addField("Syntax:","@game <jeu> [--mode=1,2,3,4]");
                        embed.setColor(Color.red);
                        arg = "";
                    } else {
                        if (message.contains("--")) {
                            type=message.substring(arg.indexOf("--")+2);
                            arg = message.substring(message.indexOf(" ")+1,message.indexOf("--"));
                            System.err.println("Type="+type);
                        } else {
                            arg = message.substring(message.indexOf(" ")+1);
                            type = "PLAYING";
                        }


                    }
                    if (arg.length()>1) {
                        if (containsIgnoreCase(type,"playing")||containsIgnoreCase(type,"mode=0")||type==null) {
                            api.updateActivity(ActivityType.PLAYING,arg);
                            embed.setDescription("Je joue maintenant à "+arg);
                            embed.setColor(Color.GREEN);
                        } else
                        if (containsIgnoreCase(type,"WATCHING") || containsIgnoreCase(type,"mode=3")) {
                            api.updateActivity(ActivityType.WATCHING,arg);
                            embed.setDescription("Je regarde maintenant "+arg);
                            embed.setColor(Color.GREEN);
                        } else
                        if (containsIgnoreCase(type,"STREAMING") || containsIgnoreCase(type,"mode=1")) {
                            api.updateActivity(ActivityType.STREAMING,arg);
                            api.updateActivity(arg,"https://www.twitch.tv/ungarscool1");
                            embed.setDescription("Je stream maintenant "+arg);
                            embed.setColor(Color.GREEN);
                        } else
                        if (containsIgnoreCase(type,"LISTENING") || containsIgnoreCase(type,"mode=2")) {
                            api.updateActivity(ActivityType.LISTENING,arg);
                            embed.setDescription("Je stream maintenant "+arg);
                            embed.setColor(Color.GREEN);
                        } else {
                            embed.addField("Syntax:","@game <jeu> [--PLAYING,--WATCHING,--STREAMING,--LISTENING]");
                            embed.setColor(Color.red);
                        }
                    }

                    event.getChannel().sendMessage(embed);
                }

                if (message.equalsIgnoreCase("!help")||message.equalsIgnoreCase("!aides")) {
                    stats.sendStats();
                    embed.setTitle("Aides");
                    embed.setColor(Color.YELLOW);
                    embed.addField("!blague ","Pour une blague, parmis 20 blagues");
                    embed.addField("!ver","Avoir la version du bot");
                    embed.addField("!help ou !aides","Afficher cette aide");
                    embed.addField("!nouveauté","Pour voir les dernière modif de Roboto");
                    embed.addField("!vacance","Pour afficher le temps restant avant les prochaines vacances");
                    embed.addField("!vacances","Pour afficher le temps restant avant les prochaines vacances");
                    embed.addField("!bac","Pour afficher le temps restant avant le bac");
                    embed.addField("!todo","Affiche les fonctions à mettre avant la release");
                    embed.addField("!createvote","Permet de créer un nouveau vote");
                    event.getChannel().sendMessage(embed);
                }

                if (message.equalsIgnoreCase("!ver")||message.equalsIgnoreCase("!version")) {
                    stats.sendStats();
                    embed.setTitle("Information de "+api.getYourself().getNickname(msgSrv).orElse(api.getYourself().getName()));
                    embed.setColor(Color.YELLOW);
                    embed.addField("Version: ",version);

                    try {
                        embed.addField("Créateur: ","<@113616829481484288>");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    embed.addField("API:","v.3.0.1");
                    embed.addField("!nouveauté:","Pour voir les dernière modif de Roboto");
                    event.getChannel().sendMessage(embed);
                }

                if (message.equalsIgnoreCase("!nouveauté")||message.equalsIgnoreCase("!changelog")) {
                    stats.sendStats();
                    embed.setTitle("Nouveauté");
                    embed.setColor(Color.YELLOW);
                    embed.setDescription("```diff\n" +
                            "+ Télémétrie\n"+
                            "```");
                    event.getChannel().sendMessage(embed);
                }
                if (message.equalsIgnoreCase("!bac")) {
                    stats.sendStats();
                    event.getChannel().sendMessage(DiscordAnex.bac());
                }

                if (containsIgnoreCase(message,api.getYourself().getMentionTag())&&!containsIgnoreCase(message,"@exec")) {
                    stats.sendStats();
                    if (author.isYourself()) {
                            /*embed.setColor(Color.RED);
                            embed.setTitle("Roboto AI - Erreur :/");
                            embed.setDescription("Vous ne pouvez pas executer l'IA avec @exec");
                            event.getChannel().sendMessage(embed);*/
                    } else {
                        if (containsIgnoreCase(message, "c'est") && containsIgnoreCase(message, "pas bien") || containsIgnoreCase(message, "c'est") && containsIgnoreCase(message, "mal")) {
                            if (author.getName().equalsIgnoreCase("Ungarscool1"))
                                event.getChannel().sendMessage("Pardon papa :frowning:");
                            else
                                event.getChannel().sendMessage("Qu'est ce que ça peut te faire foutre pd :middle_finger:");
                        } else if (containsIgnoreCase(message,"monte et qui descend")||containsIgnoreCase(message,"descend et qui monte")) {
                            if (author.getName().equalsIgnoreCase("Ungarscool1"))
                                event.getChannel().sendMessage("Un monte charge dans un monte charge...\nJe sais c'est de la merde ");
                            else
                                event.getChannel().sendMessage("Ta mère dans un ascenseur");
                        } else if (containsIgnoreCase(message,"blague")) {
                            String[] blague = DiscordAnex.blague();
                            if (author.getName().equalsIgnoreCase("Ungarscool1")||containsIgnoreCase(message,"stp")||containsIgnoreCase(message,"s'il te plaît")||containsIgnoreCase(message,"svp")) {
                                event.getChannel().sendMessage(blague[0]);
                                try {
                                    TimeUnit.SECONDS.sleep(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                event.getChannel().sendMessage(blague[1]);
                            } else {
                                event.getChannel().sendMessage(author.asUser().get().getMentionTag()+" le stp c'est gratuit connard");
                            }
                        }

                    }
                }

                if (containsIgnoreCase(message,"!vacance")||containsIgnoreCase(message,"!vacances")) {
                    stats.sendStats();
                    String arg = message;
                    if (arg.length()==9) {
                        embed.setTitle("Vacances");
                        embed.setColor(Color.RED);
                        embed.addField("Syntax:","!vacances <petite/grande>");
                        embed.addField("grande","Pour les grandes vacances");
                        embed.addField("petite","Pour les petites vacances");
                        event.getChannel().sendMessage(embed);
                        return;
                    }
                    arg = arg.substring(10);
                    System.out.println(arg);
                    if (containsIgnoreCase(arg,"grande")) {
                        event.getChannel().sendMessage(DiscordAnex.prochaineGdVacances());
                        return;
                    } else if (containsIgnoreCase(arg,"petite")) {
                        event.getChannel().sendMessage(DiscordAnex.prochaineVacances(author.getName()));
                        return;
                    }



                }

                if (containsIgnoreCase(message,"@exec")) {
                    stats.sendStats();
                    if (!author.isBotOwner()) {
                        embed.setTitle("Tu n'as pas le droit de faire ça").setColor(Color.RED);
                        event.getChannel().sendMessage(embed);
                        return;
                    }
                    System.err.println("Message length: "+message.length());
                    if(containsIgnoreCase(message,"speak")) {

                        if (message.length()==11) {
                            embed.setTitle("@exec speak - Help");
                            embed.setColor(Color.RED);
                            embed.addField("Syntax","@exec speak <serveur> <salon> <type de message> <message>");
                            embed.addField("<serveur>","L'id du serveur");
                            embed.addField("<salon>","Le nom du salon");
                            embed.addField("<type de message>","message ou embed\n Message permet de faire un message standard.\nEmbed permet de faire un message comme celui ci");
                            embed.addField("<message>","Le contenu de votre message");
                            msgSrv.getMemberById(author.getId()).get().sendMessage(embed);
                        } else {
                            String[] arg = message.split(" ");
                            String serveur = arg[2];
                            String channel = arg[3];
                            String msgType = arg[4];
                            StringBuilder sb = new StringBuilder();
                            for (int i = 5; i < arg.length; i++) {
                                sb.append(arg[i]).append(" ");
                            }
                            String value = sb.toString().trim();

                            Optional<Server> serverOptional = api.getServerById(serveur);
                            Server serveur0 = serverOptional.get();
                            TextChannel channel0 = (TextChannel) serveur0.getChannelsByName(channel).get(0);
                            if (msgType.equalsIgnoreCase("message")) {
                                channel0.sendMessage(value);
                                event.getChannel().sendMessage("Votre message à été envoyé");
                            } else if (msgType.equalsIgnoreCase("embed")) {
                                embed.setTitle("");
                                embed.setColor(Color.CYAN);
                                embed.setDescription(value);
                                channel0.sendMessage(embed);
                                event.getChannel().sendMessage("Votre message à été envoyé");
                            } else {
                                embed.setTitle("@exec speak - Help");
                                embed.setColor(Color.RED);
                                embed.addField("Syntax", "@exec speak <serveur> <salon> <type de message> <message>");
                                embed.addField("<serveur>", "L'id du serveur");
                                embed.addField("<salon>", "Le nom du salon");
                                embed.addField("<type de message>", "message ou embed\n Message permet de faire un message standard.\nEmbed permet de faire un message comme celui ci");
                                embed.addField("<message>", "Le contenu de votre message");
                                msgSrv.getMemberById(author.getId()).get().sendMessage(embed);
                            }
                        }
                    } else if (containsIgnoreCase(message,"editMsg")) {

                        if (message.length()==13) {
                            embed.setTitle("@exec editMsg - Help");
                            embed.setColor(Color.RED);
                            embed.addField("Syntax","@exec editMsg <serveur> <salon> <id> <type de message> <message>");
                            embed.addField("<serveur>","L'id du serveur");
                            embed.addField("<salon>","Le nom du salon");
                            embed.addField("<type de message>","message ou embed\n Message permet de faire un message standard.\nEmbed permet de faire un message comme celui ci");
                            embed.addField("<message>","Le contenu de votre message");
                            msgSrv.getMemberById(author.getId()).get().sendMessage(embed);
                        } else {
                            String[] arg = message.split(" ");
                            StringBuilder sb = new StringBuilder();
                            for (int i = 6; i < arg.length; i++) {
                                sb.append(arg[i]).append(" ");
                            }
                            String value = sb.toString().trim();

                            Optional<Server> serverOptional = api.getServerById(arg[2]);
                            Server serveur0 = serverOptional.get();
                            TextChannel channel0 = (TextChannel) serveur0.getChannelsByName(arg[3]).get(0);
                            if(arg[5].equalsIgnoreCase("message")) {
                                api.getMessageById(arg[4],channel0).join().edit(value);
                            } else {
                                embed.setDescription(value);
                                embed.setColor(Color.CYAN);
                                embed.setAuthor("Message modifié le "+DiscordAnex.day()+"/"+DiscordAnex.month()+"/"+DiscordAnex.year());
                                api.getMessageById(arg[4],channel0).join().edit(embed);
                            }
                            event.getChannel().sendMessage("Le message à été modifié !");

                        }
                    } else if (containsIgnoreCase(message,"say")) {
                        String arg = message;
                        arg = arg.substring(10);
                        event.getChannel().sendMessage(arg);

                    }  else if(containsIgnoreCase(message,"getServerInfo")) {
                        embed.setTitle("Information de ce serveur");
                        embed.setColor(Color.GREEN);
                        embed.addField("Nom du serveur",msgSrv.getName());
                        embed.addField("Nombre de membres",msgSrv.getMembers().size()+"");
                        embed.addField("Nombre de channel",msgSrv.getChannels().size()+"");
                        String locName = msgSrv.getRegion().getName();
                        if (locName.equalsIgnoreCase("EU Central")) locName = "Europe Centrale";
                        else if (locName.equalsIgnoreCase("EU West")) locName = "Europe occidentale";
                        else if (locName.equalsIgnoreCase("US East")) locName = "L'Est des Etats-Unis";
                        else if (locName.equalsIgnoreCase("US West")) locName = "L'Ouest des Etats-Unis";
                        else if (locName.equalsIgnoreCase("US Central")) locName = "Le centre des Etats-Unis";
                        else if (locName.equalsIgnoreCase("US South")) locName = "Sud des Etats-Unis";
                        else if (locName.equalsIgnoreCase("Russia")) locName = "Russie";
                        else if (locName.equalsIgnoreCase("Singapore")) locName = "Singapour";
                        else if (locName.equalsIgnoreCase("Brazil")) locName = "Brésil";

                        embed.addField("Localisation du serveur",locName);
                        event.getChannel().sendMessage(embed);
                    } else if(containsIgnoreCase(message,"getSystemInfo")) {
                        float CPUtemp = 0, GPUtemp = 0;
                        Runtime rt = Runtime.getRuntime();
                        String uptime = "x jours",curTime = "00:00:00", output = "temp=0.0'C";
                        //
                        try {
                            Process proc = rt.exec("/opt/vc/bin/vcgencmd measure_temp");
                            BufferedReader stdInput = new BufferedReader(new
                                    InputStreamReader(proc.getInputStream()));
                            while ((output = stdInput.readLine()) != null) {
                                output = output.substring(output.indexOf("=")+1,output.indexOf("'"));
                                GPUtemp = Float.valueOf(output);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            Process proc = rt.exec("cat /sys/class/thermal/thermal_zone0/temp");
                            BufferedReader stdInput = new BufferedReader(new
                                    InputStreamReader(proc.getInputStream()));
                            while ((output = stdInput.readLine()) != null) {
                                CPUtemp = Float.valueOf(output);
                                CPUtemp/=1000;
                                BigDecimal a = new BigDecimal(CPUtemp);
                                a = a.setScale(2,BigDecimal.ROUND_DOWN);
                                CPUtemp = a.floatValue();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        float oneM =0 ,fiveM =0,fifteenM = 0;
                        try {
                            Process proc = rt.exec("uptime");
                            BufferedReader stdInput = new BufferedReader(new
                                    InputStreamReader(proc.getInputStream()));
                            String[] average = {};
                            while ((output = stdInput.readLine()) != null) {
                                output = output.substring(output.indexOf("average")+8);
                                average = output.split(",");
                                oneM = Float.valueOf(average[0]);
                                fiveM = Float.valueOf(average[1]);
                                fifteenM = Float.valueOf(average[2]);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            Process proc = rt.exec("uptime");
                            BufferedReader stdInput = new BufferedReader(new
                                    InputStreamReader(proc.getInputStream()));
                            String[] uptimeInfo = {};
                            while ((output = stdInput.readLine()) != null) {
                                uptime = output;
                                uptime = uptime.substring(0,uptime.indexOf(","));
                                uptimeInfo = uptime.split(" ");
                                uptime = "Allumé depuis "+uptimeInfo[3]+" jours";
                                curTime = uptimeInfo[1];
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // free ram
                        String Ram = "0Mb WTF";
                        try {
                            Process proc = rt.exec("free -ht");
                            BufferedReader stdInput = new BufferedReader(new
                                    InputStreamReader(proc.getInputStream()));
                            String[] freeMem = {};
                            while ((output = stdInput.readLine()) != null) {
                                if (containsIgnoreCase(output,"Total:")) {
                                    freeMem = output.split(" ");
                                    for (int i = 0; i<freeMem.length; i++) {
                                        if (containsIgnoreCase(freeMem[i],"M")) {
                                            freeMem[i] = freeMem[i].substring(0,freeMem[i].indexOf("M"));
                                        }
                                    }
                                    Float calc = (Float.valueOf(freeMem[15])/Float.valueOf(freeMem[8]))*100;
                                    BigDecimal a = new BigDecimal(calc);
                                    a = a.setScale(2,BigDecimal.ROUND_DOWN);
                                    calc = a.floatValue();
                                    Ram = freeMem[15]+"Mb/"+freeMem[8]+"Mb ("+calc+"%)";
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // free hdd
                        String HDD = "0Mb WTF";
                        try {
                            Process proc = rt.exec("df -h");
                            BufferedReader stdInput = new BufferedReader(new
                                    InputStreamReader(proc.getInputStream()));
                            String[] freeDisk = {};
                            while ((output = stdInput.readLine()) != null) {
                                if (containsIgnoreCase(output,"/dev/root")) {
                                    freeDisk = output.split(" ");
                                    for (int i = 0; i<freeDisk.length; i++) {
                                        if (containsIgnoreCase(freeDisk[i],"G")) {
                                            freeDisk[i] = freeDisk[i].substring(0,freeDisk[i].indexOf("G"));
                                        }
                                    }
                                    Float calc = (Float.valueOf(freeDisk[10])/Float.valueOf(freeDisk[8]))*100;
                                    BigDecimal a = new BigDecimal(calc);
                                    a = a.setScale(2,BigDecimal.ROUND_DOWN);
                                    calc = a.floatValue();
                                    HDD = freeDisk[10]+"Gb/"+freeDisk[8]+"Gb ("+calc+"%)";
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        embed.setTitle("Information système")
                                .addField("Températures:","CPU: "+CPUtemp+"°C\n" +
                                        "GPU: "+GPUtemp+"°C");
                        String indice = "Indice inconnu, il doit avoir un problème ! Vérifier que tout les cas de figure sont gérés, vérifier aussi que les capteurs de température soit OK. Vérifier aussi que vous ne faites pas tourner le bot sur Windows !";
                        if (CPUtemp<31) {
                            embed.setColor(Color.BLUE);
                            indice = "Froid";
                        } else if (CPUtemp>=31&&CPUtemp<51) {
                            embed.setColor(Color.GREEN);
                            indice = "Normal";
                        } else if (CPUtemp>=51&&CPUtemp<61) {
                            embed.setColor(Color.ORANGE);
                            indice = "Un peu chaud";
                        } else if (CPUtemp>=61) {
                            embed.setColor(Color.RED);
                            indice = "Trop chaud ! Il est recommandé de laisser refroidir le RPi";
                        }
                        embed.addField("Indice de température: ",indice)
                                .addField("Load average: ", "1m: "+oneM+"\n" +
                                        "5m: "+fiveM+"\n" +
                                        "15m: "+fifteenM)
                                .addField("Ram: ",Ram)
                                .addField("HDD",HDD)
                                .addField("Uptime: ",uptime)
                                .addField("Heure interne: ",curTime);
                        event.getChannel().sendMessage(embed);
                    } else if (containsIgnoreCase(message,"eval")) {
                        String code = message.substring(message.indexOf("`")+3);
                        code = code.substring(0,code.indexOf("`"));
                        event.getChannel().sendMessage(Evaluate.eval(code));
                    } else {
                        embed.setTitle("@exec - Aides");
                        embed.setColor(Color.GREEN);
                        embed.addField("Syntax","@exec <option> [Argument d'option]");
                        embed.addField("<option>","editMsg\n" +
                                "getServerInfo\n" +
                                "getSystemInfo\n" +
                                "say\n" +
                                "speak");
                        embed.addField("[Argument d'option]","Peut être vide dans certains cas et majoritairement remplis");
                        event.getChannel().sendMessage(embed);
                    }

                }



                if (containsIgnoreCase(message,"@mute")&&isAdmin) {
                    stats.sendStats();
                    String arg[] = message.split(" ");
                    int idOfStatement = 1 + (int)(Math.random() * 99999.9D);
                    String reason = "Vous avez été bannis ! \n MuteID#"+idOfStatement;
                    List<User> users = event.getMessage().getMentionedUsers();
                    Role mute = msgSrv.getRolesByName("RobMute").get(0);
                    if (message.length()==5) {
                        embed.setTitle("Mute");

                        embed.addField("Syntax:","@mute <qui> [raison]");
                        embed.setColor(Color.red);
                    } else {

                        StringBuilder sb = new StringBuilder();
                        for (int i = 2; i<arg.length; i++) {
                            sb.append(arg[i]+" ");
                        }
                        if (sb.length()!=0) {
                            reason = sb.toString()+" MuteID#"+idOfStatement;
                        }
                        ServerUpdater serverUpdater = msgSrv.createUpdater();
                        EmbedBuilder embedBuilder = new EmbedBuilder();
                        embedBuilder.setTitle("Mute");
                        if (containsIgnoreCase(mute.getUsers().toString(), users.get(0).getName())) {
                            embed.setColor(Color.GREEN);
                            embed.setDescription("Vous avez démute " + users.get(0).getName());
                            serverUpdater.removeRoleFromUser(users.get(0), mute);
                            embedBuilder.setDescription("Vous avez été démute");
                            embedBuilder.addField("Par:", author.getName());
                            embedBuilder.setColor(Color.GREEN);
                        } else {
                            embed.setColor(Color.GREEN);
                            embed.setTitle("Vous avez mute " + users.get(0).getName())
                                    .addField("Raison",reason);
                            serverUpdater.addRoleToUser(users.get(0), mute);
                            embedBuilder.setDescription("Vous avez été mute");
                            embedBuilder.setColor(Color.RED);
                            embedBuilder.addField("Par:", author.getName());
                            embedBuilder.addField("Raison", reason);
                        }
                        serverUpdater.update().join();
                        users.get(0).sendMessage(embedBuilder);

                        msgSrv.getMemberById(author.getId()).get().sendMessage(embed);
                    }
                }


                if (containsIgnoreCase(message,"@ban")&&isAdmin) {
                    stats.sendStats();
                    String arg[] = message.split(" ");
                    int idOfStatement = 1 + (int)(Math.random() * 99999.9D);
                    String reason = "Vous avez été bannis ! \n BanID#"+idOfStatement;
                    List<User> users = event.getMessage().getMentionedUsers();

                    Role ban = msgSrv.getRolesByName("RobBan").get(0);
                    if (message.length()==4) {
                        embed.setTitle("Ban");
                        embed.addField("Syntax:","@ban <temp/perm> <qui> <raison>");
                        embed.setColor(Color.red);
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 3; i<arg.length; i++) {
                            sb.append(arg[i]+" ");
                        }
                        if (sb.length()!=0) {
                            reason = sb.toString()+" BanID#"+idOfStatement;
                        }
                        embed.setTitle("Ban de " + users.get(0).getName());
                        if (containsIgnoreCase(arg[1], "perm")) {
                            ServerUpdater serverUpdater = msgSrv.createUpdater();
                            EmbedBuilder embedBuilder = new EmbedBuilder();
                            embedBuilder.setTitle("Ban");
                            embed.setColor(Color.RED);
                            embed.setDescription("Vous avez bannis " + users.get(0).getName());
                            embed.addField("Durée:", "Permanant");
                            embed.addField("Raison:", reason);
                            embedBuilder.setDescription("Vous avez été bannis");
                            embedBuilder.addField("Par:", author.getName());
                            embedBuilder.addField("Pour:", reason);
                            embedBuilder.setColor(Color.GREEN);
                            users.get(0).sendMessage(embedBuilder);
                            msgSrv.banUser(users.get(0));
                        }
                        if (containsIgnoreCase(arg[1], "temp")) {
                            ServerUpdater serverUpdater = msgSrv.createUpdater();
                            EmbedBuilder embedBuilder = new EmbedBuilder();
                            embedBuilder.setTitle("Ban");
                            embed.setColor(Color.RED);
                            embed.setDescription("Vous avez bannis " + users.get(0).getName());
                            embed.addField("Durée:", "Temporaire");
                            embed.addField("Raison:", reason);
                            embedBuilder.setDescription("Vous avez été bannis");
                            embedBuilder.addField("Par:", author.getName());
                            embedBuilder.addField("Pour:", reason);
                            embedBuilder.setColor(Color.GREEN);
                            serverUpdater.addRoleToUser(users.get(0), ban);
                            serverUpdater.update().join();
                            users.get(0).sendMessage(embedBuilder);
                        }
                        msgSrv.getMemberById(author.getId()).get().sendMessage(embed);
                    }
                }


                /**
                 *
                 * Uptime
                 *
                 *
                 **/

                if (message.equalsIgnoreCase("!uptime")) {
                    stats.sendStats();
                    embed.setTitle("Uptime");
                    embed.setColor(Color.YELLOW);
                    embed.addField("Démarrer à ",startyMdhms[3]+":"+startyMdhms[4]+":"+startyMdhms[5]);
                    embed.addField("Démarrer le:",startyMdhms[2]+"/"+startyMdhms[1]+"/"+startyMdhms[0]);

                    long time = (startyMdhms[1]*30*24*60*60)+(startyMdhms[2]*24*60*60)+(startyMdhms[3]*60*60)+(startyMdhms[4]*60)+startyMdhms[5];
                    long curTime = (DiscordAnex.month()*30*24*60*60)+(DiscordAnex.day()*24*60*60)+(DiscordAnex.hours()*60*60)+(DiscordAnex.minutes()*60)+DiscordAnex.seconds();

                    long timeElaps = curTime - time;
                    embed.addField("Start Time:",time+"").addField("Current Time:",curTime+"").addField("Time elapsed", timeElaps+"");
                    long month=0,day=0,hour=0,minute=0,second=0;
                    month = timeElaps/60/60/24/30;
                    day = (timeElaps/60/60/24)-(month*30);
                    hour = (timeElaps/60/60)-(month*30*24)-(day*24);
                    minute = (timeElaps/60)-(month*30*24*60)-(day*24*60)-(hour*60*60);
                    second = (timeElaps/60)-(month*30*24*60*60)-(day*24*60*60)-(hour*60*60)-(minute*60);
                    embed.addField("Uptime",month+" mois "+day+" jours "+hour+":"+minute+":"+second);
                    event.getChannel().sendMessage(embed);
                }
                /**
                 *
                 * Blague
                 *
                 */
                if (message.equalsIgnoreCase("!blague")||message.equalsIgnoreCase("!blagues")) {
                    stats.sendStats();
                    String[] blague = DiscordAnex.blague();
                    embed.setTitle("Blague").setDescription("Chouette une nouvelle blague").addField(blague[0],blague[1]);
                    event.getChannel().sendMessage(embed);
                }

                if (containsIgnoreCase(message,"@help")&&isAdmin) {
                    stats.sendStats();
                    embed.setColor(Color.GREEN);
                    if (message.length()==5) {

                        embed.setTitle("Aides admin");
                        embed.addField("@mute <mention> [raison]","Permet de mute une personne");
                        embed.addField("@ban <temp/perm> <mention> [raison]","Permet de bannir quelqu'un");
                        embed.addField("@ping","Permet de ping le bot");
                        embed.addField("@game","Permet de changer le jeu en cours");
                    } else if (containsIgnoreCase(message,"mute")) {
                        embed.setTitle("Aides admin - mute");
                        embed.addField("Syntax:","@mute <mention> [raison]");
                        embed.addField("mention","Mentionne qui mute avec la mention @exemple#0000");
                    } else if (containsIgnoreCase(message,"ban")) {
                        embed.setTitle("Aides admin - ban");
                        embed.addField("Syntax:","@ban <perm/temp> <mention> [raison]");
                        embed.addField("perm","Permet de kick et bannir un utilisateur");
                        embed.addField("temp","Permet de bannir temporairement un utilisateur");
                        embed.addField("mention","Mentionne qui ban avec la mention @exemple#0000");
                    } else if (containsIgnoreCase(message,"ping")) {
                        embed.setTitle("Aides admin - ping");
                        embed.addField("Syntax:","@ping");
                    } else if (containsIgnoreCase(message,"game")) {
                        embed.setTitle("Aides admin - game");
                        embed.addField("Syntax:","@game <jeu> --mode=<mode>");
                        embed.addField("<jeu>","Jeu à mettre");
                        embed.addField("<mode>","0 = jouer\n1 = stream\n2 = Ecouter\n3 = regarde");
                    }

                    event.getChannel().sendMessage(embed);
                }
                /**
                 * If isVote
                 */
                User msgAuthor = event.getMessage().getAuthor().asUser().get();
                if (inCreateVote.containsKey(msgAuthor)) {
                    stats.sendStats();
                    event.getMessage().getMessagesBefore(1).join().deleteAll();
                    if (inCreateVote.get(msgAuthor)==1) {
                        voteName.put(msgAuthor,message);
                        embed.setTitle("Creation d'un vote").setColor(Color.YELLOW).setDescription("Quel est contenu du vote ?");
                        event.getChannel().sendMessage(embed);
                        inCreateVote.replace(msgAuthor, 2);
                        event.getMessage().delete();
                    } else
                    if (inCreateVote.get(msgAuthor)==2) {
                        voteContent.put(msgAuthor,message);
                        embed.setTitle("Creation d'un vote").setColor(Color.YELLOW).setDescription("Quel est type du vote ?").addField("Types","oui/non\noption");
                        event.getChannel().sendMessage(embed);
                        inCreateVote.replace(event.getMessage().getAuthor().asUser().get(), 3);
                        event.getMessage().delete();
                    } else
                    if (inCreateVote.get(msgAuthor)==3) {
                        if (message.equalsIgnoreCase("oui/non")) {
                            embed.setTitle("Vote: "+voteName.get(msgAuthor)).setColor(Color.GREEN).setDescription(voteContent.get(msgAuthor));
                            inCreateVote.remove(msgAuthor);
                            event.getChannel().sendMessage(embed).join().addReactions("👍","👎");

                        } else {
                            embed.setTitle("Creation d'un vote").setColor(Color.YELLOW).setDescription("Option 1 ?");
                            inCreateVote.replace(event.getMessage().getAuthor().asUser().get(), 4);
                            event.getChannel().sendMessage(embed);
                        }
                        event.getMessage().delete();
                    } else
                    if (inCreateVote.get(msgAuthor)==4) {
                        voteOpt1.put(msgAuthor,message);
                        embed.setTitle("Creation d'un vote").setColor(Color.YELLOW).setDescription("Option 2 ?");
                        event.getChannel().sendMessage(embed);
                        event.getMessage().delete();
                        inCreateVote.replace(event.getMessage().getAuthor().asUser().get(), 5);
                    } else
                    if (inCreateVote.get(msgAuthor)==5) {
                        voteOpt2.put(msgAuthor,message);
                        embed.setTitle("Creation d'un vote").setColor(Color.YELLOW).setDescription("Option 3 ?");
                        event.getChannel().sendMessage(embed);
                        event.getMessage().delete();
                        inCreateVote.replace(event.getMessage().getAuthor().asUser().get(), 6);
                    } else
                    if (inCreateVote.get(msgAuthor)==6) {
                        voteOpt3.put(msgAuthor,message);
                        embed.setTitle("Vote: "+voteName.get(msgAuthor)).setColor(Color.GREEN).setDescription(voteContent.get(msgAuthor)).addField("Option A",voteOpt1.get(msgAuthor)).addField("Option B",voteOpt2.get(msgAuthor)).addField("Option C",voteOpt3.get(msgAuthor));
                        inCreateVote.remove(msgAuthor);
                        event.getChannel().sendMessage(embed);
                        event.getMessage().delete();
                    }
                } else {
                    if (!isPrivate&&message.equalsIgnoreCase("!createvote")) {
                        stats.sendStats();
                        inCreateVote.put(msgAuthor,1);
                        embed.setTitle("Creation d'un vote").setColor(Color.YELLOW).setDescription("Quel est le nom du vote ?");
                        event.getChannel().sendMessage(embed);
                        event.getMessage().delete();
                    }
                }



                /**
                 * End vote
                 */

                if (message.equalsIgnoreCase("@@restart")&&author.getName().equalsIgnoreCase("ungarscool1")) {
                    stats.sendStats();
                    stats.setBotStatus(Status.PARTIAL_OUTAGE);
                    Runtime runtime = Runtime.getRuntime();
                    try {
                        Process process = runtime.exec("java -jar discordbotv2.jar");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    embed.setColor(Color.BLUE);
                    embed.setTitle("SuperAdmin - Restart");
                    embed.setDescription("Le bot va redémarrer");
                    event.getChannel().sendMessage(embed);
                    System.exit(0);
                }
                if (message.equalsIgnoreCase("@@shutdown")&&author.getName().equalsIgnoreCase("ungarscool1")) {
                    stats.sendStats();
                    stats.setBotStatus(Status.MAJOR_OUTAGE);
                    System.exit(0);
                }

                if(containsIgnoreCase(message,"!checkperm")) {
                    stats.sendStats();
                    embed.setTitle("Verification de permission");

                    embed.addField("Admin",isAdmin+"");
                    embed.addField("My role","La lecture des rôles n'est pas activée !\nSuite à la désactivation à cause de la RGPD");
                    event.getChannel().sendMessage(embed);
                }



            });


        }).exceptionally(ExceptionLogger.get());
    }
}