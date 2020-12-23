//simple class to keep track of a move. No functionality other than accesors / mutators.

public class Move
{
   private Unit toMove;
   private Vector2 newPosition;
   private boolean attacks=false;
   private Unit toAttack;
   
   public Move(Unit toMove, Vector2 newPosition, boolean attacks, Unit toAttack)
   {
      this.toMove = toMove;
      this.newPosition = newPosition;
      this.attacks = attacks;
      this.toAttack = toAttack;
   }
   
   public Unit getToMove()
   {
      return toMove;
   }
   
   public Vector2 getNewPosition()
   {
      return newPosition;
   }
   
   public boolean getAttacks()
   {
      return attacks;
   }
      
   public Unit getToAttack()
   {
      return toAttack;
   }
   
   public String toString()
   {
      return toMove+" "+newPosition+" "+attacks+" "+toAttack;
   }
}