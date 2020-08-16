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
				return "option1 / " + nombreOption;
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
			try {
				nombreOption = Integer.parseInt(option);
			} catch (Exception e) {
				where--;
				return "nbrOption";
			}
			if (nombreOption < 3 || nombreOption > 10) {
				where--;
				return "nbrOption";
			}
			options = new String[nombreOption];
			return "option1 / " + nombreOption;
		} else {
			int curopt = where - 4;
			options[curopt] = option;
			if ((curopt) < nombreOption) {
				return "option" + (curopt) + " / " + nombreOption;
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

	private void setDescription(String description) {
		this.description = description;
	}

	public String[] getOptions() {
		return this.options;
	}
	
}
