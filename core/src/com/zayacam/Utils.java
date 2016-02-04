package com.zayacam;

import java.awt.*;

/**
 * Created by allworld on 1/8/2016.
 */
public class Utils {
    public static boolean isNumeric(String arg){
        boolean result = true;
        try
        {
            double d = Double.parseDouble(arg);
        }
        catch(Exception ex)
        {
            result = false;
        }
        return result;
    }

    public static void Beep(int repeats) throws InterruptedException {
        for (int i = 0; i < repeats; i++) {
            Toolkit.getDefaultToolkit().beep();
            Thread.sleep(100);
            if (repeats > 3)
                break;
        }
    }

    public static void Beep() throws InterruptedException {
        Beep(1);
    }


    public static void ClearScreen() {
        int numRowsInConsole = 10;
        for (int ii = 0; ii < numRowsInConsole; ii++) {
            // scroll down one line
            System.out.println();
        }
    }

    public static String GetStageName(String bidWhistStage) {
        String result = "";
        result = bidWhistStage.substring(
                bidWhistStage.lastIndexOf(".") + 1, bidWhistStage.lastIndexOf("@")
        );
        return result.trim();
    }
}
