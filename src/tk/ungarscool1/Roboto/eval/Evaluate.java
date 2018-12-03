package tk.ungarscool1.Roboto.eval;

import bsh.EvalError;
import bsh.Interpreter;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Evaluate {

    public static EmbedBuilder eval(String command) {
        boolean isSuccess = true;
        Interpreter i = new Interpreter();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        System.setOut(ps);
        EmbedBuilder embed = new EmbedBuilder().setFooter("Eval with Java").setTitle("Evaluation").addField("Commande","```java\n"+command+"```");
        try {
            i.eval(command);
        } catch (EvalError evalError) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            evalError.printStackTrace(pw);

            embed.setColor(Color.RED).addField("Erreur !",sw.toString());
            isSuccess = false;
        }
        if (isSuccess) {
            embed.addField("Réponse",baos.toString()).setColor(Color.GREEN);
        }
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        return embed;
    }
}