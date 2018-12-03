package tk.ungarscool1.Roboto;


import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Date;
public class DiscordAnex {

    protected static int ss=-99;
    protected static int jour=0;
    protected static int[] Lson = new int[] {8,8,9,9,10,10,10,11,11,12,12,13,13,13,13,14,15,15,16,16,17,17};
    protected static int[] Lmin= new int[]  {05,10,05,10,0,10,15,10,15,05,10,0,05,50,55,50,45,55,0,55,50,55};
    public String TM() {
        //if(message.getAuthor().getRoles(api.getServerById("371656952050352140")).toString().contains("Admin")) {
        long jdecal = ChronoUnit.DAYS.between(LocalDate.of(2018, Month.FEBRUARY, 13), LocalDate.now());
        ss=(int) (-99+(jdecal*(-2)));

        for (int i=0; i<Lson.length+1;i++){

            if (i==22) return "Tu n'es pas au lycée";
            if (hours()==Lson[i] && minutes()<Lmin[i]) {
                long h = ChronoUnit.HOURS.between(LocalTime.now(), LocalTime.of(Lson[i], Lmin[i], 59));
                long m = ChronoUnit.MINUTES.between(LocalTime.now(), LocalTime.of(hours(), Lmin[i], 59));
                return ":warning: TMS est en beta :warning:\n"+"SS="+ss+"\n" +"Il reste " + h + "h " + m + "m avant la prochaine sonnerie";
            }
        }
        return null;
    }



    public EmbedBuilder prochaineGdVacances() {
        EmbedBuilder embed = new EmbedBuilder().setFooter("Prochaine Grande Vacances Powered by Roboto").setColor(Color.GREEN);
        long jdecal;
        long h;
        long m;
        long s;
        jdecal = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.of(2019, Month.JULY, 4));
        h = ChronoUnit.HOURS.between(LocalTime.now(), LocalTime.of(18, 59, 59));
        m = ChronoUnit.MINUTES.between(LocalTime.now(), LocalTime.of(hours(), 59, 59));
        s = ChronoUnit.SECONDS.between(LocalTime.now(), LocalTime.of(hours(), minutes(), 59));




        if ((int)h<0) {
            jdecal=(int)jdecal-1;
            h=23+(int)h;
        }


