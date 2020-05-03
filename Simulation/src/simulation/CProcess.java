package simulation;

/**
 *	Blueprint for processes
 *	Classes that implement this interface can process events
 *	@author Joel Karel
 *	@version %I%, %G%
 */
public interface CProcess
{
	/**
	*	Method to have this object process an event
	*	@param type The type of the event that has to be executed
	*	@param tme	The current time
	*/
	public void execute(int type, double tme);
}
