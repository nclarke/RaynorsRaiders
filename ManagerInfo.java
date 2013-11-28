package javabot.RaynorsRaiders;

import java.awt.Point;
import java.util.*;

import javabot.JNIBWAPI;
import javabot.model.BaseLocation;
import javabot.model.Unit;
import javabot.types.TechType;
import javabot.types.TechType.TechTypes;
import javabot.types.UnitType;
import javabot.types.UnitType.UnitTypes;
import javabot.types.UpgradeType;
import javabot.types.UpgradeType.UpgradeTypes;
import javabot.util.BWColor;



public class ManagerInfo extends RRAITemplate
{

	MiltScouter scouter;
	List<Unit> neutralUnits;
	List<Unit> enemyUnits;
	int selfID;
	int hostileX;
	int hostileY;
	
	public ManagerInfo() {
		//SET UP ALL INTERNAL VARIABLES HERE
		super();
		hostileX = 0;
		hostileY = 0;
		scouter = new MiltScouter(this);
		neutralUnits = new ArrayList<Unit>();
		enemyUnits = new ArrayList<Unit>();
	}
	
	
	public void setup() {
		selfID = bwapi.getSelf().getID();
		this.scouter.startUp();

	}
	

	public void checkUp() 
	{
		if (this.bwapi.getFrameCount() % 1050 == 0)
			this.scouter.scout();
	
	}
	
	public void unitSeen(int unitID)
	{		
		Unit unit = bwapi.getUnit(unitID);
		if(bwapi.getUnit(unitID).getPlayerID() == 0) //neutral Unit
		{
			if(!this.neutralUnits.contains(unit))
			{
				neutralUnits.add(unit);
			}
		}
		else if(bwapi.getUnit(unitID).getPlayerID() != selfID) //enemy Unit
		{
			if(!this.enemyUnits.contains(unit))
			{
				enemyUnits.add(unit);
				//System.out.println("New Enemy unit discovered of type: "+bwapi.getUnitType(unitID));
			}
		
		}
		else
		{
			//our unit
		}
/*		
		System.out.println("this was the unit discovered: "+unitID);
		System.out.println("of type: "+bwapi.getUnitType(unitID));
		System.out.println("of player ID: "+bwapi.getUnit(unitID).getPlayerID());
		System.out.println("of type ID: "+bwapi.getUnit(unitID).getTypeID());
		System.out.println("\n");
		*/
	}
	
	public void unitDestoryed(int unitID)
	{		
		Unit unit = bwapi.getUnit(unitID);
		//System.out.println("destroyed unitID: "+unitID);
//		System.out.println("ID of: "+bwapi.getUnit(unitID).getPlayerID());
		if (unitID == this.scouter.scout.getID())
		{
			//System.out.println("scout destroyed of tyep:"+scouter.scout.getTypeID());
			if (this.scouter.scout.getTypeID() == UnitTypes.Terran_SCV.ordinal())
				workers.checkInWorker(scouter.scout.getID());
			this.scouter.scout = null;
			//System.out.println("scout checked back in");

			
		}
		//System.out.println("here");
/*		System.out.println("ID of: "+bwapi.getUnit(unitID).getPlayerID());
		if(bwapi.getUnit(unitID).getPlayerID() == 0) //neutral Unit
		{
			System.out.println("here2");
			if(this.neutralUnits.contains(unit))
			{
				neutralUnits.remove(unit);
			}
		}
		else if(bwapi.getUnit(unitID).getPlayerID() != selfID) //enemy Unit
		{
			System.out.println("here3");
			if(this.enemyUnits.contains(unit))
			{
				enemyUnits.remove(unit);
				System.out.println("Enemy unit distroeyed of type: "+bwapi.getUnitType(unitID));
			}
		
		}
		else
		{
			System.out.println("here4");
			//our unit
		}*/
		//System.out.println("done");
		
	}

	
	public void debug() {
	}
	
}

