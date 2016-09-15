/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author arthur
 */
public abstract class TranslatorLogger
{
    public static final Logger LOGGER = Logger.getLogger(TranslatorLogger.class.getName());
    
    public static void log(String message, Level level)
    {
        log(level, message);
    }
    
    public static void log(Level level, String message)
    {
        LOGGER.log(level, message);
    }
}
