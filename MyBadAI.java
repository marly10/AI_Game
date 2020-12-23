import java.util.*;
import javafx.scene.paint.*;
import javafx.scene.*;

public class MyBadAI extends AI
{
   public MyBadAI()
   {
      name = "BadAI";
   }
   /* Order of methods called by the AI */
   // 1. pickNextUnit
   // 2. Init
   // 3. ratePossibleToMoveForAdvancement
   // 4. removeDangerousMoves
   // 5. pickFinalMove


   //if you need to init any information. Such as clear out arraylists you make...
   //[harder] or, alternatively,  you could generate moves for every one of your units in this method and then assign them in pick final move.
   public void init(Unit u, ArrayList<Unit> unitsLeftToMove, boolean firstUnitInTurn)
   {

   }

   public void ratePossiblePlacesToMoveForAdvancement( Unit theUnit,ArrayList<MapTile> possibleSpots)
   {
      //if(true)
         //return; //don't actually do any of these steps for the bad AI
   
      //example of getting all tiles in the map. Starts from top left corner
      Map.Iterator spots = Map.getMap().generateIteratorAllTiles();
      while(spots.hasNext())
      {
         MapTile tile = spots.next();
         //do something here.
      }

      //example of getting all tiles of move range of a unit -> tiles are in order starting from X/Y point and then nearest tiles next. No guarentees on which tiles will be called if there is a tie
      spots = Map.getMap().generateIteratorWithinMoveRange(theUnit.getX(),theUnit.getY(),theUnit.getSpeed(),theUnit);
      while(spots.hasNext())
      {
         MapTile tile = spots.next();
         //do something here.
         tile.setColor(Color.PURPLE);  //this was really so I can see what the AI is doing. You will probably find you want to display what the AI is doing too
      }

      
      //you can put the scores on the tiles in the GameController @ about line 307. Its currently commented out.

      //example of getting all tiles of attack range of a unit. Starts in top left corner of the square (if it was shaped as a square) and goes row major until bottom right
      spots = Map.getMap().generateIteratorWithinAttackRange(theUnit.getX(),theUnit.getY(),theUnit.getRange(),theUnit);
      MapTile tile = null;
      while(spots.hasNext())
      {
         tile = spots.next();
         //do something here.
      }
      
      
      //tile.getUnit() returns null if no unit is present or the unit if a unit is present
      
      //examples of getting something from a unit
      if(tile.getUnit() != null)
      {
         tile.getUnit().getHP(); //get the current HP
         tile.getUnit().getType(); //get the type of unit
         tile.getUnit().getRange(); //attack range
         tile.getUnit().getSpeed(); //move range
         tile.getUnit().getOwner(); //integer indicating the owner. compare to "theUnit"'s owner to know if it is yours or the other
         tile.getUnit().getX(); //get tile position
         tile.getUnit().getY(); //get tile position
         tile.getUnit().getTile(); //get tile the unit is at
         
         tile.setGoodScore(100); // setting the good score to 100 -> default (cleared) value is -9000.
         tile.setBadScore(99); //setting the bad score to 99 -> default (cleared) value is -9000. These scores are interpreterd how you want to interpret them
         //possibleSpots.remove(0); remove a spot for the possible spots AL
         tile.getGoodScore();
         tile.getBadScore();
         
         MapTile.Iterator tileIterator = tile.createIterator(); //getting an iterator that gives all neighboring tiles
       
       
         MapTile t = Map.getMap().getTile(5,5); // getting a specific tile from the map  
         Map.getMap(); // getting the map
      }
   }
   
   //I suggest you use this to score bad moves OR remove bad possibleMoves. However, If no spots are left in possibleSpots, it will crash
   public void removeDangerousMoves(Unit theUnit,ArrayList<MapTile> possibleSpots)
   {
   
   }
   
   //generates the final move based on the previous methods (or, ignoring them if you prefer to write your code in this method only)
   public Move pickFinalMove(Unit theUnit,ArrayList<MapTile> possibleSpots)
   {
      //my AI is bad, so it just picks a valid spot to move to.
      Random gen = new Random();      
      int selected = gen.nextInt(possibleSpots.size());
      
      //then it gets all the enemies if the unit was at that the new spot
      ArrayList<Unit> enemiesInRange = Map.getMap().getEnemyUnitsInRangeIfUnitUWasAtLocationXY(possibleSpots.get(selected).getX(),possibleSpots.get(selected).getY(),theUnit);
      
      //then, if there is an enemy in range, it creates the move attack it
      Move m=null;
      if(enemiesInRange.size() > 0)
      {
         m = new Move(theUnit,new Vector2(possibleSpots.get(selected).getX(),possibleSpots.get(selected).getY()),true,enemiesInRange.get(0));
      }
      else
      {
         //otherwise just moves there
         m = new Move(theUnit,new Vector2(possibleSpots.get(selected).getX(),possibleSpots.get(selected).getY()),false,null);
      }
      return m;
   }
   
   //returns the index of the unit in unitsLeftToRun you want to run. Return 0 if don't care. The arraylist will probably be in different orders each time it is called.   
   public int pickNextUnit(ArrayList<Unit> unitsLeftToRun)
   {
      return 0; //its a bad AI! just returns the first unit. 
   }
}
