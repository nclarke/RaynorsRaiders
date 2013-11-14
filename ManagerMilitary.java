package javabot.RaynorsRaiders;

import java.awt.Point;
import java.util.*;

import javabot.JNIBWAPI;
import javabot.RaynorsRaiders.Level;
import javabot.model.*;
import javabot.types.UnitType.UnitTypes;
import javabot.util.*;

public class ManagerMilitary extends RRAITemplate
{
	int homePositionX, homePositionY;
	MiltScouter scouter;
	BaseLocation homeBase;
	
	EnumMap<UnitTypes, LinkedList<Unit>> militaryUnits;
	
	public class Tile {
		int x;
		int y;
		public Tile(int x,int y){
			this.x=x;
			this.y=y;
		}
		public int getX(){
			return this.x;
		}
		public int getY(){
			return this.y;
		}
	}
	
	public ManagerMilitary()
	{		
		militaryUnits = new EnumMap<UnitTypes, LinkedList<Unit>>(UnitTypes.class);
		initMilitaryUnit();
		scouter = new MiltScouter(this);
	}
	
	public void AILinkData() {
		//None here
	}
	
	public void setup() {
		//System.out.println("ManageMilitary online");
	}
	
	public void startUp()
	{
		setHomePosition();
	}
	
	public void checkUp() {
		this.scouter.scout();
		
		//for testing purposes - tries to send 5 marines to an enemy base atm
		/*for (BaseLocation b : bwapi.getMap().getBaseLocations()) 
		{
			if (b.isStartLocation() )
			{
				unitOperation(b.getX(), b.getY()); 
			}
		}*/
	}
	
    public void debug()
    {
    	int spacing = 10;
		
		// Draw our home position.
		bwapi.drawText(new Point(5,0), "Our home position: " + String.valueOf(homePositionX) + ", " + String.valueOf(homePositionY), true);
		for (BaseLocation b : bwapi.getMap().getBaseLocations()) {
			if (b.isStartLocation()) {
				int index  = spacing/10;
				bwapi.drawText(new Point(5,spacing), "Base position " + index + ": " + String.valueOf(b.getX()) + ", " + String.valueOf(b.getY()), true);
			}
			spacing =+ 10;
		}
		
		//bwapi.drawText(new Point(5,120), "Total Marines trained: " + String.valueOf(getCurrentUnitCount(UnitTypes.Terran_Marine)), true);
    }
    
	public void setHomePosition()
	{
		int cc = getNearestUnit(UnitTypes.Terran_Command_Center.ordinal(), 0, 0);
		if (cc == -1) cc = getNearestUnit(UnitTypes.Zerg_Hatchery.ordinal(), 0, 0);
		if (cc == -1) cc = getNearestUnit(UnitTypes.Protoss_Nexus.ordinal(), 0, 0);
			homePositionX = bwapi.getUnit(cc).getX();
			homePositionY = bwapi.getUnit(cc).getY();
			
		int index;
		for(index=0;index<bwapi.getMap().getBaseLocations().size();index++){
			BaseLocation tmpBase = bwapi.getMap().getBaseLocations().get(index);
			if(tmpBase.getX()==homePositionX && tmpBase.getY()==homePositionY){
				this.homeBase=tmpBase;
			}				
		}
	}
	
	
	private void initMilitaryUnit()
	{
		for(UnitTypes ut: UnitTypes.values())
		{
			LinkedList<Unit> tmp = new LinkedList<Unit>();
			militaryUnits.put(ut, tmp);
		}
	}
	
	public void addMilitaryUnit(Unit unitObj, UnitTypes unitType)
	{
		System.out.println("Military Manager: Adding unit " + unitObj.getID() + " to militaryUnit");
		militaryUnits.get(unitType).add(unitObj);
		System.out.println("Military Manager: Added unit " + unitObj.getID() + " to militaryUnit");
	}
	
	public void removeMilitaryUnit(Unit unitObj, UnitTypes unitType)
	{
		//System.out.println("Military Manager: Removing unit " + unitObj.getID() + " in militaryUnit");
		militaryUnits.get(unitType).remove(unitObj);
		//System.out.println("Military Manager: Removd unit " + unitObj.getID() + " in militaryUnit");
	}
	
