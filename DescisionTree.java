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
	
	private boolean canIBuildThis(Unit unit)
	{
		if (unit.getID() == UnitTypes.Terran_Marine.ordinal())
			for (Unit building : this.mBld.builtBuildings)		
				if (building.getID() == UnitTypes.Terran_Barracks.ordinal())
					return true;
		

		return false;
	}

}
