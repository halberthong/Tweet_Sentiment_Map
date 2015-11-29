public class Common {
	/*
	 * Define common functionalities
	 */
	public static void writeLog(Object obj, Exception e) {
    	if (obj == null) {
    		System.out.println("null");
    	} else {
    		System.out.println(obj.toString());
    		e.printStackTrace();
    	}
    }
	
	public static void print(Object obj) {
    	if (obj == null) {
    		System.out.println("null");
    	} else {
    		System.out.println(obj.toString());
    	}
    }
}