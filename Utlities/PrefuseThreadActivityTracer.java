import com.sun.btrace.annotations.*;
import static com.sun.btrace.BTraceUtils.*;


/**
 * Assuming the btrace jars are one level below the the application jar, prefuse jar and btrace script...
 * 
 * java -javaagent:../btrace-agent.jar=script=PrefuseThreadActivityTracer.class -jar PersistentTests.jar -PREFUSE -ITERATIONS 1  -DATA ./squarified_10K.txt
 * @author jcottam
 *
 */

@BTrace
public class PrefuseThreadActivityTracer {
	//Hooks for data loading
	@OnMethod(clazz="persistentTests.prefuse.SquareShells", method="add", location=@Location(value=Kind.SYNC_ENTRY, where=Where.AFTER))
    public static void loadEnter() {println(strcat("Load: Entry: ", str(timeMillis())));}

    @OnMethod(clazz="persistentTests.prefuse.SquareShells", method="add", location=@Location(Kind.RETURN))
    public static void LoadExit() {println(strcat("Load: Return: ", str(timeMillis())));}

	
    //Hooks for pre-render activities (actions that recalculate based on global values
	@OnMethod(clazz="prefuse.action.ActionList", method="run", location=@Location(Kind.ENTRY))
    public static void onUpdatesEnter() {println(strcat("Updates: Entry: ", str(timeMillis())));}

	@OnMethod(clazz="prefuse.action.ActionList", method="run", location=@Location(Kind.RETURN))
    public static void onUpdatesLeave() {println(strcat("Updates: Return: ", str(timeMillis())));}
	
	
	
	//Hooks for pre-rendering
	private static String ignore;
    @OnMethod(clazz="prefuse.Display", method="paintDisplay", location=@Location(value=Kind.SYNC_ENTRY, where=Where.AFTER))
        public static void onSyncEntry(Object obj) {
        	String id = identityStr(obj);
        	if (ignore == null || strcmp(id, ignore) == 0) {ignore = id; return;}	//locking of m_vis needs to be ignored
            println(strcat("Render: Entry: ", str(timeMillis())));
        }
	
    @OnMethod(clazz="prefuse.Display", method="paintDisplay", location=@Location(Kind.RETURN))
    public static void paintExit() {
    	println(strcat("Render: Return: ", str(timeMillis())));
    }
}