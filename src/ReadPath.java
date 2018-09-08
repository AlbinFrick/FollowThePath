import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// Sample file demonstrating how to read a path file and convert the points
// to objects of the type Point

// Thomas Johansson 2018 thomasj@cs.umu.se

public class ReadPath
{
   public static void main(String[] args) throws Exception
   {
      File pathFile = new File("Path-around-table.json");

      BufferedReader in = new BufferedReader(new InputStreamReader(
            new FileInputStream(pathFile)));

      ReadPath readpathp = new ReadPath(in);
   }

   @SuppressWarnings("unchecked")
   public ReadPath(BufferedReader in) throws JsonParseException, JsonMappingException, IOException
   {
      ObjectMapper mapper = new ObjectMapper();

      // read file and convert to Collection
      Collection <Map<String, Object>> data = 
            (Collection<Map<String, Object>>) mapper.readValue(in, Collection.class);

      int nPoints = data.size();
      Position[] path = new Position[nPoints];

      // Loop through the Collection and extract pose, X, Y
      // make a new Position and put in list
      int index = 0;
      for (Map<String, Object> point : data)
      {
         Map<String, Object> pose = (Map<String, Object>)point.get("Pose");
         Map<String, Object> aPosition = (Map<String, Object>)pose.get("Position");

         double x = (Double)aPosition.get("X");
         double y = (Double)aPosition.get("Y");
         path[index] = new Position(x, y);
         
//         System.out.println("x = " + x + ", y = " + y);
      }
      
      System.out.println("Points in file: " + nPoints);
   }

}
