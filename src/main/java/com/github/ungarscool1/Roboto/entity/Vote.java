package com.github.ungarscool1.Roboto.entity;

import com.github.ungarscool1.Roboto.enums.VoteEnum;
import org.javacord.api.entity.user.User;

public class Vote {
	private String name;
	private String description;
	private int optionsNumber;
	private String[] options;
	private boolean multi;
	private int where = 0;
	
	public VoteEnum builder(String option) {
		if (option.equals(""))
			return (where - 1 >= 0 && where -1 <= 3) ? VoteEnum.findByValue(where - 1) : VoteEnum.FILL;
		where++;
		if (where-1 == 0)
			setName(option);
		else if (where-1 == 1)
			setDescription(option);
		else if (where-1 == 2) {
			if (option.equalsIgnoreCase("oui") || option.equalsIgnoreCase("ui") || option.equalsIgnoreCase("yes") || option.equalsIgnoreCase("yep") || option.equalsIgnoreCase("yop")) {
				multi = true;
				return VoteEnum.OPTIONNUMBERS;
			} else if (option.equalsIgnoreCase("non") || option.equalsIgnoreCase("nan") || option.equalsIgnoreCase("nah") || option.equalsIgnoreCase("no") || option.equalsIgnoreCase("nope"))
				return VoteEnum.ENDED;
			else
				where--;
		} else if (where-1 == 3) {
			setOptionNumber(option);
		} else {
			options[where - 5] = option;
			if (where - 4 >= optionsNumber) {
				return VoteEnum.MULTIPLE_ENDED;
			}
		}
		return (where - 1 >= 0 && where -1 <= 3) ? VoteEnum.findByValue(where - 1) : VoteEnum.FILL;
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

	public int getWhere() {
		return where;
	}

	private void setOptionNumber(String option) {
		try {
			optionsNumber = Integer.parseInt(option);
		} catch (Exception e) {
			where--;
		}
		if (optionsNumber < 3 || optionsNumber > 10)
			where--;
		options = new String[optionsNumber];
	}
	
}
