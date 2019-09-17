package com.github.ungarscool1.Roboto.entity;

import org.javacord.api.entity.user.User;

public class Vote {
	private String name;
	private String description;
	private boolean multi;
	private int nombreOption;
	private String[] options;
	private User author;
	private int where;
	
	public Vote(User user) {
		author = user;
		where = 0;
	}
	
	public String builder(String option) {
		if (option.equals("")) {
			if (where-1 == 0) {
				return "description";
			} else if (where-1 == 1) {
				return "multi";
			} else if (where-1 == 2) {
				return "nbrOption";
			} else if (where-1 == 3) {
				return "option";
			} else {
				return "option";
			}
		}
		where++;
		if (where-1 == 0) {
			setName(option);
			return "description";
		} else if (where-1 == 1) {
			setDescription(option);
			return "multi";
		} else if (where-1 == 2) {
			if (option.equalsIgnoreCase("oui") || option.equalsIgnoreCase("ui") || option.equalsIgnoreCase("yes") || option.equalsIgnoreCase("yep") || option.equalsIgnoreCase("yop")) {
				multi = true;
				return "nbrOption";
			} else if (option.equalsIgnoreCase("non") || option.equalsIgnoreCase("nan") || option.equalsIgnoreCase("nah") || option.equalsIgnoreCase("no") || option.equalsIgnoreCase("nope")){
				return "fini";
			} else {
				where--;
				return "multi";
			}
		} else if (where-1 == 3) {
			nombreOption = Integer.parseInt(option);
			if (nombreOption < 1 || nombreOption > 10) {
				where--;
				return "nbrOption";
			}
			System.out.println("Il y a " + Integer.parseInt(option) + " options");
			options = new String[nombreOption];
			return "option";
		} else {
			int curopt = where - 5; 
			System.out.println("Option " + (curopt + 1) + "/" + nombreOption);
			options[curopt] = option;
			if ((curopt + 1) < nombreOption) {
				return "option" + (curopt + 1) + " / " + nombreOption;
			} else {
				return "fin multi";
			}
		}
		
	}
	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public User getAuthor() {
		return author;
	}
	
	public String[] getOptions() {
		return this.options;
	}
	
}
