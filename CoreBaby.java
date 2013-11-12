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
		 Integer d_workers, 
		 Integer d_supply,
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
		//System.out.println("CoreBaby Online");
		// Now populate the buildingStack
		initBuildStyle_siegeExpand();
	}
	
	
	public void checkUp() 
	{
		/* Check and add to build orders if we can */
		BuildOrder order = buildingGoals.peek();
		
		if (order != null) {
			System.out.println("Order on top: " + order.supplyNeeded + "/" + order.workersNeeded
			 + " Comp: " + bwapi.getSelf().getSupplyTotal()/2 + "/" + workers.getBaseWorkers(0)
			 + " U: " + bwapi.getSelf().getSupplyUsed()/2);
			if (
			 order.supplyNeeded <= bwapi.getSelf().getSupplyTotal()/2 &&
			 order.workersNeeded <= workers.getBaseWorkers(0)
			) {
				builder.orders.addLast(order.unitToMake);
				System.out.println("Adding order to make" + order.unitToMake.toString());
				buildingGoals.remove(order);
			}
			
		}
		else
		{
			//next phase?
			
		}
		
		/*if (
		    (builder.orders.peek() != UnitTypes.Terran_Supply_Depot && bwapi.getSelf().getSupplyUsed()/2 + 5 >= bwapi.getSelf().getSupplyTotal()/2)
		   )
		{
			builder.orders.addLast(UnitTypes.Terran_Supply_Depot);
		}*/

		
		/* Add workers if we need to, ALL of the workers */
		//if (workers.getBaseWorkers(0) < bwapi.getSelf().getSupplyTotal()) {
		if (workers.getBaseWorkers(0) < 21) {
			workers.trainWorker();
		}
		builder.roster.addLast(UnitTypes.Terran_Marine);
		builder.roster.addLast(UnitTypes.Terran_Vulture);
			
		//}
	}
	
	public void debug() 
	{
		//Put debug info here for the Baby
	}
	
	public void initBuildStyle_siegeExpand() {
		buildingGoals.addLast(new BuildOrder(9,10,UnitTypes.Terran_Supply_Depot,0));
		//buildingGoals.addLast(new BuildOrder(9,10,UnitTypes.Terran_Supply_Depot,0));
		//buildingGoals.addLast(new BuildOrder(9,10,UnitTypes.Terran_Supply_Depot,0)); // the "wall"
		buildingGoals.addLast(new BuildOrder(12,18,UnitTypes.Terran_Barracks,0));
		buildingGoals.addLast(new BuildOrder(12,18,UnitTypes.Terran_Refinery,0));
		buildingGoals.addLast(new BuildOrder(15,18,UnitTypes.Terran_Supply_Depot,0));
		buildingGoals.addLast(new BuildOrder(16,26,UnitTypes.Terran_Factory,0));
		buildingGoals.addLast(new BuildOrder(16,26,UnitTypes.Terran_Machine_Shop,0));
		buildingGoals.addLast(new BuildOrder(21,26,UnitTypes.Terran_Command_Center,0));
		buildingGoals.addLast(new BuildOrder(24,26,UnitTypes.Terran_Supply_Depot,0));
		//buildingGoals.addLast(new BuildOrder(25,34,UnitTypes.Terran_Siege_Tank_Siege_Mode,0)); // this might be wrong
		buildingGoals.addLast(new BuildOrder(28,34,UnitTypes.Terran_Engineering_Bay,0));
	}
	
}
