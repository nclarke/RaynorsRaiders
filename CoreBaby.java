package javabot.RaynorsRaiders;

import javabot.JNIBWAPI;
import javabot.RaynorsRaiders.*;
import javabot.types.UnitType.UnitTypes;

public class CoreBaby extends RRAITemplate 
{
	
	public CoreBaby() 
	{
		//Nothing to init yet
	}
	
	public void startUp() 
	{
		System.out.println("CoreBaby Online");
		// Now populate the buildingStack
		buildBasicBase();
	}
	
	
	public void checkUp() 
	{
		//Baby is doing nothing during runtime
	}
	
	public void debug() 
	{
		//Put debug info here for the Baby
	}
	
	
	public void buildBasicBase() 
	{
		
		builder.orders.push(UnitTypes.Terran_Supply_Depot);
		builder.orders.push(UnitTypes.Terran_Supply_Depot);
		builder.orders.push(UnitTypes.Terran_Barracks);
		builder.orders.push(UnitTypes.Terran_Refinery);
		builder.orders.push(UnitTypes.Terran_Supply_Depot);
		builder.orders.push(UnitTypes.Terran_Bunker);
		builder.orders.push(UnitTypes.Terran_Bunker);
		
		/*builder.orders.push(UnitTypes.Terran_SCV);
		builder.orders.push(UnitTypes.Terran_SCV);
		builder.orders.push(UnitTypes.Terran_SCV);
		builder.orders.push(UnitTypes.Terran_SCV);
		builder.orders.push(UnitTypes.Terran_Marine);
		builder.orders.push(UnitTypes.Terran_Marine);
		builder.orders.push(UnitTypes.Terran_Marine);
		builder.orders.push(UnitTypes.Terran_Marine);*/
	}
	
}