	public void addCreatedMilitaryUnits(Unit createdUnit, int unitTypeID)
	{
		if(unitTypeID == UnitTypes.Terran_Marine.ordinal())
			addMilitaryUnit(createdUnit, UnitTypes.Terran_Marine);
		
		if(unitTypeID == UnitTypes.Terran_Firebat.ordinal())
			addMilitaryUnit(createdUnit, UnitTypes.Terran_Firebat);
		
		if(unitTypeID == UnitTypes.Terran_Ghost.ordinal())
			addMilitaryUnit(createdUnit, UnitTypes.Terran_Ghost);
			
		if(unitTypeID == UnitTypes.Terran_Goliath.ordinal())
			addMilitaryUnit(createdUnit, UnitTypes.Terran_Goliath);
			
		if(unitTypeID == UnitTypes.Terran_Medic.ordinal())
			addMilitaryUnit(createdUnit, UnitTypes.Terran_Medic);
			
		if(unitTypeID == UnitTypes.Terran_Valkyrie.ordinal())
			addMilitaryUnit(createdUnit, UnitTypes.Terran_Valkyrie);
			
		if(unitTypeID == UnitTypes.Terran_Vulture.ordinal())
			addMilitaryUnit(createdUnit, UnitTypes.Terran_Vulture);
			
		if(unitTypeID == UnitTypes.Terran_Vulture_Spider_Mine.ordinal())
			addMilitaryUnit(createdUnit, UnitTypes.Terran_Vulture_Spider_Mine);
			
		if(unitTypeID == UnitTypes.Terran_Dropship.ordinal())
			addMilitaryUnit(createdUnit, UnitTypes.Terran_Dropship);
			
		if(unitTypeID == UnitTypes.Terran_Siege_Tank_Tank_Mode.ordinal())
			addMilitaryUnit(createdUnit, UnitTypes.Terran_Siege_Tank_Tank_Mode);
			
		if(unitTypeID == UnitTypes.Terran_Siege_Tank_Siege_Mode.ordinal())
			addMilitaryUnit(createdUnit, UnitTypes.Terran_Siege_Tank_Siege_Mode);
			
		if(unitTypeID == UnitTypes.Terran_Wraith.ordinal())
			addMilitaryUnit(createdUnit, UnitTypes.Terran_Wraith);
	}
	
	public void removeDestroyedMilitaryUnits(Unit createdUnit, int unitTypeID)
	{
		if(unitTypeID == UnitTypes.Terran_Marine.ordinal())
			removeMilitaryUnit(createdUnit, UnitTypes.Terran_Marine);
		
		if(unitTypeID == UnitTypes.Terran_Firebat.ordinal())
			removeMilitaryUnit(createdUnit, UnitTypes.Terran_Firebat);
		
		if(unitTypeID == UnitTypes.Terran_Ghost.ordinal())
			removeMilitaryUnit(createdUnit, UnitTypes.Terran_Ghost);
			
		if(unitTypeID == UnitTypes.Terran_Goliath.ordinal())
			removeMilitaryUnit(createdUnit, UnitTypes.Terran_Goliath);
			
		if(unitTypeID == UnitTypes.Terran_Medic.ordinal())
			removeMilitaryUnit(createdUnit, UnitTypes.Terran_Medic);
			
		if(unitTypeID == UnitTypes.Terran_Valkyrie.ordinal())
			removeMilitaryUnit(createdUnit, UnitTypes.Terran_Valkyrie);
			
		if(unitTypeID == UnitTypes.Terran_Vulture.ordinal())
			removeMilitaryUnit(createdUnit, UnitTypes.Terran_Vulture);
			
		if(unitTypeID == UnitTypes.Terran_Vulture_Spider_Mine.ordinal())
			removeMilitaryUnit(createdUnit, UnitTypes.Terran_Vulture_Spider_Mine);
			
		if(unitTypeID == UnitTypes.Terran_Dropship.ordinal())
			removeMilitaryUnit(createdUnit, UnitTypes.Terran_Dropship);
			
		if(unitTypeID == UnitTypes.Terran_Siege_Tank_Tank_Mode.ordinal())
			removeMilitaryUnit(createdUnit, UnitTypes.Terran_Siege_Tank_Tank_Mode);
			
		if(unitTypeID == UnitTypes.Terran_Siege_Tank_Siege_Mode.ordinal())
			removeMilitaryUnit(createdUnit, UnitTypes.Terran_Siege_Tank_Siege_Mode);
			
		if(unitTypeID == UnitTypes.Terran_Wraith.ordinal())
			removeMilitaryUnit(createdUnit, UnitTypes.Terran_Wraith);
	}
	
	/*
	 * Method used for gathering the specified UnitTypes and # of units to attack a location
	 * 
	 * unitTypes:	UnitTypes of units requested
	 * numOfUnits:  number of units needed (don't know how many units you want each)
	 * locationX:   Pixel X coordinate on the map
	 * locationY:   Pixel X coordinate on the map
	 */
	public void unitOperation(List<UnitTypes> unitTypes, int numOfUnits,  int locationX, int locationY)
	{
		LinkedList<Unit> tmp = new LinkedList<Unit>();
		
		for(UnitTypes unitTy: unitTypes)
		{
			for(Unit ut: militaryUnits.get(unitTy))
			{
				if(tmp.size() < numOfUnits)
				{
					if(ut.isIdle())
					{
						System.out.println("Military Manager: Adding unit " + ut.getID() + " to group");
						tmp.add(ut);
						System.out.println("Military Manager: Added unit " + ut.getID() + " to group");
					}
				}
			}
		}
		
		if(!tmp.isEmpty())
		{
			unitOperationHelper(tmp, locationX, locationY);
		}
	}
	
