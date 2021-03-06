import java.io.*;
import java.util.Collection;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A Class that reads a path file and convert the points
 * to objects of the type Point.
 */

public class ReadPath
{
   private Position[] path;
   private int nPoints;

   @SuppressWarnings("unchecked")
   public ReadPath(String fileName) throws IOException
   {
      File pathFile = new File(fileName);

      BufferedReader in = new BufferedReader(new InputStreamReader(
              new FileInputStream(pathFile)));

      ObjectMapper mapper = new ObjectMapper();

      // read file and convert to Collection
      Collection <Map<String, Object>> data = 
            (Collection<Map<String, Object>>) mapper.readValue(in, Collection.class);

      nPoints = data.size();
      path = new Position[nPoints];

      // Loop through the Collection and extract pose, X, Y
      // make a new Position and put in list
      int index = 0;
      for (Map<String, Object> point : data)
      {
         Map<String, Object> pose = (Map<String, Object>)point.get("Pose");
         Map<String, Object> aPosition = (Map<String, Object>)pose.get("Position");

         double x = (Double)aPosition.get("Y");
         double z = (Double)aPosition.get("X");
         path[index] = new Position(z, x);
         index++;
         //made to easier pinpoint the position in the simulation.
         //System.out.println("x = " + x + ", z = " + (z+5));
         //System.out.println(index);
      }
      
      System.out.println("Points in file: " + nPoints);

   }

   /**
    * Method: GetPath
    * Description: Returns the paths as a position array.
    * @return - Position[]
    */
   public Position[] GetPath(){
      return path;
   }
}
