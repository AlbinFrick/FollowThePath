
/**
 * Class: main
 * Description: This is class reads a path greats a FollowPathRobot3
 * and then runs the robot.
 *
 *  * @author AlbinF 20/9-18
 *  * @author JonathanH 20/9-18
 */

import java.io.IOException;

import static java.lang.System.exit;

public class main {

    public static void main(String[] args)  {
        try{
            System.out.println("Reading path");
            ReadPath readPath = new ReadPath(args[0]);
            Position[] path = readPath.GetPath();
            System.out.println("Creating Robot");
            FollowPathRobot3 robot = new FollowPathRobot3("http://127.0.0.1", 50000);
            robot.Run(path);


        }catch (IOException e){
            System.out.println("Control the following:");
            System.out.println("Path-file");
            System.out.println("Simulator");
            System.out.println("Program arguments");
            exit(-1);
        }catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Control the argument of the program");
            exit(-1);
        }catch (Exception e){
            e.printStackTrace();
            exit(-1);
        }
    }
}
