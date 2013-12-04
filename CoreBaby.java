package javabot.RaynorsRaiders;

import java.util.*;

import javabot.JNIBWAPI;
import javabot.RaynorsRaiders.*;
import javabot.RaynorsRaiders.ManagerBuild.BuildStatus;
import javabot.RaynorsRaiders.ManagerBuild.BuildingRR;
import javabot.model.ChokePoint;
import javabot.model.Region;
import javabot.model.Unit;
import javabot.types.UnitType;
import javabot.types.UnitType.UnitTypes;
import javabot.RaynorsRaiders.ManagerMilitary.UnitTypesRequest;

public class CoreBaby extends RRAITemplate 
{
	ArrayList<BuildingRR> buildingGoals;
	int hostileX,hostileY, countdown, campaign ;
	CoreSupportGenome genomeSetting;
	LinkedList<UnitTypes> genBasicUnitList;

	public CoreBaby() 
	{
		hostileX = 0;
		hostileY = 0;
		countdown = 100;
		genomeSetting = new CoreSupportGenome();
		genBasicUnitList = new LinkedList<UnitTypes>();
		campaign = genomeSetting.bloodFrequency * 10;
	}
	
	public void AILinkData() {
		buildingGoals = builder.buildingsStack;
		hostileX = this.info.hostileX;
		hostileY = this.info.hostileY;
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
			
			if (supplyNeeded <= supplyTotal && SCVsNeeded <= SCVsTotal && buildingGoals.get(builder.nextToBuildIndex).status == BuildStatus.HOLD)
				buildingGoals.get(builder.nextToBuildIndex).status = BuildStatus.ATTEMPT_BUILD;
			
			//if (SCVsNeeded > SCVsTotal) 
			//	workers.trainWorker();
			
			if (supplyUsed + 5 > supplyTotal && buildingGoals.get(builder.nextToBuildIndex).blueprint != UnitTypes.Terran_Supply_Depot) 
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
		workers.trainWorker();
		
		/* Add units */
		//if (builder.roster.size() < 20)
			genUnitsBasic();
		
		
		/* Military Orders */
		if (campaign > 0)
			campaign--;
		if (countdown > 0)
			countdown--;
		
		if (countdown <= 0) {
			
			if (genomeSetting.defensiveness > (int) (Math.random() * 100) && campaign > 0) {
				genDefendMilitaryGroup();
				if (buildingGoals.size() - builder.completedBuildingsIndex < 3)
					//genDefensiveBasic();
					;
			}
			else {
				if (campaign > 0) {
					genSpreadMilitaryGroup();
				}
				else {
					genFullMilitaryAssault();
				}
				if (buildingGoals.size() - builder.completedBuildingsIndex < 3) {
					//genOffensiveBasic();
					System.out.println("Ready to build more!");
				}
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

		//for (index = 0; index < 4; index++)
		builder.roster.addLast(UnitTypes.Terran_Marine);
		//builder.roster.addLast(UnitTypes.Terran_Medic);
		//for (index = 0; index < 2; index++)
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
		//buildingGoals.add(new BuildingRR(1, 1, 0, UnitTypes.Terran_Missile_Turret, BuildStatus.HOLD));
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
				military.unitOperation(genBasicUnitList, 20, info.hostileX, info.hostileY);
				System.out.println("Base Attack " + i);
			}
		}
		System.out.println("End group spread");
	}
	
	public void genFullMilitaryAssault() {
		military.unitOperation(genBasicUnitList, 20, 0,0);
		military.orderAllMilitaryTeamsToAtk(0,0);
		//military.orderUnitPoolToAtk(info.hostileX, info.hostileY);
		military.scanLocation(0,0);
		
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
		//buildingGoals.add(new BuildingRR(10, 9, 0, UnitTypes.Terran_Supply_Depot, BuildStatus.HOLD));
		//buildingGoals.add(new BuildingRR(10, 9, 0, UnitTypes.Terran_Supply_Depot, BuildStatus.HOLD));
		buildingGoals.add(new BuildingRR(0, 1, 0, UnitTypes.Terran_Barracks, BuildStatus.HOLD));
		//buildingGoals.add(new BuildingRR(0, 1, 0, UnitTypes.Terran_Bunker, BuildStatus.HOLD));
		buildingGoals.add(new BuildingRR(0, 2, 0, UnitTypes.Terran_Refinery, BuildStatus.HOLD));
		buildingGoals.add(new BuildingRR(0, 12, 0, UnitTypes.Terran_Barracks, BuildStatus.HOLD));
		buildingGoals.add(new BuildingRR(0, 16, 0, UnitTypes.Terran_Factory, BuildStatus.HOLD));
		buildingGoals.add(new BuildingRR(0, 16, 0, UnitTypes.Terran_Machine_Shop, BuildStatus.HOLD));
		buildingGoals.add(new BuildingRR(0, 16, 0, UnitTypes.Terran_Academy, BuildStatus.HOLD));
	}
	
	
	public void cleanUp(boolean winner) {
		genomeSetting.GenomeWriteNewEntry(winner);
	}
	
}