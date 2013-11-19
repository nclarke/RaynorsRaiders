package javabot.RaynorsRaiders;

import java.util.*;

import javabot.JNIBWAPI;
import javabot.RaynorsRaiders.*;
import javabot.RaynorsRaiders.ManagerBuild.BuildStatus;
import javabot.RaynorsRaiders.ManagerBuild.BuildingRR;
import javabot.model.ChokePoint;
import javabot.model.Region;
import javabot.types.UnitType;
import javabot.types.UnitType.UnitTypes;

public class CoreBaby extends RRAITemplate 
{
	ArrayList<BuildingRR> buildingGoals;
	LinkedList<MilitaryOrder> militaryGoals;
	LinkedList<UnitTypes> unitMixtures;
	int hostileX,hostileY, countdown;
	
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
		hostileX = 0;
		hostileY = 0;
		countdown = 2000;
		militaryGoals = new LinkedList<MilitaryOrder>();
		unitMixtures = new LinkedList<UnitTypes>();
	}
	
	public void AILinkData() {
		buildingGoals = builder.buildingsStack;
	}
	public void setup() 
	{
		//Begin tactics path
		initBuildStyle_siegeExpand();
		initUnitStyle_basic();
	}
	
	public void startUp()
	{
		int index;
		initEntrenchBase();
		for (index = 0; index < 100; index++)
		{
			builder.roster.addLast(UnitTypes.Terran_Marine);
			builder.roster.addLast(UnitTypes.Terran_Vulture);
		}
		for (index = 0; index < 50; index++)
		{
			builder.roster.addLast(UnitTypes.Terran_Siege_Tank_Tank_Mode);
		}
	}
	
	
	public void checkUp() 
	{
		int supplyTotal = bwapi.getSelf().getSupplyTotal()/2;
		int supplyUsed = bwapi.getSelf().getSupplyUsed()/2;
		/* --- Base Building --- */
		
		if (buildingGoals.size() > 0 && builder.nextToBuildIndex != -1) {
			int SCVsTotal = workers.getBaseWorkers(buildingGoals.get(builder.nextToBuildIndex).baseAssignment);
			int supplyNeeded = buildingGoals.get(builder.nextToBuildIndex).requiredSupply;
			int SCVsNeeded = buildingGoals.get(builder.nextToBuildIndex).requiredSCVs;
			//System.out.println("Order on top: " + order.supplyNeeded + "/" + order.workersNeeded
			// + " Comp: " + bwapi.getSelf().getSupplyTotal()/2 + "/" + workers.getBaseWorkers(0)
			// + " U: " + bwapi.getSelf().getSupplyUsed()/2);
			if (supplyNeeded <= supplyTotal && SCVsNeeded <= SCVsTotal) 
			{
				buildingGoals.get(builder.nextToBuildIndex).status = BuildStatus.ATTEMPT_BUILD;
			}
			
			if (SCVsNeeded > SCVsTotal) 
			{	
				workers.trainWorker();
			}
			
			if (supplyUsed + 4 > supplyTotal) 
			{
				if (buildingGoals.get(builder.nextToBuildIndex).blueprint != UnitTypes.Terran_Supply_Depot) 
				{
					buildingGoals.add(builder.nextToBuildIndex, new BuildingRR(0, 0, 0, UnitTypes.Terran_Supply_Depot, BuildStatus.ATTEMPT_BUILD));
				}
			}
		}
		else
		{
			if (supplyUsed + 4 > supplyTotal) 
			{
				buildingGoals.add(new BuildingRR(0, 0, 0, UnitTypes.Terran_Supply_Depot, BuildStatus.ATTEMPT_BUILD));
				builder.nextToBuildIndex = buildingGoals.size() - 1;
			}
		}
		
		/* Add units */
		UnitTypes unit = unitMixtures.peek();
		
		//if (unit != null)
		//{
			
			//builder.roster.addLast(unit);
			//unitMixtures.pop();
		//}
		
		/* Military Orders */
		MilitaryOrder groundPound = militaryGoals.peek();
		
		if (groundPound != null)
		{
			if (true)
			{
				military.unitOperation(groundPound.units, groundPound.strength, groundPound.x, groundPound.y);
				for(int index = 0; index < military.currUnitGroups.size(); index++)
				{
					military.handleUnitsAttacking(military.currUnitGroups.get(index), groundPound.strength, groundPound.y);
				}
				militaryGoals.pop();
			}
		}
		if (countdown == 0)
		{
			military.unitOperation(hostileX, hostileY);
		}
		else
		{
			countdown--;
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
			unitMixtures.add(UnitTypes.Terran_Medic);
			unitMixtures.add(UnitTypes.Terran_Siege_Tank_Tank_Mode);
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
		//buildingGoals.add(new BuildingRR(5, 10, 0, UnitTypes.Terran_Bunker, BuildStatus.HOLD));
		//buildingGoals.add(new BuildingRR(6, 10, 0, UnitTypes.Terran_Bunker, BuildStatus.HOLD));
		buildingGoals.add(new BuildingRR(10, 9, 0, UnitTypes.Terran_Supply_Depot, BuildStatus.HOLD));
		buildingGoals.add(new BuildingRR(10, 9, 0, UnitTypes.Terran_Supply_Depot, BuildStatus.HOLD));
		buildingGoals.add(new BuildingRR(18, 12, 0, UnitTypes.Terran_Barracks, BuildStatus.HOLD));
		buildingGoals.add(new BuildingRR(18, 12, 0, UnitTypes.Terran_Barracks, BuildStatus.HOLD));
		buildingGoals.add(new BuildingRR(26, 16, 0, UnitTypes.Terran_Factory, BuildStatus.HOLD));
		buildingGoals.add(new BuildingRR(26, 16, 0, UnitTypes.Terran_Factory, BuildStatus.HOLD));
		buildingGoals.add(new BuildingRR(26, 16, 0, UnitTypes.Terran_Machine_Shop, BuildStatus.HOLD));
		buildingGoals.add(new BuildingRR(26, 16, 0, UnitTypes.Terran_Machine_Shop, BuildStatus.HOLD));
	}
	
}