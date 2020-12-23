import java.util.*;

public abstract class AI
{
   protected String name = "Your AI needs a name!!!!";

   private ArrayList<MapTile> possibleSpots = new ArrayList<MapTile>();

   public final Move runAI(ArrayList<Unit> unitsLeftToRun, boolean firstUnitInTurn)
   {
      if(unitsLeftToRun.size()>0)
      {
         possibleSpots.clear();
         Map.getMap().clearScores();
         
         //picking the next unit you want to run. It should return the index of the unit you want to run.
         int i = pickNextUnit(unitsLeftToRun);
         
         Unit u = unitsLeftToRun.get(i);
         
         //generaring the possible spots the unit can move to. This includes the spot the unit is at, but does not include every other spot with a unit.
         Map.Iterator spots = Map.getMap().generateIteratorWithinMoveRange(u.getX(),u.getY(),u.getSpeed(),u);
         while(spots.hasNext())
         {
            MapTile tile = spots.next();
            if(tile.getUnit()==null || tile.getUnit() == u)
               possibleSpots.add(tile);
         }
         
         
         //init any variables you need to
         u.getAI().init(u,unitsLeftToRun,firstUnitInTurn);
         
         //give different tiles "scores" of where you want to move the unit
         u.getAI().ratePossiblePlacesToMoveForAdvancement(u, possibleSpots);
         
         //remove bad spots from the possible spots list or assign bad scores
         u.getAI().removeDangerousMoves(u, possibleSpots);
         
         //pick the final best move based on the scores you gave / any other considerations.
         Move m =  u.getAI().pickFinalMove(u, possibleSpots);
         
         return m;
      }
      return null;
   }
   
   public abstract int pickNextUnit(ArrayList<Unit> unitsLeftToRun);
   public abstract void init(Unit u,ArrayList<Unit> unitsLeftToMove, boolean firstUnitInTurn);
   public abstract void ratePossiblePlacesToMoveForAdvancement( Unit theUnit,ArrayList<MapTile> possibleSpots);
   public abstract void removeDangerousMoves(Unit theUnit,ArrayList<MapTile> possibleSpots);
   public abstract Move pickFinalMove(Unit theUnit,ArrayList<MapTile> possibleSpots);
  
   
   public String getName()
   {
      return name;
   }
}