# Roboto

## Supported Languges
 - French
 - English
 - Spanish

## Commands
 - games
 - ui
 - si
 - \@lang
 - \@ban

## Games

The bot include 3 games:
 - rock paper scissors
 - rock paper scissors: Battle Royale
 - Connect 4
 
 ### Rock Paper Scissors
 
 The game knowed by everyone is available on Discord.
 You say in any channel !game pfc the bot create a game instence for your party
 
 Rules:
  - You have 3 choises Rock, Paper and Scissors
  - You select your choise in message in bot's dm and when your friend select his choise too you can see the result
  - Rock beat Scissors, Paper beat Rock and Scissors beat Paper
  
 ### Rock Paper Scissors: Battle Royale
 
 It's a battle royale mode of RPS. You can start an instence from 2 players to 100 players
 You have 1 round to beat your enemy, when you won a round, Roboto select for you another player to beat.
 
 ### Connect 4
 
 Objective: The aim for both players is to make a straight line of four own pieces; the line can be vertical, horizontal or diagonal.
 
 ### Game in progress
  - Werewolf
  - Hangman
  - Random event (if Roboto listens to more than 100 servers, the principle is to ensure that players from different servers can play together in a good mood)

## Utility commands

### User info (!ui)
Usage: !ui [optional arg]

Get user info, the user must be in the server where the command is executed. This command will get some basic info: 
- join date on the server
- register date on discord
- user status
- where is the user is connected

### Server info (!si)
Usage: !si

The command will get basic server information.

## Admin commands

### Change the bot language (\@lang)
Usage: \@lang <lang code>

Exemple to enable french on your server: \@lang fr_FR

Available language:
- English (en_US)
- French  (fr_FR)
- Spanish (es_ES)

I working on:
- German
- Portuguese
- Portuguese (BR)
- Italian

### Ban user (\@ban)
Usage: \@ban <user mention tag> <reason>

The command will ban the mentionned user.

The reason is displayed to the banned user in DM and the reason will be displayed in ban reason in your discord server settings.

### Command in development
 - !kick <user> <reason>(need administrator role or be the server owner)