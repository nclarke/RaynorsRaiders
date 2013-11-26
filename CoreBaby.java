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
	CoreSupportGenome genomeSetting;
	
	LinkedList<UnitTypes> genBasicUnitList;

	
	
	public class CoreSupportGenome {
		int bloodFrequency;
		int mutation;
		int responsePotential;
		int spread;
		int defensiveness;
		
		CoreSupportGenome(int mutation, int phase) {
			bloodFrequency = Math.abs(mutation) % 100;
			mutation += phase;
			responsePotential = Math.abs(mutation) % 100;
			mutation += phase;
			spread = Math.abs(mutation) % 100;
			mutation += phase;
			defensiveness = Math.abs(mutation) % 100;
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
		hostileX = 0;
		hostileY = 0;
		countdown = 200;
		militaryGoals = new LinkedList<MilitaryOrder>();
		unitMixtures = new LinkedList<UnitTypes>();
		genomeSetting = new CoreSupportGenome((int) (Math.random() * 100), (int) (Math.random() * 100));
		System.out.println("Checking random " + (int) (Math.random() * 100));
		genBasicUnitList = new LinkedList<UnitTypes>();
		genBasicUnitList.add(UnitTypes.Terran_Marine);
		genBasicUnitList.add(UnitTypes.Terran_Vulture);
		genBasicUnitList.add(UnitTypes.Terran_Medic);
		genBasicUnitList.add(UnitTypes.Terran_Siege_Tank_Tank_Mode);
	}
	
	public void AILinkData() {
		buildingGoals = builder.buildingsStack;
	}
	public void setup() 
	{
		//Begin tactics path
		initBuildStyle_siegeExpand();
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
		
		/* Military Orders */
		if (countdown == 0) {
			genUnitsBasic();
			if (genomeSetting.defensiveness > Math.random() % 100) {
				genDefendMilitaryGroup();
				genDefensiveBasic();
			}
			else {
				genSpreadMilitaryGroup();
				genOffensiveBasic();
			}
			countdown = genomeSetting.bloodFrequency;
		}
		else {
			genUnitsBasic();
			countdown--;
		}
	}
	
	public void debug() 
	{
		//Put debug info here for the Baby
	}
	
	public void genUnitsBasic()
	{
		int index;
		for (index = 0; index < 2; index++)
		{
			builder.roster.addLast(UnitTypes.Terran_Marine);
			builder.roster.addLast(UnitTypes.Terran_Vulture);
		}
		for (index = 0; index < 1; index++)
		{
			builder.roster.addLast(UnitTypes.Terran_Siege_Tank_Tank_Mode);
		}
		
	}
	
	public void genDefensiveBasic() {
		buildingGoals.add(new BuildingRR(1, 1, 0, UnitTypes.Terran_Bunker, BuildStatus.HOLD));
		buildingGoals.add(new BuildingRR(1, 1, 0, UnitTypes.Terran_Missile_Turret, BuildStatus.HOLD));
	}
	
	public void genOffensiveBasic() {
		buildingGoals.add(new BuildingRR(1, 1, 0, UnitTypes.Terran_Barracks, BuildStatus.HOLD));
		buildingGoals.add(new BuildingRR(1, 1, 0, UnitTypes.Terran_Factory, BuildStatus.HOLD));
		buildingGoals.add(new BuildingRR(1, 1, 0, UnitTypes.Terran_Machine_Shop, BuildStatus.HOLD));
	}
	
	
	public void genSpreadMilitaryGroup() {
		ArrayList<Region> regions = bwapi.getMap().getRegions();
		int totalRegions = regions.size();

		for (int i = 0; i < totalRegions; i++) {
			if (genomeSetting.spread >  (i/totalRegions) * 100) {
				military.unitOperation(genBasicUnitList, 20, regions.get(i).getCenterX(), regions.get(i).getCenterY());
			}
			if (genomeSetting.spread <= 1/totalRegions) {
				military.unitOperation(genBasicUnitList, 20, hostileX, hostileY);
			}
		}
	}
	
	public void genDefendMilitaryGroup() {
		ChokePoint entrance = null;
		Region baseStart = react.gen_findClosestRegion(builder.homePositionX, builder.homePositionY);
		if (baseStart != null) 
		{
			if (react.gen_findClosestRegion(builder.homePositionX, builder.homePositionY).getChokePoints().isEmpty())
			{
			}
			else 
				entrance = (react.gen_findClosestRegion(builder.homePositionX, builder.homePositionY)).getChokePoints().get(0);

		}
		military.unitOperation(genBasicUnitList, 20, entrance.getFirstSideX(), entrance.getFirstSideY());
	}

	
	public void initBuildStyle_siegeExpand() 
	{
		buildingGoals.add(new BuildingRR(10, 9, 0, UnitTypes.Terran_Supply_Depot, BuildStatus.HOLD));
		buildingGoals.add(new BuildingRR(10, 9, 0, UnitTypes.Terran_Supply_Depot, BuildStatus.HOLD));
		buildingGoals.add(new BuildingRR(18, 12, 0, UnitTypes.Terran_Barracks, BuildStatus.HOLD));
		buildingGoals.add(new BuildingRR(18, 12, 0, UnitTypes.Terran_Refinery, BuildStatus.HOLD));
		buildingGoals.add(new BuildingRR(18, 12, 0, UnitTypes.Terran_Barracks, BuildStatus.HOLD));
		buildingGoals.add(new BuildingRR(26, 16, 0, UnitTypes.Terran_Factory, BuildStatus.HOLD));
		buildingGoals.add(new BuildingRR(26, 16, 0, UnitTypes.Terran_Factory, BuildStatus.HOLD));
		buildingGoals.add(new BuildingRR(26, 16, 0, UnitTypes.Terran_Machine_Shop, BuildStatus.HOLD));
		buildingGoals.add(new BuildingRR(26, 16, 0, UnitTypes.Terran_Machine_Shop, BuildStatus.HOLD));
	}
	
}