        if ((int)m<0) {
            h=(int)h-1;
            m=59+(int)m;
        }
        if ((int)s<0) {
            m=(int)m-1;
            s=59+(int)s;
        }
        if (((int)jdecal<0)||((int)jdecal<1&&(int)h<1&&(int)m<1&&(int)s<1)) {
            embed.addField("Bonne nouvelle !", "C'EST LES VACANCES !");
            return embed;
        }
        embed.addField("Il reste",jdecal+" jours "+h+":"+m+":"+s);
        return embed;
    }

    public static String[] blague() {
        String response = null;
        int rdm = 1 + (int)(Math.random() * 20.9D);
        if (rdm==1) {

            response = "Pourquoi les chats ont-ils mauvais caractère ?\nParce qu'ils n'arrênet pas de grimper aux rideaux";
        } else if (rdm==2) {
            response = "Que dit un volcan à une montagne ? \n Ça ne te dérange pas si je fume ?";
        }  else if (rdm==3) {
            response = "Qu'est ce qui est vert et qui se déplace sous l'eau ?\nUn chou-marin";
        } else if (rdm==4) {
            response = "Toto, dis-moi le futur simple du verbe bâiller\nJe dormirai";
        }  else if (rdm==5) {
            response = "Quel est le sport préfére des chèvres ?\nL'aérobic";
        } else if (rdm==6) {
            response = "Comment s'appelle un chat tombé dans un pot de peinture le jour de Noêl ? \n Un chat peint de Noël";
        }  else if (rdm==7) {
            response = "Pourquoi les chiens enrhumés peuvent-ils miauler ?\n Parce qu'ils ont un chat dans la gorge";
        }  else if (rdm==8) {
            response = "Quel est le comble pour un cordonnier ?\n C'est d'avoir un coup de pompe";
        }  else if (rdm==9) {
            response = "Pourquoi la vie des ampoules est-ell si fragile ?\n Parce qu'elle ne tiens qu'à un fil";
        }  else if (rdm==10) {
            response = "Qu'est ce qui est jaune et qui court vite ?\n Un citron pressé";
        }  else if (rdm==11) {
            response = "Comment un aspirateur peut-il mourir ?\nEn mordant la poussière";
        }  else if (rdm==12) {
            response = "Docteur, docteur, mes cheveux tombent. Pouvez-vous me doner quelque chose pour les conserver ?\n Bien sûr, voici une boîte";
        }  else if (rdm==13) {
            response = "Comment appelle-t-on un prisonnier qui rate son évasion...\nC'est un repris de justesse";
        } else if (rdm==14) {
            response = "Docteur, je crois bien que j'ai besoin de lunettes !\nEn effet, parce que là vous êtes à la banque";
        } else if (rdm==15) {
            response = "Quelles est l'heure préferée des fakirs ? \nL'heure de pointe";
        } else if (rdm==16) {
            response = "Quels sont les sportifs les plus attentionnés ?\n Les joueurs de tennis, parce qu'ils sont toujours là pour servir";
        } else if (rdm==17) {
            response = "Qu'est-ce qu'un wagon-lit ?\nA vérifier, mais je crois que les wagons ne savent pas lire";
        } else if (rdm==18) {
            response = "Pourquoi les livres n'ont-ils pas froid en hiver ?\nParce qu'ils portent une couverture";
        } else if (rdm==19) {
            response = "Pourquoi les pêcheurs ne sont-ils pas gros ?\nParce q'uils surveillent leur ligne !";
        } else if (rdm==20) {
            response = "Qu'est-ce qu'un feu rouge ?\nUn feu vert qui a mûri";
        }
        String[] output = response.split("\n");
        return output;
    }

    public EmbedBuilder bac() {
        EmbedBuilder embed = new EmbedBuilder().setFooter("Le Bac avec Roboto");
        long jdecal=0;
        long h=0;
        long m=0;
        long s=0;

        if (year()==2018||(month()<=4&&year()==2019)) {
            embed.setColor(Color.GREEN);
        } else if (year()==2019&&month()==5) {
            embed.setColor(Color.YELLOW);
        } else if (year()==2019&&month()==5&&day()>=15) {
            embed.setColor(Color.ORANGE);
        } else {

        }
        if (year()==2018||(month()<6&&year()==2019)) {
            jdecal = ChronoUnit.DAYS.between(LocalDate.now(),LocalDate.of(2019, Month.JUNE, 18));
            h = ChronoUnit.HOURS.between(LocalTime.now(),LocalTime.of(8,0,0));
            m = ChronoUnit.MINUTES.between(LocalTime.now(),LocalTime.of(hours(),0,0));
            s = ChronoUnit.SECONDS.between(LocalTime.now(),LocalTime.of(hours(),minutes(),0));
            embed.addField("Epreuve de ","Philo");
        }
        if (month()==6&&day()==18) {
            jdecal = ChronoUnit.DAYS.between(LocalDate.now(),LocalDate.of(2019, Month.JUNE, 19));
            h = ChronoUnit.HOURS.between(LocalTime.now(),LocalTime.of(14,0,0));
            m = ChronoUnit.MINUTES.between(LocalTime.now(),LocalTime.of(hours(),0,0));
            s = ChronoUnit.SECONDS.between(LocalTime.now(),LocalTime.of(hours(),minutes(),0));
            embed.addField("Epreuve de ","Maths");
        }
        if (month()==6&&day()==19) {
            jdecal = ChronoUnit.DAYS.between(LocalDate.now(),LocalDate.of(2019, Month.JUNE, 20));
            h = ChronoUnit.HOURS.between(LocalTime.now(),LocalTime.of(14,0,0));
            m = ChronoUnit.MINUTES.between(LocalTime.now(),LocalTime.of(hours(),0,0));
            s = ChronoUnit.SECONDS.between(LocalTime.now(),LocalTime.of(hours(),minutes(),0));
            embed.addField("Epreuve de ","LV1 / Anglais");
        }
        if (month()==6&&day()==20) {
            jdecal = ChronoUnit.DAYS.between(LocalDate.now(),LocalDate.of(2019, Month.JUNE, 21));
            h = ChronoUnit.HOURS.between(LocalTime.now(),LocalTime.of(14,0,0));
            m = ChronoUnit.MINUTES.between(LocalTime.now(),LocalTime.of(hours(),0,0));
            s = ChronoUnit.SECONDS.between(LocalTime.now(),LocalTime.of(hours(),minutes(),0));
            embed.addField("Epreuve de ","Enseignements Technologique / SIN / EE / ITEC");
        }
        if (month()==6&&day()==21) {
            jdecal = ChronoUnit.DAYS.between(LocalDate.now(),LocalDate.of(2019, Month.JUNE, 22));
            h = ChronoUnit.HOURS.between(LocalTime.now(),LocalTime.of(14,0,0));
            m = ChronoUnit.MINUTES.between(LocalTime.now(),LocalTime.of(hours(),0,0));
            s = ChronoUnit.SECONDS.between(LocalTime.now(),LocalTime.of(hours(),minutes(),0));
            embed.addField("Epreuve de ","LV2 / Espagnol / Allemand");
        }
        if (month()==6&&day()==22) {
            jdecal = ChronoUnit.DAYS.between(LocalDate.now(),LocalDate.of(2019, Month.JUNE, 25));
            h = ChronoUnit.HOURS.between(LocalTime.now(),LocalTime.of(8,0,0));
            m = ChronoUnit.MINUTES.between(LocalTime.now(),LocalTime.of(hours(),0,0));
            s = ChronoUnit.SECONDS.between(LocalTime.now(),LocalTime.of(hours(),minutes(),0));
            embed.addField("Epreuve de ","Physique-Chimie");
        }
        if (month()>6&&year()==2019) {
            embed.addField("Le bac est déjà passer","Et oui si tu ne savais pas, bah tu es un peu dans la merde !");
        }


        if ((int)h<0) {
            jdecal=(int)jdecal-1;
            h=23+(int)h;
        }


        if ((int)m<0) {
            h=(int)h-1;
            m=59+(int)m;
        }
        if ((int)s<0) {
            m=(int)m-1;
            s=59+(int)s;
        }
        embed.addField("Il reste",jdecal+" jours, "+h+":"+m+":"+s);
        return embed;
    }
    public EmbedBuilder prochaineVacances(String WhoSendMessage) {
        EmbedBuilder embed = new EmbedBuilder().setTitle("Prochaine vacances").setColor(Color.GREEN).setFooter("Prochaine vacances Powered by Roboto");
        long jdecal=0;
        long h=0;
        long m=0;
        long s=0;
        if(month()>=9&&month()<=10) {
            if (month()==10&&day()>=20) {
                jdecal=0;
            } else {
                jdecal = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.of(year(), Month.OCTOBER, 19));
                h = ChronoUnit.HOURS.between(LocalTime.now(), LocalTime.of(17, 59, 1));
                m = ChronoUnit.MINUTES.between(LocalTime.now(), LocalTime.of(hours(), 59, 1));
                s = ChronoUnit.SECONDS.between(LocalTime.now(), LocalTime.of(hours(), minutes(), 1));
                embed.addField("Vacances le", "Vendredi 19 octobre à 18h");
            }
        }
        if(month()>=11&&month()<=12) {
            if (month()==12&&day()>=21) {
                jdecal=0;
            } else {
                jdecal = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.of(year(), Month.DECEMBER, 21));
                h = ChronoUnit.HOURS.between(LocalTime.now(), LocalTime.of(17, 59, 1));
                m = ChronoUnit.MINUTES.between(LocalTime.now(), LocalTime.of(hours(), 59, 1));
                s = ChronoUnit.SECONDS.between(LocalTime.now(), LocalTime.of(hours(), minutes(), 1));
                embed.addField("Vacances le", "Vendredi 22 décembre à 18h");
            }
        }
        if(month()>=1&&month()<=2) {
            if (month()==2&&day()>=9) {
                jdecal=0;
            } else {
                jdecal = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.of(year(), Month.FEBRUARY, 8));
                h = ChronoUnit.HOURS.between(LocalTime.now(), LocalTime.of(17, 59, 1));
                m = ChronoUnit.MINUTES.between(LocalTime.now(), LocalTime.of(hours(), 59, 1));
                s = ChronoUnit.SECONDS.between(LocalTime.now(), LocalTime.of(hours(), minutes(), 1));
                embed.addField("Vacances le", "Vendredi 8 février à 18h");
            }
        }
        if(month()>=2&&month()<=4) {
            if (month()==4&&day()>=6) {

            } else {
                jdecal = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.of(year(), Month.APRIL, 5));
                h = ChronoUnit.HOURS.between(LocalTime.now(), LocalTime.of(17, 59, 1));
                m = ChronoUnit.MINUTES.between(LocalTime.now(), LocalTime.of(hours(), 59, 1));
                s = ChronoUnit.SECONDS.between(LocalTime.now(), LocalTime.of(hours(), minutes(), 1));
                embed.addField("Vacances le", "Vendredi 5 avril à 18h");
            }
        }
        if(month()>=4&&month()<=7) {
            if (month()==7) {

            } else {
                jdecal = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.of(year(), Month.JULY, 5));
                h = ChronoUnit.HOURS.between(LocalTime.now(), LocalTime.of(17, 59, 1));
                m = ChronoUnit.MINUTES.between(LocalTime.now(), LocalTime.of(hours(), 59, 1));
                s = ChronoUnit.SECONDS.between(LocalTime.now(), LocalTime.of(hours(), minutes(), 1));
                embed.addField("Vacances le", "Vendredi 5 juillet à 18h");
            }
        }


        if ((int)jdecal<=0) {
            embed.setDescription("Tu es deja en vacances !");
            return embed;
        }

        if ((int)h<0) {
            jdecal=(int)jdecal-1;
            h=23+(int)h;
        }


        if ((int)m<0) {
            h=(int)h-1;
            m=59+(int)m;
        }
        if ((int)s<0) {
            m=(int)m-1;
            s=59+(int)s;
        }
        System.out.println("Il reste "+jdecal+" jours, "+h+":"+m+":"+s);
        embed.addField("Il reste",jdecal+" jours, "+h+":"+m+":"+s);
        return embed;
    }










    public int hours() {
        DateFormat dateFormat = new SimpleDateFormat("HH");
        Date date = new Date();

        return Integer.parseInt(dateFormat.format(date));
    }
    public int minutes() {
        DateFormat dateFormat = new SimpleDateFormat("mm");
        Date date = new Date();

        return Integer.parseInt(dateFormat.format(date));
    }
    public int seconds() {
        DateFormat dateFormat = new SimpleDateFormat("ss");
        Date date = new Date();

        return Integer.parseInt(dateFormat.format(date));
    }

    public int day() {
        DateFormat dateFormat = new SimpleDateFormat("dd");
        Date date = new Date();

        return Integer.parseInt(dateFormat.format(date));
    }
    public int month() {
        DateFormat dateFormat = new SimpleDateFormat("MM");
        Date date = new Date();

        return Integer.parseInt(dateFormat.format(date));
    }
    public int year() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy");
        Date date = new Date();

        return Integer.parseInt(dateFormat.format(date));
    }

}