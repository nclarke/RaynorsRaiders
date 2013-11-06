package javabot.RaynorsRaiders;

import javabot.JNIBWAPI;

public class RRAITemplate {
	JNIBWAPI bwapi;
	CoreReactive react;
	CoreBaby baby;
	ManagerBuild builder;
	ManagerMilitary military;
	ManagerWorkers workers;
	
	public RRAITemplate() {
		//Do nothing
	}
	
	public void AILink(
	 JNIBWAPI d_bwapi,
	 CoreReactive d_react,
	 CoreBaby d_baby,
	 ManagerBuild d_builder,
	 ManagerMilitary d_military,
	 ManagerWorkers d_workers
	 ) {
	    bwapi = d_bwapi;
		react = d_react;
		baby = d_baby;
		builder = d_builder;
		military = d_military;
		workers = d_workers;
		AILinkData();
	}
	
	// This is so AIs can link data if they need to
	// they only need to rewrite this function in
	// their code
	public void AILinkData() {
		//Remember by this time all AI pointers are pointing to their respective AIs
		//So you can use react.whatever, baby.something, ect
	}
	
	public void startUp() {
	}
	
	
	public void checkUp() {
	}
	
	public void debug() {
		
	}
	
}
