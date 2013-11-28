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
	
	public CoreBaby() 
	{
		hostileX = 0;
		hostileY = 0;
		countdown = 200;
		genomeSetting = new CoreSupportGenome((int) (Math.random() * 100), (int) (Math.random() * 100));
		genBasicUnitList = new LinkedList<UnitTypes>();
	}
	
	public void AILinkData() {
		buildingGoals = builder.buildingsStack;
	}
	
	public void setup() 
	{
		initBuildStyle_siegeExpand();
		genBasicUnitList.add(UnitTypes.Terran_Marine);
		genBasicUnitList.add(UnitTypes.Terran_Vulture);
		genBasicUnitList.add(UnitTypes.Terran_Medic);
		genBasicUnitList.add(UnitTypes.Terran_Siege_Tank_Tank_Mode);
	}
	
	public void checkUp() 
	{
		/* --- Base Building --- */
		int supplyTotal = bwapi.getSelf().getSupplyTotal()/2;
		int supplyUsed = bwapi.getSelf().getSupplyUsed()/2;
		
		if (buildingGoals.size() > 0 && builder.nextToBuildIndex != -1) {
			int SCVsTotal = workers.getBaseWorkers(buildingGoals.get(builder.nextToBuildIndex).baseAssignment);
			int supplyNeeded = buildingGoals.get(builder.nextToBuildIndex).requiredSupply;
			int SCVsNeeded = buildingGoals.get(builder.nextToBuildIndex).requiredSCVs;
			
			if (supplyNeeded <= supplyTotal && SCVsNeeded <= SCVsTotal) 
				buildingGoals.get(builder.nextToBuildIndex).status = BuildStatus.ATTEMPT_BUILD;
			
			if (SCVsNeeded > SCVsTotal) 
				workers.trainWorker();
			
			if (supplyUsed + 10 > supplyTotal && buildingGoals.get(builder.nextToBuildIndex).blueprint != UnitTypes.Terran_Supply_Depot) 
					buildingGoals.add(builder.nextToBuildIndex, new BuildingRR(0, 0, 0, UnitTypes.Terran_Supply_Depot, BuildStatus.ATTEMPT_BUILD));
		}
		else
		{
			if (supplyUsed + 10 > supplyTotal) 
			{
				buildingGoals.add(new BuildingRR(0, 0, 0, UnitTypes.Terran_Supply_Depot, BuildStatus.ATTEMPT_BUILD));
				builder.nextToBuildIndex = buildingGoals.size() - 1;
			}
		}
		
		/* Add units */
		if (builder.roster.size() < 20)
			genUnitsBasic();
		
		
		/* Military Orders */
		if (countdown-- <= 0) {
			if (genomeSetting.defensiveness > (int) (Math.random() * 100)) {
				genDefendMilitaryGroup();
				if (buildingGoals.size() - builder.completedBuildingsIndex < 3)
					genDefensiveBasic();
			}
			else {
				genSpreadMilitaryGroup();
				if (buildingGoals.size() - builder.completedBuildingsIndex < 3)
					genOffensiveBasic();
			}
			countdown = genomeSetting.bloodFrequency;
		}
		
	}
	
	public void debug() 
	{
		//Put debug info here for the Baby
	}
	
	public void genUnitsBasic()
	{
		int index;

		for (index = 0; index < 4; index++)
			builder.roster.addLast(UnitTypes.Terran_Marine);
		builder.roster.addLast(UnitTypes.Terran_Medic);
		for (index = 0; index < 2; index++)
			builder.roster.addLast(UnitTypes.Terran_Siege_Tank_Tank_Mode);
		builder.roster.addLast(UnitTypes.Terran_Vulture);
		
	}
	
	public void buildOrTrain(UnitTypes toTrain) {
		UnitTypes toBuild;
		if ((toBuild = builder.techTree.canIBuildThis(toTrain.ordinal())) == null)
				builder.roster.addLast(toTrain);
		else
			buildingGoals.add(new BuildingRR(1, 1, 0, toBuild, BuildStatus.HOLD));
		
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
		System.out.println("Offensive orders sent out");
		ArrayList<Region> regions = bwapi.getMap().getRegions();
		int totalRegions = regions.size();

		for (int i = 0; i < totalRegions; i++) {
			if (genomeSetting.spread >  (i/totalRegions) * 100) {
				System.out.println("Going to region " + i);
				military.unitOperation(genBasicUnitList, 20, regions.get(i).getCenterX(), regions.get(i).getCenterY());
			}
			if (i == 0) {
				military.unitOperation(genBasicUnitList, 20, hostileX, hostileY);
				System.out.println("Base Attack " + i);
			}
		}
		System.out.println("End group spread");
	}
	
	public void genDefendMilitaryGroup() {
		System.out.println("Defensive orders sent out");
		ChokePoint entrance = null;
		Region baseStart = react.gen_findClosestRegion(builder.homePositionX, builder.homePositionY);
			if (baseStart != null && !react.gen_findClosestRegion(builder.homePositionX, builder.homePositionY).getChokePoints().isEmpty())
				entrance = (react.gen_findClosestRegion(builder.homePositionX, builder.homePositionY)).getChokePoints().get(0);
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