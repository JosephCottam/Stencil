import com.sun.btrace.annotations.*;
import static com.sun.btrace.BTraceUtils.*;


/**
 * Assuming the btrace jars are one level below stencil and assuming the tracer
 * is with them then this will trace properly:
 * 
 * java -javaagent:../btrace-agent.jar=script=../Tracer.class -jar Stencil.jar -headless -open ./StencilExplore.stencil -sources Entities ../DataSets/count_50000.txt
 * @author jcottam
 *
 */

@BTrace
public class Tracer {
    @OnMethod(clazz="stencil.adapters.java2D.util.MultiThreadPainter", method="doUpdates", location=@Location(Kind.ENTRY))
    public static void onUpdatesEnter() {println(strcat("Updates: Entry: ", str(timeMillis())));}

	@OnMethod(clazz="stencil.adapters.java2D.util.MultiThreadPainter", method="doUpdates", location=@Location(Kind.RETURN))
    public static void onUpdatesLeave() {println(strcat("Updates: Return: ", str(timeMillis())));}

	
	
	
    @OnMethod(clazz="stencil.adapters.java2D.util.MultiThreadPainter", method="render", location=@Location(Kind.ENTRY))
    public static void paintEnter() {println(strcat("Render: Entry: ", str(timeMillis())));}

    @OnMethod(clazz="stencil.adapters.java2D.util.MultiThreadPainter", method="render", location=@Location(Kind.RETURN))
    public static void paintExit() {println(strcat("Render: Return: ", str(timeMillis())));}


    
    
    @OnMethod(clazz="stencil.display.StencilPanel", method="processTuple", location=@Location(Kind.ENTRY))
    public static void loadEnter() {println(strcat("Load: Entry: ", str(timeMillis())));}

    @OnMethod(clazz="stencil.display.StencilPanel", method="processTuple", location=@Location(Kind.RETURN))
    public static void LoadExit() {println(strcat("Load: Return: ", str(timeMillis())));}

    
}