import java.util.*;

public class OurAI extends AI {
   boolean enemyCanKill(MapTile enemy, Unit player) {
      return enemy.getUnit().getDamage() > player.getHP();
   }

   boolean weCanKill(MapTile enemy, Unit player) {
      return player.getDamage() > enemy.getUnit().getHP();
   }

   String enemyColor(MapTile e) {
      return e.getUnit().getColor().toString();
   }

   public OurAI() {
      name = "OurAI";
   }

   public void init(Unit player, ArrayList<Unit> unitsLeftToMove, boolean firstUnitInTurn) {}

   public void ratePossiblePlacesToMoveForAdvancement(Unit player, ArrayList<MapTile> possibleSpots) {
      //algorithm to calculate scores.
      //prepares to hold checks
      ArrayList<MapTile> spots = new ArrayList<>();
      //first add all tiles with an enemy to my list
      Map.Iterator enemyMap = Map.getMap().generateIteratorAllTiles();
      while(enemyMap.hasNext()) {
         MapTile enemy = enemyMap.next();
         if(enemy.getUnit() != null && enemy.getUnit().getOwner() != player.getOwner()) {
            if (weCanKill(enemy, player)) {
               switch (enemyColor(enemy)) {
                  case "0x009999ff": //blue
                     enemy.setGoodScore(109);
                  case "0x4d4d4dff": //dark gray
                     enemy.setGoodScore(106);
                  case "0x99994dff": //gold
                     enemy.setGoodScore(103);
               }
               spots.add(enemy);
            } else if (enemyCanKill(enemy, player)) {
               enemy.setGoodScore(97);
               spots.add(enemy);
            } else {
               enemy.setGoodScore(100);
               spots.add(enemy);
            }
         }
      }

      //traversal of the graph (from center outward)
      int currentLocation = Map.initCounter();

      //starting from each enemy unit, traverse entire map, assigning scores based on distance from an enemy unit - manual traversal instead of using the iterator as I want to start from each enemy at the same time and work my way outward
      //It goes through each enemy, adding the tiles that are near to them (as long as they were not added already), each time it sets the score as the neighbor tile as -5 of the current tile
      while (spots.size() > 0) {
         //get and remove first item in the list
         MapTile here = spots.get(0);
         spots.remove(0);
         //set you visited it so you cannot visit it a second time
         here.setIt(currentLocation);
         MapTile.Iterator neighbors = here.createIterator(); //each tile has the ability to get its neighbors through the use of an iterator
         while (neighbors.hasNext()) {
            //check & getting for the next neighboring tile of mt
            MapTile next = neighbors.next();
            //cannot visit a node twice unless the next node is larger than this node, we use currentIt to indicate whether it has visited that tile previously
            if ((next.getIt() != currentLocation || next.getGoodScore() - 5 > here.getGoodScore()) && (next.getUnit() == null || next.getUnit().getOwner() == player.getOwner())) {
               // if the tile is null OR if I can move through my own unit. Cannot move through enemy units...
               next.setIt(currentLocation);
               spots.add(next);
               if (next.getGoodScore() < here.getGoodScore()) {
                  next.setGoodScore(here.getGoodScore() - 5); //every tile away from an enemy, i decrease the score by 5.
               }
            }
         }
      }
   }

   //can remove files from possibleSpots or assign bad scores
   //"don't move where an enemy can attack"
   public void removeDangerousMoves(Unit player, ArrayList<MapTile> possibleSpots) {}

   //generates the final move based on the previous methods (or, ignoring them if you prefer to write your code in this method only)
   public Move pickFinalMove(Unit player, ArrayList<MapTile> possibleSpots) {
      //sorts our array of possible spots from greatest score to lowest score, so index will always be 0 since its the first item in the list
      Collections.sort(possibleSpots, compare("score", "tiles"));

      //then it gets all the enemies if the unit was at that the new spot
      int x = possibleSpots.get(0).getX();
      int y = possibleSpots.get(0).getY();
      Vector2 pos = new Vector2(x, y);
      ArrayList<Unit> enemies = Map.getMap().getEnemyUnitsInRangeIfUnitUWasAtLocationXY(x, y, player);
      int counter = 0;
      float hp = 20;
      int index = 0;
      for(Unit e: enemies) {
         if (e.getDamage() < hp) {
            hp = e.getHP();
            index = counter;
         }
         counter++;
      }

      //then, if there is an enemy in range, it creates the move attack it
      return enemies.size() > 0 ? new Move(player, pos, true, enemies.get(index)) : new Move(player, pos, false, null);
   }

   public int pickNextUnit(ArrayList<Unit> unitsLeftToRun) {
      Collections.sort(unitsLeftToRun, compare("damage", "units"));
      Collections.sort(unitsLeftToRun, compare("range", "units"));
      return 0;
   }



   //new if else-if else statement syntax:
   //condition ? if true do : if false do
   //key.equals("owner") ? (Unit x, Unit y) -> Float.compare(x.getOwner(), y.getOwner()) : null;
   //and they can be chained, i.e. replace the null above with another condition and true and false options
   //condition ? if true do : condition ? if true do : if false do...
   //
   //sorting methods
   Comparator compare(String key, String type) {
      if (type.equals("units")) {
         //creates the comparator for type <Unit>
         Comparator<Unit> temp =
                 key.equals("hp") ? (Unit x, Unit y) -> Float.compare(x.getHP(), y.getHP()) : //sorts units by hp
                         key.equals("range") ? (Unit x, Unit y) -> Float.compare(x.getRange(), y.getRange()) : //sorts units by attack range
                                 key.equals("speed") ? (Unit x, Unit y) -> Float.compare(y.getSpeed(), x.getSpeed()) : //sorts units by move range
                                         key.equals("score") ? (Unit x, Unit y) -> Float.compare(y.getTile().getGoodScore(), x.getTile().getGoodScore()) : //sorts units by good score
                                                 key.equals("damage") ? (Unit x, Unit y) -> Float.compare(y.getDamage(), x.getDamage()) : null; //sorts units by attack damage
         return temp;
      } else if (type.equals("tiles")) {
         //creates the comparator for type <MapTile>
         Comparator<MapTile> temp =
                 key.equals("hp") ? (MapTile x, MapTile y) -> Float.compare(x.getUnit().getHP(), y.getUnit().getHP()) : //sorts tiles by hp
                         key.equals("range") ? (MapTile x, MapTile y) -> Float.compare(x.getUnit().getRange(), y.getUnit().getRange()) : //sorts tiles by attack range
                                 key.equals("speed") ? (MapTile x, MapTile y) -> Float.compare(y.getUnit().getSpeed(), x.getUnit().getSpeed()) : //sorts tiles by move range
                                         key.equals("score") ? (MapTile x, MapTile y) -> Float.compare(y.getGoodScore(), x.getGoodScore()) : //sorts tiles by good score (tiles are flipped from x, y to y, x to sort most to least)
                                                 key.equals("damage") ? (MapTile x, MapTile y) -> Float.compare(x.getUnit().getDamage(), y.getUnit().getDamage()) : null; //sorts tiles by attack damage
         return temp;
      } return null;
   }
}