/**
 * 
 */
package turtlekit.viewer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * GenericViewer is an annotation tagging a viewer agent 
 * which is independent from a particular
 * model so that it will be included in the viewers menu
 * 
 * @author Fabien Michel
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GenericViewer {

}
