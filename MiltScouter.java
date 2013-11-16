package javabot.RaynorsRaiders;

import java.awt.Point;
import java.util.*;

import javabot.JNIBWAPI;
import javabot.RaynorsRaiders.Level;
import javabot.model.*;
import javabot.types.UnitType.UnitTypes;
import javabot.util.*;

public class MiltScouter
{

	/* EnumMap for us to know what UnitTypes to train per Level - Level is determined by how well AI is doing in the game 
	 * (AI is very rich in the beginning, gets its level changed from ZERO to TWO) */
//	int homePositionX, homePositionY;
	BaseLocation homeBase;
	Unit scout;
	LinkedList<Tile> scoutingPositions;
	ManagerInfo mInfo;
	
	LinkedList<Unit> hostilUnits;
	
	/*store-enemy-base?*/
	

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
	
	
	
	public MiltScouter(ManagerInfo mInfo)
	{
		this.mInfo = mInfo;
		System.out.println("Scouter online");
		this.scoutingPositions = new LinkedList<Tile>();
	}
	
	public void startUp()
	{
	}

	
	public void checkUp() {
		//Check up - FIXME - code needs to go here.
		scout();
	}

	
	/*
	 * Gets the scout unit at the very start of the game
	 * 
	 * Returns the scout Unit, a Unit with ID of -1 if not
	 */
	public Unit getNewScoutUnit()
	{
		int ID = this.mInfo.workers.checkOutWorker(ManagerWorkers.workerOrders.SCOUT, 0);
		for (Unit unit : this.mInfo.bwapi.getMyUnits())
		{
			if (unit.getID() == ID)
			{
				addEnemyBases();
				return unit;
			}
		}
		
		return null;
		
	}
	
	private Unit getNewScoutUnit(int typeID)
	{
		if (typeID == UnitTypes.Terran_SCV.ordinal())
			return getNewScoutUnit();

		for (Unit unit : mInfo.bwapi.getMyUnits())
			if (unit.getTypeID() == typeID && (unit.isIdle()))
				return new Unit(unit.getID());
		
		return new Unit(-1);
	}
	

	/*scout to a specific position immediately*/
	public void scout(int X, int Y)
	{
		this.scoutingPositions.addFirst(new Tile(X, Y));
		scout();
	}


	/*scout to a specific position after other positions have been seen*/
	public void scoutEventually(int X, int Y)
	{
		this.scoutingPositions.add(new Tile(X, Y));
		scout();
	}
	
	
	/*makes scouting more generic
	 * so it can be called any time in game
	 */
	public void scout(){
		if (this.scout == null){
			this.scout = getNewScoutUnit();
			System.out.println("new scout is: "+this.scout);
			if (this.scout == null)
				return;
		}
		if(scoutHasArrived() && !this.scoutingPositions.isEmpty()){
			Tile next=this.scoutingPositions.peek();
			System.out.println("new scouting location at ("+next.getX()+","+next.getY()+")");
			mInfo.bwapi.move(scout.getID(), next.getX(),next.getY());
		}
		else if(this.scoutingPositions.isEmpty()){
			System.out.println("no more scouting locations");
			mInfo.workers.checkInWorker(scout.getID());
			this.scout = null;
		}
	}
	
	
	
	public boolean scoutHasArrived()
	{
		Tile next;
		if(!this.scoutingPositions.isEmpty()){
			next=this.scoutingPositions.peek();
/*			System.out.println("scout going to: ("+next.getX()+","+next.getY()+")");
			System.out.println("   and is now at: ("+scout.getX()+","+scout.getY()+")");
			System.out.println("this is your home location: ("+mInfo.military.homePositionX+","+mInfo.military.homePositionY+")");
*/
			//			System.out.println("frameCoutn: "+MM.bwapi.getFrameCount());
			if(!((scout.getX() < (next.getX() + 100) && scout.getX() > (next.getX() - 100) ) 
					&& (scout.getY() < (next.getY() + 100) && scout.getY() > (next.getY() - 100) )))
			{
				mInfo.bwapi.move(scout.getID(), next.getX(),next.getY());
				//				System.out.println("not there yet");
				return false;
			}	
			else
			{
			//					System.out.println("here3");
				//scout has reached the Tile, so he can go scout some more
				this.scoutingPositions.pop();
				return true;
			}
		}
		return false;
	}
	
	
	
	
	   // Returns the id of a unit of a given type, that is closest to a pixel position (x,y), or -1 if we
    // don't have a unit of this type
    public int getNearestUnit(int unitTypeID, int x, int y) 
    {
    	int nearestID = -1;
	    double nearestDist = 9999999;
	    for (Unit unit : mInfo.bwapi.getMyUnits()) 
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
    
	/*
	 * Gets all the possible the base locations on the map
	 * 
	 * Returns an ArrayList of those base locations other than our home base location
	 */
	public void addEnemyBases()
	{		
		for (BaseLocation b : mInfo.bwapi.getMap().getBaseLocations()) 
		{
			
			if (b.isStartLocation() && (b.getX() != mInfo.military.homePositionX) && (b.getY() != mInfo.military.homePositionY)) 
			{
				System.out.println("this is your home location: ("+mInfo.military.homePositionX+","+mInfo.military.homePositionY+")");
				this.scoutingPositions.add(new Tile(b.getX(), b.getY()));//, y).Tile(b.getX(),b.getY()));
			}
		}
		this.scoutingPositions.add(new Tile(mInfo.military.homePositionX, mInfo.military.homePositionY));
	}

}
