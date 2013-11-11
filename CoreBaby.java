package javabot.RaynorsRaiders;

import java.util.*;
import javabot.JNIBWAPI;
import javabot.RaynorsRaiders.*;
import javabot.types.UnitType.UnitTypes;

public class CoreBaby extends RRAITemplate 
{
	LinkedList<BuildOrder> buildingGoals;
	//LinkedList<Orders> unitGoals;
	
	public class BuildOrder {
		Integer supplyNeeded;
		Integer workersNeeded;
		UnitTypes unitToMake;
		Integer baseID;
		
		public BuildOrder(
	     Integer d_supply,
	     Integer d_workers,
	     UnitTypes d_unit,
	     Integer d_base) {
			supplyNeeded = d_supply;
			workersNeeded = d_workers;
			unitToMake = d_unit;
			baseID = d_base;		
		}
	}
	
	public CoreBaby() 
	{
		buildingGoals = new LinkedList<BuildOrder>();
	}
	
	public void setup() 
	{
		System.out.println("CoreBaby Online");
		// Now populate the buildingStack
		initBuildStyle_siegeExpand();
	}
	
	
	public void checkUp() 
	{
		/* Check and add to build orders if we can */
		for (BuildOrder order: buildingGoals) {
			if (
			 order.supplyNeeded <= bwapi.getSelf().getSupplyTotal() &&
			 order.workersNeeded <= workers.getBaseWorkers(order.baseID)
			) {
				react.core_econ_buildingStack.add(order.unitToMake);
				buildingGoals.remove(order);
			}
		}
		
		/* Add workers if we need to, ALL of the workers */
		if (workers.getBaseWorkers(0) < bwapi.getSelf().getSupplyTotal()) {
			react.core_econ_unitsStack.addLast(UnitTypes.Terran_SCV);
		}
	}
	
	public void debug() 
	{
		//Put debug info here for the Baby
	}
	
	public void initBuildStyle_siegeExpand() {
		buildingGoals.add(new BuildOrder(9,10,UnitTypes.Terran_Supply_Depot,0));
		buildingGoals.add(new BuildOrder(9,10,UnitTypes.Terran_Supply_Depot,0));
		buildingGoals.add(new BuildOrder(9,10,UnitTypes.Terran_Supply_Depot,0)); // the "wall"
		buildingGoals.add(new BuildOrder(12,18,UnitTypes.Terran_Barracks,0));
		buildingGoals.add(new BuildOrder(12,18,UnitTypes.Terran_Refinery,0));
		buildingGoals.add(new BuildOrder(15,18,UnitTypes.Terran_Supply_Depot,0));
		buildingGoals.add(new BuildOrder(16,26,UnitTypes.Terran_Factory,0));
		buildingGoals.add(new BuildOrder(16,26,UnitTypes.Terran_Machine_Shop,0));
		buildingGoals.add(new BuildOrder(21,26,UnitTypes.Terran_Command_Center,1));
		buildingGoals.add(new BuildOrder(24,26,UnitTypes.Terran_Supply_Depot,0));
		buildingGoals.add(new BuildOrder(25,34,UnitTypes.Terran_Siege_Tank_Siege_Mode,0)); // this might be wrong
		buildingGoals.add(new BuildOrder(28,34,UnitTypes.Terran_Engineering_Bay,0));
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
		
		builder.roster.push(UnitTypes.Terran_SCV);
		builder.roster.push(UnitTypes.Terran_SCV);
		builder.roster.push(UnitTypes.Terran_SCV);
		builder.roster.push(UnitTypes.Terran_SCV);
		builder.roster.push(UnitTypes.Terran_Marine);
		builder.roster.push(UnitTypes.Terran_Marine);
		builder.roster.push(UnitTypes.Terran_Marine);
		builder.roster.push(UnitTypes.Terran_Marine);
	}
	
}
