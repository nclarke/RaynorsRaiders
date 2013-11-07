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
		//README You should NOT be calling AILinkData().  This is already called in RRAITemplate within
		// link.  NO AI should be dependent on any other AI to initalize.
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
