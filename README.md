AndHow
======

Simple typed and validated configuration loading for web apps, command line or 
any environment application.

Goals
--------------
*	Strong Typing.  Each Configuration Point *only* returns its appropriate type.
	String Config Points have a getValue() method that returns a String.
	Integer Points have a getValue() method that returns an Int.
*	Flexible and early validation.
*	Expressions.
*	No 'magic strings' used to access configuration params.  In fact, the application
	or library does not know or care what the parameter name is.
	Instead, `ConfigPoints` are completely defined as a type of
	**static final ConfigPoint MY_CONFIG_POINT = ...** and accessing its configured value is simply:
	**MY_CONFIG_POINT.getValue();**
	which is also used directly to access its value.
*	Fail-Fast for required configuration points.  Config Points can be marked
	required.  If a required point is not provided, AndHow will report the issue
	the application will report the issue at startup (and stop), rather than
	waiting until the bit of code using that parameter discovers its missing.
*	Support for library/module usage.  A module can declare its configuration
	points and mark them as required, again, catching these missing parameters
	at startup.
	

Design Choices
--------------
*	Use statics over Enums.  Enums are a natural fit for groups of static final
	constants, however, Enums do not allow generic types at the individual level.
	That is, all enums of an Enum type have the same class signature, and thus, 
	cannot be made to return different types from their getValue() method.

ToDo
----
*	Need to add blocking on getting App instances that are not ready.
*	LoaderErrors need to be handled in the same way as the other errors in AppConfig.
*	There needs to be a way to use AH for ServletConfig in an isolated scope.
	These will need to be initiated out of sequence from the main AppConfig.
	They may have no relation to the AppConfig.  Perhaps a separate instance-
	based AppConfig (one that you create and hold a reference too, instead of
	a static single instance one).
*	Direct individual parms to be written to SytemProps (or other).  This would
	expose writable system properties to the system.  For instance, if there
	is a system level connection timeout that reads a system property to find
	a default value, creating a ConfigPoint and marking is as 'write to sys props'
	with a specific name would provide a way to config that thru AH.
*	Add a CONFIG_POINT.default(value) that creates an argument for the AppConfig
	that sets a default at creation time.
*	Create a JNDI loader.  This will expose some issues w/ dealing w/ configed
	values that are non-string.  For instance, a JNDI param may already be an
	Integer, instead of needing to be converted from a string.
*	The AppConfig should initial creating should have a key that is returned
	to allow resets for testing.  That 'key' should be an inner class that has
	access to calling reload.
*	All loaders should complain bitterly if there is an unmatched value.
	Since some apps must deal w/ legacy params, there should be a ConfigPointGroup
	subclass _FixedNameConfigPointGroup_ that uses only the default name or
	explicit name w/o any package prefix.
*	Explicitly handle the case of ConfigPointGroups extending other ConfigPointGroups.
	This might be allows to build up and easily register nested groups of params,
	but if two Groups extend the same base group, the 2nd child will bomb during
	registration b/c the super points would attempt to register twice under different
	names.  Super group w/ multiple child groups == bad / not allowed.
	One place that might aggregate  (many super groups into a single subclass
	Group) would be a module that has several logical groups.  When exposing
	those groups to a larger app, it would be nice to roll those all into a single
	Uber-Group so the parent app only has one group to register for that module.
	In that case, the registration should register names based on the most
	super-classy Group.
*	Redirect AppConfig errors during tests to some place other than out.
Ideas
-----
*	Make AppConfig just an interface w/ static methods and default methods.
	Actual implementation should be AppConfigCore, which would have no instancing
	rules/restrictions.
	This would separate construction/reset complexity from the actual AppConfig logic,
	allow easier testing of just the AppConfigCore, and make it easier to create
	new instances for subtrees or isolated trees (like servlet config).
*	ConfigPoints should validate their default value against any constraints
	at construction time to ensure bad defaults cannot be spec'ed.
*	If dynamic properties are supported, then there needs to be a way to do
	consistent reads.  Two ways to handle this:
	*	Get a read-key that would be associated w/ Value map.  Then have some way
		to use that read-key to read values, which would throw an exception if
		the read-key is invalid (i.e., the value map has been reassigned).
	*	Create a transaction object that would contain only the value map.
		All reads could be done against that transaction object.
		The trans object would share a ref to all of the values, so it would
		hold the values in memory if it did not close (typical issue w/ resources).
	Both of those options involve isolating the value map.
*	Nulls might be explicitly set, possibly w/ EL.  In that case, the LoaderState
	(and ConfigPoints) will need to track if a value is explicitly set in some
	way other than figuring it out from a null value.
