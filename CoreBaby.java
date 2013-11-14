package javabot.RaynorsRaiders;

import java.util.*;

import javabot.JNIBWAPI;
import javabot.RaynorsRaiders.*;
import javabot.model.ChokePoint;
import javabot.model.Region;
import javabot.types.UnitType;
import javabot.types.UnitType.UnitTypes;

public class CoreBaby extends RRAITemplate 
{
	LinkedList<BuildOrder> buildingGoals;
	LinkedList<MilitaryOrder> militaryGoals;
	LinkedList<UnitTypes> unitMixtures;
	
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
	
	public enum BattleMode
	{
		ENTRENCH, MARAUD, RAZE
	}
	
	public enum BattleTargets
	{
		MILITARY, INNOCENTS, ALL
	}
	
	public class MilitaryOrder {
		Integer strength;
		Integer x,y;
		Integer wobble;
		BattleMode mode;
		BattleTargets target;
		LinkedList<UnitTypes> units;
		
		public MilitaryOrder(
		 Integer d_strength,
		 Integer d_xLoc,
		 Integer d_yLoc,
		 Integer d_wobble,
		 BattleMode d_mode,
		 BattleTargets d_target,
		 LinkedList<UnitTypes> d_units)
		{
			strength = d_strength;
			x = d_xLoc;
			y = d_yLoc;
			wobble = d_wobble;
			mode = d_mode;
			target = d_target;
			units = d_units;
		}
	}
	
	public CoreBaby() 
	{
		buildingGoals = new LinkedList<BuildOrder>();
		militaryGoals = new LinkedList<MilitaryOrder>();
		unitMixtures = new LinkedList<UnitTypes>();
	}
	
	public void setup() 
	{
		//Begin tactics path
		initBuildStyle_siegeExpand();
		initUnitStyle_basic();
	}
	
	public void startUp()
	{
		initEntrenchBase();
	}
	
	
	public void checkUp() 
	{
		/* --- Base Building --- */
		BuildOrder order = buildingGoals.peek();
		
		if (order != null) {
			//System.out.println("Order on top: " + order.supplyNeeded + "/" + order.workersNeeded
			// + " Comp: " + bwapi.getSelf().getSupplyTotal()/2 + "/" + workers.getBaseWorkers(0)
			// + " U: " + bwapi.getSelf().getSupplyUsed()/2);
			if (
			 order.supplyNeeded <= bwapi.getSelf().getSupplyTotal()/2 &&
			 order.workersNeeded <= workers.getBaseWorkers(0)
			) 
			{
				builder.orders.addLast(order.unitToMake);
				////System.out.println("Adding order to make" + order.unitToMake.toString());
				buildingGoals.remove(order);
			}
			
			if (order.workersNeeded > workers.getBaseWorkers(0)) 
			{	
				workers.trainWorker();
			}
			
			if (
			 bwapi.getSelf().getSupplyUsed()/2 + 10 > bwapi.getSelf().getSupplyTotal()/2) 
			{
				////System.out.println("Supply depot needed");
				if (builder.orders.peek() != UnitTypes.Terran_Supply_Depot) 
				{
					////System.out.println("Adding supply depot since supply is running out");
					builder.orders.addFirst(UnitTypes.Terran_Supply_Depot);
				}
			}
			
		}
		else
		{
			if (bwapi.getSelf().getSupplyUsed()/2 + 4 > bwapi.getSelf().getSupplyTotal()/2) 
			{
				builder.orders.addFirst(UnitTypes.Terran_Supply_Depot);
			}
		}
		
		/* Add units */
		UnitTypes unit = unitMixtures.peek();
		
		if (unit != null)
		{
			//builder.roster.addLast(UnitTypes.Terran_Marine);
			//builder.roster.addLast(UnitTypes.Terran_Vulture);
			
			builder.roster.addLast(unit);
			unitMixtures.pop();
		}
		
		/* Military Orders */
		MilitaryOrder groundPound = militaryGoals.peek();
		
		if (groundPound != null)
		{
			if (true)
			{
				military.unitOperation(groundPound.units, groundPound.strength, groundPound.x, groundPound.y);
				militaryGoals.pop();
			}
		}
	}
	
	public void debug() 
	{
		//Put debug info here for the Baby
	}
	
	public void initUnitStyle_basic()
	{
		for (int i = 0; i < 20; i++)
		{
			//System.out.println("Adding units");
			unitMixtures.add(UnitTypes.Terran_Marine);
			unitMixtures.add(UnitTypes.Terran_Vulture);
		}
		
	}
	
	public void initEntrenchBase() 
	{
		ChokePoint entrance = null;
		Region baseStart = react.gen_findClosestRegion(builder.homePositionX, builder.homePositionY);
		if (baseStart != null) 
		{
			if (react.gen_findClosestRegion(builder.homePositionX, builder.homePositionY).getChokePoints().isEmpty())
			{
				//System.out.println("No chokepoint?");
			}
			else 
			{
			entrance = (react.gen_findClosestRegion(builder.homePositionX, builder.homePositionY)).getChokePoints().get(0);
			}
		}
		LinkedList<UnitTypes> unitList = new LinkedList<UnitTypes>();
		unitList.add(UnitTypes.Terran_Marine);
		unitList.add(UnitTypes.Terran_Vulture);
		
		militaryGoals.add(
		 new MilitaryOrder(
		  5,
		  entrance.getFirstSideX(),
		  entrance.getFirstSideY(),
		  0,
		  BattleMode.ENTRENCH,
		  BattleTargets.ALL,
		  unitList
		 )
		);
	}
	
	public void initBuildStyle_siegeExpand() 
	{
		//buildingGoals.addLast(new BuildOrder(9,10,UnitTypes.Terran_Supply_Depot,0));
		//buildingGoals.addLast(new BuildOrder(9,10,UnitTypes.Terran_Supply_Depot,0));
		//buildingGoals.addLast(new BuildOrder(9,10,UnitTypes.Terran_Supply_Depot,0)); // the "wall"
		buildingGoals.addLast(new BuildOrder(12,18,UnitTypes.Terran_Barracks,0));
		buildingGoals.addLast(new BuildOrder(12,18,UnitTypes.Terran_Refinery,0));
		buildingGoals.addLast(new BuildOrder(12,18,UnitTypes.Terran_Barracks,0));
		//buildingGoals.addLast(new BuildOrder(15,18,UnitTypes.Terran_Supply_Depot,0));
		buildingGoals.addLast(new BuildOrder(16,26,UnitTypes.Terran_Factory,0));
		buildingGoals.addLast(new BuildOrder(16,26,UnitTypes.Terran_Machine_Shop,0));
		buildingGoals.addLast(new BuildOrder(21,26,UnitTypes.Terran_Command_Center,1));
		//buildingGoals.addLast(new BuildOrder(24,26,UnitTypes.Terran_Supply_Depot,0));
		//buildingGoals.addLast(new BuildOrder(25,34,UnitTypes.Terran_Siege_Tank_Siege_Mode,0)); // this might be wrong
		buildingGoals.addLast(new BuildOrder(28,34,UnitTypes.Terran_Engineering_Bay,0));
	}
	
}