	public void unitOperation(int numOfUnits, int locationX, int locationY)
	{
		LinkedList<Unit> tmp = new LinkedList<Unit>();
		
		for(Unit ut: militaryUnits.get(UnitTypes.Terran_Marine))
		{
			if(tmp.size() < numOfUnits)
			{
				if(ut.isIdle())
				{
					System.out.println("Military Manager: Adding unit " + ut.getID() + " to group");
					tmp.add(ut);
					System.out.println("Military Manager: Added unit " + ut.getID() + " to group");
				}
			}
		}
		
		if(!tmp.isEmpty())
		{
			unitOperationHelper(tmp, locationX, locationY);
		}
	}
	
	public void unitOperation(int locationX, int locationY)
	{
		LinkedList<Unit> tmp = new LinkedList<Unit>();
		
		for(Unit ut: militaryUnits.get(UnitTypes.Terran_Marine))
		{
			if(tmp.size() < 5)
			{
				//if(ut.isIdle())
				//{
					System.out.println("Military Manager: Adding unit " + ut.getID() + " to group");
					tmp.add(ut);
					System.out.println("Military Manager: Added unit " + ut.getID() + " to group");
				//}
			}
		}
		
		if(!tmp.isEmpty())
		{
			unitOperationHelper(tmp, locationX, locationY);
		}
	}
	
	/*
	 * Helper function to execute unit attacks to a location 
	 */
	private void unitOperationHelper(LinkedList<Unit> unitGroup, int locationX, int locationY)
	{
		//rallyUnits(unitGroup, homePositionX, homePositionY);
		//if(rallyReadyCheck(unitGroup, homePositionX, homePositionY))
		//{
			System.out.println("Military Manager: In unitOperationHelper");
			attackEnemyLocation(unitGroup, locationX, locationY);
			System.out.println("Military Manager: Exited unitOperationHelper");
		//}
	}
	
	/*
	 * Commands the Units stored in an LinkedList to attack a location on the map
	 * 
	 * unitFormation:   LinkedList of Units we wanted to rally
	 * pixelPositionX:  Rally position's X
	 * pixelPositionY:  Rally position's Y
	 * 
	 */
	private void rallyUnits(LinkedList<Unit> unitGroup, int pixelPositionX, int pixelPositionY )
	{
		if(unitGroup != null)
		{
			for(Unit unit: unitGroup)
			{
				bwapi.move(unit.getID(), pixelPositionX, pixelPositionY);
			}
		}
		//System.out.println("RALLY TEST");
	}
	
	/*
	 * Checks to see if all the units in a group are gathered at Command Center 
	 * before going out to attack together
	 */
	private boolean rallyReadyCheck(LinkedList<Unit> unitGroup, int pixelPositionX, int pixelPositionY)
	{
		boolean checkReadyFlag = false;
		
		if(unitGroup != null)
		{
			if((unitGroup.getLast().getX() == pixelPositionX) && (unitGroup.getLast().getY() == pixelPositionY))
			{
				checkReadyFlag = true;
			}
		}
		return checkReadyFlag;
	}
	
	/*
	 * Rally the Units stored in an LinkedList to a location on the map
	 * 
	 * unitFormation:   LinkedList of Units we wanted to rally
	 * pixelPositionX:  Attack position's X
	 * pixelPositionY:  Attack position's Y
	 * 
	 */
	private void attackEnemyLocation(LinkedList<Unit> unitGroup, int pixelPositionX, int pixelPositionY)
	{
		if(unitGroup != null)
		{
			for(Unit unit: unitGroup)
			{
				System.out.println("Military Manager: Attack Ordering to unit " + unit.getID());
				bwapi.attack(unit.getID(), pixelPositionX, pixelPositionY);
				System.out.println("Military Manager: Attack ordered to unit " + unit.getID());
			}
			//System.out.println("ATTACK TEST");
		}
	}
	
	/*
	 * Returns the current number of units of a UnitType
	 */
	private int getCurrentUnitCount(UnitTypes unitType)
	{
		return militaryUnits.get(unitType).size();
	}
	
	private int getWorkersCount()
	{
		int unitsTotal = 0;
		
		for (Unit unit : bwapi.getMyUnits())
		{
			if(unit.getTypeID() == UnitTypes.Terran_SCV.ordinal())
			{
				unitsTotal++;
			}
		}
		return unitsTotal;
	}
	
	// Returns the id of a unit of a given type, that is closest to a pixel position (x,y), or -1 if we
    // don't have a unit of this type
    public int getNearestUnit(int unitTypeID, int x, int y) 
    {
    	int nearestID = -1;
	    double nearestDist = 9999999;
	    for (Unit unit : bwapi.getMyUnits()) 
	    {
	    	if ((unit.getTypeID() != unitTypeID) || (!unit.isCompleted())) continue;
	    	double dist = Math.sqrt(Math.pow(unit.getX() - x, 2) + Math.pow(unit.getY() - y, 2));
	    	if (nearestID == -1 || dist < nearestDist) 
	    	{
	    		nearestID = unit.getID();
	    		nearestDist = dist;
	    	}
	    }
	    return nearestID;
    }
    

}
