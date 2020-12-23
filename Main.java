//this main is for the GUI version of the program
//places to change things for testing purposes:
// Line ~115 in this file:  theGame.Start(new MyBadAI(),new AgressiveAI(), TestHarnass.maps[1]); to change AIs or change what map is being used
// Line ~70 in GameController:   if(counter > 10f && winningSide==-1)  - change the 10f to a smaller value to speed it up. 10f is 1 move per second. 1f is 10 moves per second. .1f is 100 moves per second (capped by one move per frame). The f means float.


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


public class Main extends Application
{
   VBox root = new VBox();
   Stage theStage;
   
   Button start = new Button("Start");
    
   Scene menu;
   Scene game;
   
   private Canvas gameCanvas = new Canvas(800,600);
       
   public void setMenu(Stage stage)
   {
      stage.setScene(menu);
      stage.setTitle("Final");
      stage.show(); 
   }
   public void start(Stage stage)
   {
      //creating the menu & scences
      Label title = new Label("AI");
      title.setFont(Font.font ("Verdana", 34));
      
      Label subtitle = new Label("Here it comes!");
      subtitle.setFont(Font.font ("Verdana", 18));
   
      Label spacer1 = new Label(" ");
      spacer1.setFont(Font.font ("Verdana", 52));

   

      start.setFont(Font.font ("Verdana", 14));      
      start.setPrefSize(150,30);

      root.getChildren().add(title);
      root.getChildren().add(subtitle);
      root.getChildren().add(spacer1);
      root.getChildren().add(start);


      
      root.setAlignment(Pos.TOP_CENTER);


      start.setOnAction(new ButtonListener());  
      theStage = stage;
      menu = new Scene(root,800,600);
      setMenu(stage);
      

      VBox empty = new VBox();            
      empty.getChildren().add(gameCanvas);
      game = new Scene(empty, 800, 600);


      (new AnimationHandler()).start();
      
      gameCanvas.setOnKeyPressed(new KeyListenerDown());
   }
   
  
   
   public static void main(String [] args)
   {
      launch(args);
   }
   
   
   //handler for the start button
   public class ButtonListener implements EventHandler<ActionEvent>
   {
      public void handle(ActionEvent e)      
      {
         if(e.getSource() == start)
         {
            theStage.setScene(game);
            gameCanvas.requestFocus();
            theGame = new GameController();
            theGame.Start(new AgressiveAI(), new OurAI(), TestHarnass.maps[5]);
         }
      }
   }
   
   
   GameController theGame;
   
   public class AnimationHandler extends AnimationTimer
   {
      long lastTime=-1;
      
      //runs the AI each frame
      public void handle(long currentTimeInNanoSeconds) 
      {
         
         if(lastTime != -1)
         {
            long t = (currentTimeInNanoSeconds-lastTime)/1000l;
            double time = t*1.0/100000;
            if(theGame!= null)
            {
               float score = theGame.Update(gameCanvas,time);
               if(score != -1)
               {
                  System.out.println("score: "+score);
               }
            }
         }
         lastTime = currentTimeInNanoSeconds;
         
         if(theGame != null && theGame.end())
         {
              setMenu(theStage);
              theGame = null;        
         }
      }
   }

   //listeners to keep track of whether a key is up or down
   public class KeyListenerDown implements EventHandler<KeyEvent>  
   {
      public void handle(KeyEvent event) 
      {
          if (event.getCode() == KeyCode.Q) 
          {
              theGame = null;
              setMenu(theStage);
          }
      }
   }
}