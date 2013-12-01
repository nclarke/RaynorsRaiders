package javabot.RaynorsRaiders;

import java.awt.Point;
import java.util.*;

import javabot.JNIBWAPI;
import javabot.RaynorsRaiders.MiltScouter.Base;
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
	boolean isScouting;
	List<MiltScouter.Base> enemyBases;
	
	public ManagerInfo() {
		//SET UP ALL INTERNAL VARIABLES HERE
		super();
		hostileX = 0;
		hostileY = 0;
		scouter = new MiltScouter(this);
		isScouting = false;
		neutralUnits = new ArrayList<Unit>();
		enemyUnits = new ArrayList<Unit>();
		enemyBases = new LinkedList<MiltScouter.Base>();
	}
	
	
	public void setup() {
		selfID = bwapi.getSelf().getID();
		this.scouter.startUp();

	}
	

	public void checkUp() 
	{
//		if (this.bwapi.getFrameCount() == 1)
			this.scouter.scout();
			for(Base base : scouter.bases)
			{
				if(base.hasEnemy && !enemyBases.contains(base))
				{
					enemyBases.add(base);
				}				
			}
	
	}
	
	public void unitSeen(int unitID)
	{		
		Unit unit = bwapi.getUnit(unitID);
		if(bwapi.getUnit(unitID).getPlayerID() == 0) //neutral Unit
		{
			if(!this.neutralUnits.contains(unit))
			{
				neutralUnits.add(unit);
				System.out.println("New Neautral unit discovered of type: "+bwapi.getUnitType(unitID));
			}
		}
		else if(bwapi.getUnit(unitID).getPlayerID() != selfID) //enemy Unit
		{
			if(!this.enemyUnits.contains(unit))
			{
				enemyUnits.add(unit);
				System.out.println("New Enemy unit discovered of type: "+bwapi.getUnitType(unitID));
			}
		
		}
		else
		{
			//our unit
		}
	}
	
	public void unitDestoryed(int unitID)
	{		
		Unit unit = bwapi.getUnit(unitID);
		System.out.println("destroyed unitID: "+unitID);


		if (unitID == this.scouter.scout.getID())
		{
			System.out.println("scout destroyed of tyep:"+scouter.scout.getTypeID());
			if (this.scouter.scout.getTypeID() == UnitTypes.Terran_SCV.ordinal())
				workers.checkInWorker(scouter.scout.getID());
			this.scouter.scout = null;
			this.scouter.bases.get(scouter.currIndex).hasEnemy = true;
			System.out.println("scout checked back in");

			
		}

		for(Unit u : this.enemyUnits)
		{
			if(u.getID() == unitID)
				this.enemyUnits.remove(u);
		}
		for(Unit u : this.neutralUnits)
		{
			if(u.getID() == unitID)
				this.neutralUnits.remove(u);
		}
		
	}

	
	public void debug() {
	}
	
}

