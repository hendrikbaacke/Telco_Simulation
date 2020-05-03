package simulation;

/**
 *	Blueprint for accepting calls
 *	Classes that implement this interface can accept calls
 *	@author Joel Karel
 *	@version %I%, %G%
 */
public interface CallAcceptor
{
	/**
	*	Method to have this object process an event
	*	@param p	The call that is accepted
        *       @return true if accepted
	*/
	public boolean giveCall(Call p);
}
