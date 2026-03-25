package com.pacman.web.entity;

public enum Rank {
	BRONZE, SILVER, GOLD;
	
	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
