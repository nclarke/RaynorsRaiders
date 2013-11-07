package javabot.RaynorsRaiders;

import javabot.JNIBWAPI;
import javabot.RaynorsRaiders.*;
import javabot.types.UnitType.UnitTypes;

public class CoreBaby extends RRAITemplate {
	
	public CoreBaby() {
		//Nothing to init yet
	}
	
	public void startUpSequence() {
		// Now populate the buildingStack
		buildBasicBase();
	}
	
	
	public void checkUp() {
		//Baby is doing nothing during runtime
	}
	
	

	public void startUp() {
		this.builder.AILinkData();
		this.military.AILinkData();
	}
	
	
	
	public void buildBasicBase() {
		
		builder.orders.push(UnitTypes.Terran_Supply_Depot);
		builder.orders.push(UnitTypes.Terran_Supply_Depot);
		builder.orders.push(UnitTypes.Terran_Barracks);
		builder.orders.push(UnitTypes.Terran_Refinery);
		builder.orders.push(UnitTypes.Terran_Supply_Depot);
		builder.orders.push(UnitTypes.Terran_Bunker);
		builder.orders.push(UnitTypes.Terran_Bunker);
		
		builder.orders.push(UnitTypes.Terran_SCV);
		builder.orders.push(UnitTypes.Terran_SCV);
		builder.orders.push(UnitTypes.Terran_SCV);
		builder.orders.push(UnitTypes.Terran_SCV);
		builder.orders.push(UnitTypes.Terran_Marine);
		builder.orders.push(UnitTypes.Terran_Marine);
		builder.orders.push(UnitTypes.Terran_Marine);
		builder.orders.push(UnitTypes.Terran_Marine);
	}
	
}
