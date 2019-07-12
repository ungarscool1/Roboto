package com.github.ungarscool1.Roboto.entity.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.listener.message.reaction.ReactionAddListener;
import org.javacord.api.util.event.ListenerManager;

import com.github.ungarscool1.Roboto.Main;
import com.github.ungarscool1.Roboto.listeners.ReacListener;
import com.github.ungarscool1.Roboto.listeners.commands.GameCommand;

public class Puissance4 {

	private Message joinMessage;
	private Message lastMessage;
	private ArrayList<User> players = new ArrayList<>();
	private int player;
	private boolean inGame = false;
	private int maxCoups = 36;
	private int coups;
	private ListenerManager<ReactionAddListener> listener;
	private Thread th;
	private int[][] grid = {
			{0,0,0,0,0,0},
			{0,0,0,0,0,0},
			{0,0,0,0,0,0},
			{0,0,0,0,0,0},
			{0,0,0,0,0,0},
			{0,0,0,0,0,0}
	};
	
	private BufferedImage p1Img;
	private BufferedImage p2Img;
	private BufferedImage gridImg = new BufferedImage(700, 600, BufferedImage.TYPE_INT_ARGB);
	
	public Puissance4(User Owner) {
		player = 1 + (int)(Math.random() * 2);
		System.out.println("Joueur n°"+player+" jouera en 1er");
		join(Owner);
		this.inGame = false;
		try {
			p1Img = ImageIO.read(getClass().getClassLoader().getResource("Puissance4/p1.png"));
			p2Img = ImageIO.read(getClass().getClassLoader().getResource("Puissance4/p2.png"));
			gridImg = ImageIO.read(getClass().getClassLoader().getResource("Puissance4/grid.png"));
			gridImg = resize(gridImg, 700, 600);
			p1Img = resize(p1Img, 60, 60);
			p2Img = resize(p2Img, 60, 60);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public EmbedBuilder joinMessage() {
		String playersToString = "";
		for (int i = 0; i < players.size(); i++) {
			playersToString += "- " + players.get(i).getMentionTag() + "\n";
		}
		Color color = Color.RED;
		String desc = players.get(0).getName() + " vous a invité à jouer à Puissance 4.";
		if (inGame) {
			color = Color.GREEN;
			desc = "La partie de " + players.get(0).getName() + " a commencée !";
		}
		return new EmbedBuilder().setTitle("Puissance 4").setDescription(desc).addField("Slots", players.size() + " / " + 2, true).addField("Joueurs dans la partie", playersToString).setImage(gridImg).setColor(color);
	}
	
	public void setJoinMessage(Message message) {
		this.joinMessage = message;
	}
	
	public String join (User player) {
		if (!players.contains(player)) {
			if (players.size() <= 2) {
				players.add(player);
				if (players.size() == 2) {
					inGame = true;
					return "Starting";
				}
				return "Joined";
			} else {
				return "This party has already started";
			}
		} else {
			return "Already in party";
		}
	}
	
	public String leave(User player) {
		players.remove(player);
		if (players.size()==0) {
			return "party disbended";
		}
		return "disconnected";
	}
	
	private void finish() {
		joinMessage.getChannel().sendMessage(new EmbedBuilder().setTitle("Puissance 4").setDescription(players.get(player - 1).getDisplayName(joinMessage.getServer().get()) + " a gagné la partie").setImage(gridImg).setFooter("Partie de " + players.get(0).getDisplayName(joinMessage.getServer().get()) + " contre " + players.get(1).getDisplayName(joinMessage.getServer().get())));
		joinMessage.delete("^4 party is finished");
		GameCommand.P4.remove(joinMessage);
		ReacListener.updateGames();
		listener.remove();
		th.stop();
	}
	
	private void message() {
		coups++;
		if (coups <= maxCoups) {
			EmbedBuilder embed = new EmbedBuilder();
			embed.setTitle("Puissance 4: C'est le tour de " + players.get(player - 1).getDisplayName(joinMessage.getServer().get()));
			embed.setDescription("Coups " + coups + "/" + maxCoups);
			embed.setImage(gridImg);
			embed.setFooter("Partie de " + players.get(0).getDisplayName(joinMessage.getServer().get()) + " contre " + players.get(1).getDisplayName(joinMessage.getServer().get()));
			lastMessage = joinMessage.getChannel().sendMessage(embed).join();
			lastMessage.addReactions("1⃣", "2⃣", "3⃣", "4⃣", "5⃣", "6⃣");
		} else 
			finish();
	}
	
	private boolean checkP4() {
				//check for 4 across
				for(int row = 0; row<grid.length; row++){
					for (int col = 0;col < grid[0].length - 3;col++){
						if (grid[row][col] == player   && 
							grid[row][col+1] == player &&
							grid[row][col+2] == player &&
							grid[row][col+3] == player){
							return true;
						}
					}			
				}
				//check for 4 up and down
				for(int row = 0; row < grid.length - 3; row++){
					for(int col = 0; col < grid[0].length; col++){
						if (grid[row][col] == player   && 
							grid[row+1][col] == player &&
							grid[row+2][col] == player &&
							grid[row+3][col] == player){
							return true;
						}
					}
				}
				//check upward diagonal
				for(int row = 3; row < grid.length; row++){
					for(int col = 0; col < grid[0].length - 3; col++){
						if (grid[row][col] == player   && 
							grid[row-1][col+1] == player &&
							grid[row-2][col+2] == player &&
							grid[row-3][col+3] == player){
							return true;
						}
					}
				}
				//check downward diagonal
				for(int row = 0; row < grid.length - 3; row++){
					for(int col = 0; col < grid[0].length - 3; col++){
						if (grid[row][col] == player   && 
							grid[row+1][col+1] == player &&
							grid[row+2][col+2] == player &&
							grid[row+3][col+3] == player){
							return true;
						}
					}
				}
		if (player == 1) 
			player = 2;
		else
			player = 1;
		message();
		return false;
	}
	
	public void gameHandler() {
		th = new Thread(new Runnable() {
		     public void run() {
		    	 listener = Main.API.addReactionAddListener(event -> {
		    		 if (event.getUser().isYourself()) return;
		    		 Emoji emoji = event.getEmoji();
		    		 Message message = event.getMessage().get();
		    		 if (event.getUser().equals(players.get(player - 1))) {
		    			 if (emoji.asUnicodeEmoji().isPresent()) {
		    				 String finalEmoji = emoji.asUnicodeEmoji().get();
		    				 if (finalEmoji.equals("1⃣")) {
		    					 if (grid[5][0] == 0) {
		    						 grid[5][0] = player;
		    						 draw(120, 530);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[4][0] == 0) {
		    						 grid[4][0] = player;
		    						 draw(120, 460);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[3][0] == 0) {
		    						 grid[3][0] = player;
		    						 draw(120, 390);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[2][0] == 0) {
		    						 grid[2][0] = player;
		    						 draw(120, 320);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[1][0] == 0) {
		    						 grid[1][0] = player;
		    						 draw(120, 250);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[0][0] == 0) {
		    						 grid[0][0] = player;
		    						 draw(120, 180);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 }
		    				 } else if (finalEmoji.equals("2⃣")) {
		    					 if (grid[5][1] == 0) {
		    						 grid[5][1] = player;
		    						 draw(200, 530);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[4][1] == 0) {
		    						 grid[4][1] = player;
		    						 draw(200, 460);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[3][1] == 0) {
		    						 grid[3][1] = player;
		    						 draw(200, 390);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[2][1] == 0) {
		    						 grid[2][1] = player;
		    						 draw(200, 320);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[1][1] == 0) {
		    						 grid[1][1] = player;
		    						 draw(200, 250);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[0][1] == 0) {
		    						 grid[0][1] = player;
		    						 draw(200, 180);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 }
		    				 } else if (finalEmoji.equals("3⃣")) {
		    					 if (grid[5][2] == 0) {
		    						 grid[5][2] = player;
		    						 draw(280, 530);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[4][2] == 0) {
		    						 grid[4][2] = player;
		    						 draw(280, 460);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[3][2] == 0) {
		    						 grid[3][2] = player;
		    						 draw(280, 390);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[2][2] == 0) {
		    						 grid[2][2] = player;
		    						 draw(280, 320);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[1][2] == 0) {
		    						 grid[1][2] = player;
		    						 draw(280, 250);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[0][2] == 0) {
		    						 grid[0][2] = player;
		    						 draw(280, 180);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 }
		    				 } else if (finalEmoji.equals("4⃣")) {
		    					 if (grid[5][3] == 0) {
		    						 grid[5][3] = player;
		    						 draw(360, 530);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[4][3] == 0) {
		    						 grid[4][3] = player;
		    						 draw(360, 460);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[3][3] == 0) {
		    						 grid[3][3] = player;
		    						 draw(360, 390);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[2][3] == 0) {
		    						 grid[2][3] = player;
		    						 draw(360, 320);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[1][3] == 0) {
		    						 grid[1][3] = player;
		    						 draw(360, 250);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[0][3] == 0) {
		    						 grid[0][3] = player;
		    						 draw(360, 180);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 }
		    				 } else if (finalEmoji.equals("5⃣")) {
		    					 if (grid[5][4] == 0) {
		    						 grid[5][4] = player;
		    						 draw(440, 530);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[4][4] == 0) {
		    						 grid[4][4] = player;
		    						 draw(440, 460);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[3][4] == 0) {
		    						 grid[3][4] = player;
		    						 draw(440, 390);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[2][4] == 0) {
		    						 grid[2][4] = player;
		    						 draw(440, 320);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[1][4] == 0) {
		    						 grid[1][4] = player;
		    						 draw(440, 250);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[0][4] == 0) {
		    						 grid[0][4] = player;
		    						 draw(440, 180);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 }
		    				 } else if (finalEmoji.equals("6⃣")) {
		    					 if (grid[5][5] == 0) {
		    						 grid[5][5] = player;
		    						 draw(520, 530);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[4][5] == 0) {
		    						 grid[4][5] = player;
		    						 draw(520, 460);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[3][5] == 0) {
		    						 grid[3][5] = player;
		    						 draw(520, 390);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[2][5] == 0) {
		    						 grid[2][5] = player;
		    						 draw(520, 320);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[1][5] == 0) {
		    						 grid[1][5] = player;
		    						 draw(520, 250);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 } else if (grid[0][5] == 0) {
		    						 grid[0][5] = player;
		    						 draw(520, 180);
		    						 lastMessage.delete("Delete ^4 message from P4.gameHandler");
		    						 if (checkP4()) {
				    					 finish();
				    				 }
		    					 }
		    				 }
		    			 }
		    		 }
		    	 });
		    	 message();
		     }
		});
		th.start();
	}
	
	
	private void draw(int x, int y) {
		Graphics2D g2d = gridImg.createGraphics();
		if (player == 1) {
			g2d.drawImage(p1Img, x, y, null);
			g2d.dispose();
		} else {
			g2d.drawImage(p2Img, x, y, null);
			g2d.dispose();
		}
	}
	
	
	private BufferedImage resize(BufferedImage img, int newW, int newH) { 
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}  
}
