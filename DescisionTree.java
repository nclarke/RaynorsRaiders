package javabot.RaynorsRaiders;

import java.awt.Point;
import java.util.*;

import javabot.JNIBWAPI;
import javabot.RaynorsRaiders.Level;
import javabot.model.*;
import javabot.types.UnitType.UnitTypes;
import javabot.util.*;

public class DescisionTree {

	ManagerBuild mBld;
	
	
	public DescisionTree(ManagerBuild mBld)
	{
		this.mBld = mBld;

	}

	
	public UnitTypes canIBuildThis(Unit unit)
	{
		return canIBuildThis(unit.getTypeID());
	}
	
	//return null if dont need to build anythign else
	public UnitTypes canIBuildThis(int unitType)
	{
		if (unitType == UnitTypes.Terran_Barracks.ordinal() || unitType == UnitTypes.Terran_Engineering_Bay.ordinal())
		{
			for (Unit building : this.mBld.builtBuildings)		
				if (building.getID() == UnitTypes.Terran_Command_Center.ordinal())
					return null;
			return UnitTypes.Terran_Command_Center;
		}
		else if (unitType == UnitTypes.Terran_Bunker.ordinal() || unitType == UnitTypes.Terran_Academy.ordinal() || unitType == UnitTypes.Terran_Factory.ordinal())
		{
			for (Unit building : this.mBld.builtBuildings)		
				if (building.getID() == UnitTypes.Terran_Barracks.ordinal())
					return null;
			return UnitTypes.Terran_Barracks;
		}
		else if (unitType == UnitTypes.Terran_Starport.ordinal() || unitType == UnitTypes.Terran_Armory.ordinal())
		{
			for (Unit building : this.mBld.builtBuildings)		
				if (building.getID() == UnitTypes.Terran_Factory.ordinal())
					return null;
			return UnitTypes.Terran_Factory;
		}
		else if (unitType == UnitTypes.Terran_Science_Facility.ordinal())
		{
			for (Unit building : this.mBld.builtBuildings)		
				if (building.getID() == UnitTypes.Terran_Starport.ordinal())
					return null;
			return UnitTypes.Terran_Starport;
		}

		else if (unitType == UnitTypes.Terran_Marine.ordinal())
		{
			for (Unit building : this.mBld.builtBuildings)		
				if (building.getID() == UnitTypes.Terran_Barracks.ordinal())
					return null;
			return UnitTypes.Terran_Barracks;
		}
		else if (unitType == UnitTypes.Terran_Firebat.ordinal() || unitType == UnitTypes.Terran_Medic.ordinal())
		{
			for (Unit building1 : this.mBld.builtBuildings)		
			{
				if (building1.getID() == UnitTypes.Terran_Barracks.ordinal())
				{
					for (Unit building2 : this.mBld.builtBuildings)	
					{
						if (building2.getID() == UnitTypes.Terran_Academy.ordinal())						
							return null;						
					}
					return UnitTypes.Terran_Academy;				
				}
			}
			return UnitTypes.Terran_Barracks;
		}
		else if (unitType == UnitTypes.Terran_Ghost.ordinal())
		{
			for (Unit building1 : this.mBld.builtBuildings)		
			{
				if (building1.getID() == UnitTypes.Terran_Barracks.ordinal())
				{
					for (Unit building2 : this.mBld.builtBuildings)	
					{
						if (building2.getID() == UnitTypes.Terran_Academy.ordinal())	
						{
							for (Unit building3 : this.mBld.builtBuildings)	
							{
								if (building3.getID() == UnitTypes.Terran_Science_Facility.ordinal())	
								{
									return null;
								}								
							}						
							return UnitTypes.Terran_Science_Facility;
						}
					}
					return UnitTypes.Terran_Academy;				
				}
			}
			return UnitTypes.Terran_Barracks;
		}
		else if (unitType == UnitTypes.Terran_Vulture.ordinal())
		{
			for (Unit building : this.mBld.builtBuildings)		
				if (building.getID() == UnitTypes.Terran_Factory.ordinal())
					return null;
			return UnitTypes.Terran_Factory;
		}

		else if (unitType == UnitTypes.Terran_Wraith.ordinal())
		{
			for (Unit building : this.mBld.builtBuildings)		
				if (building.getID() == UnitTypes.Terran_Starport.ordinal())
					return null;
			return UnitTypes.Terran_Starport;
		}

		else if (unitType == UnitTypes.Terran_Siege_Tank_Tank_Mode.ordinal() || unitType == UnitTypes.Terran_Siege_Tank_Siege_Mode.ordinal())
		{
			for (Unit building1 : this.mBld.builtBuildings)		
			{
				if (building1.getID() == UnitTypes.Terran_Factory.ordinal())
				{
					for (Unit building2 : this.mBld.builtBuildings)	
					{
						if (building2.getID() == UnitTypes.Terran_Machine_Shop.ordinal())						
							return null;						
					}
					return UnitTypes.Terran_Machine_Shop;				
				}
			}
			return UnitTypes.Terran_Factory;
		}
		else if (unitType == UnitTypes.Terran_Goliath.ordinal())
		{
			for (Unit building1 : this.mBld.builtBuildings)		
			{
				if (building1.getID() == UnitTypes.Terran_Factory.ordinal())
				{
					for (Unit building2 : this.mBld.builtBuildings)	
					{
						if (building2.getID() == UnitTypes.Terran_Armory.ordinal())						
							return null;						
					}
					return UnitTypes.Terran_Armory;				
				}
			}
			return UnitTypes.Terran_Factory;
		}

		else if (unitType == UnitTypes.Terran_Science_Vessel.ordinal())
		{
			for (Unit building1 : this.mBld.builtBuildings)		
			{
				if (building1.getID() == UnitTypes.Terran_Control_Tower.ordinal())
				{
					for (Unit building2 : this.mBld.builtBuildings)	
					{
						if (building2.getID() == UnitTypes.Terran_Academy.ordinal())	
						{
							for (Unit building3 : this.mBld.builtBuildings)	
							{
								if (building3.getID() == UnitTypes.Terran_Science_Facility.ordinal())	
								{
									return null;
								}								
							}						
							return UnitTypes.Terran_Science_Facility;
						}
					}
					return UnitTypes.Terran_Control_Tower;				
				}
			}
			return UnitTypes.Terran_Starport;
		}

		else if (unitType == UnitTypes.Terran_Goliath.ordinal())
		{
			for (Unit building1 : this.mBld.builtBuildings)		
			{
				if (building1.getID() == UnitTypes.Terran_Starport.ordinal())
				{
					for (Unit building2 : this.mBld.builtBuildings)	
					{
						if (building2.getID() == UnitTypes.Terran_Control_Tower.ordinal())						
							return null;						
					}
					return UnitTypes.Terran_Control_Tower;				
				}
			}
			return UnitTypes.Terran_Starport;
		}

		
/*
		else if (unitType == UnitTypes.Terran_Ghost.ordinal())
		{
			for (Unit building : this.mBld.builtBuildings)		
				if (building.getID() == UnitTypes.Terran_Factory.ordinal())
					return null;
			return UnitTypes.Terran_Factory;
		}
		*/

		return null;
	}

}
