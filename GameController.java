//see main for the few things you might want to change.

//this class manages the game, the units, and the drawing of them units

import javafx.event.*;
import javafx.stage.*;
import javafx.scene.canvas.*;
import javafx.scene.paint.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.animation.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.geometry.*;
import javafx.application.Application;
import java.io.*;
import java.util.*;
import java.text.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class GameController
{
   //factories for the units
   Factory st = new Factory(1,4,2,10,new Color(.3,.3,.3,1)); //2
   Factory mt = new Factory(1,3,4,20,new Color(.6,.6,.3,1)); //4
   Factory rk = new Factory(3,2,4,5,new Color(0,.6,.6,1));  //6 

   int [][] templatemap; //keeping track of this game's map

   float counter = 0; //keeps track of the time elapsed

   ArrayList<Unit> theUnits = new ArrayList<Unit>(); //variable to keep track of all units on a side that still need to be run
   int side=0; //which player is moving next
   int maxSide=2; //total number of players. Its always two.

   int winningSide=-1; //who won
   AI winningAI; //which AI won

   long totalTimeOfAICallone; //for keeping track of the time the 1AI took
   long totalTimeOfAICallzero;//for keeping track of the time the 0AI took

   int moves=0; //number of moves taken

   public AI getWinningAI()
   {
      return winningAI;
   }

   public float Update(Canvas theCanvas, double time) //this is off by a factor of 10x (i.e., a time of 10 is equivilant to
   {

      //if tie game.
      if(moves >= 2000)
      {
         GraphicsContext gc = theCanvas.getGraphicsContext2D();
         gc.setFill(Color.WHITE);
         gc.fillText("TIE!",500,30);
         return 0;
      }

      counter += time; //time elapsed counter. resets when counter > its limit.

      float score=-1;

      //change the 10f below to speed up the play. it can do as low as .01f without error (I didn't test it lower than that)
      //if(counter > 7f && winningSide==-1)//  if(counter > 10f && winningSide==-1)

      if(counter > 2f && winningSide==-1)
      {
         boolean first=false;

         //if no more units to run, then gather the other side's units
         if(theUnits.size() == 0)
         {
            first=true;
            Map.Iterator it = Map.getMap().generateIteratorAllTiles();
            while(it.hasNext())
            {
               MapTile m = it.next();

               if(m.getUnit() != null && m.getUnit().getOwner() == side)
                  theUnits.add(m.getUnit());
            }

            side++;
            if(maxSide <= side)
            {
               side = 0;
            }

         }
         if(theUnits.size() > 0) //needed on victory, otherwise tries to run a player with 0 units. Sad days.
         {
            moves++;

            //getting the move from the AI. The AI picks and unit, which then uses that particular AI's unit to move.


            long timeStart =  System.nanoTime();
            Move m = theUnits.get(0).runAI(theUnits,first); //this does not mean the 0th unit is moved. But the AI is called originally from the 0th unit.
            long timeEnd =  System.nanoTime();

            //timings
            if(side == 0)
            {
               totalTimeOfAICallzero+=timeEnd - timeStart;
            }
            if(side == 1)
            {
               totalTimeOfAICallone+=timeEnd - timeStart;
            }

            //getting the data from the move object & making the move
            Unit ut = m.getToMove();
            Vector2 vec = m.getNewPosition();



            //verifying the move position is valid...
            boolean found=false;
            Map.Iterator spots = Map.getMap().generateIteratorWithinMoveRange(ut.getX(),ut.getY(),ut.getSpeed(),ut);
            while(spots.hasNext())
            {
               MapTile tile = spots.next();
               if(tile.getX() == vec.getX() && tile.getY() == vec.getY())
               {
                  found = true;
                  break;
               }
            }

            if(!found)
            {
               System.out.println("bad move detected");
            }

            if(vec.getX() != ut.getX() || ut.getY() != vec.getY()) //if not moving to same spot
            {
               MapTile mt = Map.getMap().getTile(vec.getX(),vec.getY());
               if(mt.getUnit() != null)
               {
                  System.out.println("ERRor, Trying to move to place with a unit already");
                  System.out.println(m);
               }
               ut.setTile(mt);

            }
            //if attacks
            if(m.getAttacks())
            {
               Unit toAttack = m.getToAttack();

               int distance = (int) (Math.abs(ut.getX() - toAttack.getX()));
               distance+= (int) (Math.abs(ut.getY() - toAttack.getY()));

               if(ut.getRange() >= distance)
               {
                  ut.attack(toAttack);
                  if(toAttack.getHP() >0 && toAttack.getRange() >= distance)
                  {
                     toAttack.attack(ut);
                  }

                  if(ut.getHP()<=0)
                  {
                     ut.remove();
                  }
                  if(toAttack.getHP() <=0)
                  {
                     toAttack.remove();
                  }

               }
               else
               {
                  System.out.println("Invalid attack command "+ m+"\n"+ut+"\n"+toAttack);
               }
            }


            //remove the unit after done
            theUnits.remove(ut);
         }


         //reseting counter and keeping track of whether a unit is in range of another unit.
         counter = 0;
      }

      //getting the scores for each player each time & determining a winner
      float score0 = 0;
      float score1 = 0;
      Map.Iterator it = Map.getMap().generateIteratorAllTiles();
      while(it.hasNext())
      {
         MapTile mb = it.next();
         if(mb.getUnit() != null)
         {
            if(mb.getUnit().getOwner()==0)
               score0+= mb.getUnit().getHP();
            else if (mb.getUnit().getOwner()==1)
               score1+= mb.getUnit().getHP();
         }
      }
      if(score0 == 0)
      {
         winningSide = 1;
         score = score1;
         winningAI = oneAI;
      }
      else if(score1 == 0)
      {
         winningSide = 0;
         score = score0;
         winningAI = zeroAI;
      }


      if(theCanvas!= null)
         draw(theCanvas);

      return score;
   }

   public long getAI0Time(){return totalTimeOfAICallzero/moves;}
   public long getAI1Time(){return totalTimeOfAICallone/moves;}


   AI oneAI;
   AI zeroAI;

   //starting the game.. initializing all variables
   public void Start(AI playerZero, AI playerOne, int[][] map)
   {
      oneAI = playerOne;
      zeroAI = playerZero;
      totalTimeOfAICallone=0;
      totalTimeOfAICallzero=0;
      winningSide=-1;
      moves=0;
      winningAI=null;
      theUnits.clear();
      templatemap = map;

      //blanking the map and remaking it
      Map.resetMap(map);

      //creating the units
      for(int i=0;i<templatemap.length;i++)
      {
         for(int j=0;j<templatemap[0].length;j++)
         {
            int spot = templatemap[i][j];

            if(spot != 0 && spot != -1)
            {
               int owner = spot & 0x1;
               int unit = (spot >> 1);
               Unit u = null;

               if(unit == 1) //st
               {
                  u = st.createUnit(j,i,owner,owner == 0? playerZero : playerOne, Unit.Type.ST);
               }
               if(unit == 2) //mt
               {
                  u = mt.createUnit(j,i,owner,owner == 0? playerZero : playerOne, Unit.Type.MT);
               }
               if(unit == 3) //rk
               {
                  u = rk.createUnit(j,i,owner,owner == 0? playerZero : playerOne, Unit.Type.RK);
               }
               u.setTile(Map.getMap().getTile(j,i));
            }
         }
      }

   }

   public boolean end()
   {
      return false;
   }

   //drawing the game.
   public void draw(Canvas theCanvas)
   {
      GraphicsContext gc = theCanvas.getGraphicsContext2D();
      gc.setFill(Color.BLACK);
      gc.fillRect(0,0,800,700);

      Map.Iterator it = Map.getMap().generateIteratorAllTiles();

      while(it.hasNext())
      {
         MapTile m = it.next();

         if(m == null)
            continue;

         int x = m.getX();
         int y = m.getY();

         gc.setFill(Color.BLACK);
         gc.fillRect(150+x*50,50+y*50,50,50);
         gc.setFill(m.getColor());
         gc.fillRect(150+x*50+1,50+y*50+1,48,48);



         if(m.getUnit()!= null)
         {
            if(m.getUnit().getOwner()==0)
            {
               gc.setFill(Color.RED);
            }
            else
            {
               gc.setFill(Color.BLUE);
            }
            gc.fillOval(150+x*50+5,50+y*50+5,40,40);

            gc.setFill(m.getUnit().getColor());
            gc.fillOval(150+x*50+8,50+y*50+8,34,34);

            gc.setFill(Color.WHITE);
            String s = ""+m.getUnit().getHP();
            if(s.length() >= 4)
            {
               s = s.substring(0,4);
            }
            gc.fillText(s,150+x*50+10,50+y*50+15);

            gc.setFill(Color.WHITE);
            gc.fillText(""+m.getUnit().getDamage(),150+x*50+10,50+y*50+30);
         }



         /*
         gc.setFill(Color.WHITE);
         if(m.getGoodScore() != -9000) gc.fillText(""+m.getGoodScore(),150+x*50+30,50+y*50+10);
         if(m.getBadScore() != -9000) gc.fillText(""+m.getBadScore(),150+x*50+30,50+y*50+30); */
      }

      gc.setFill(Color.WHITE);
      if(winningSide != -1)
      {

         gc.fillText(""+winningAI.getName()+" Wins!",300,30);
      }

      gc.fillText(""+moves+" moves!",600,30);
   }
}