package org.yarnandtail.andhow.api;

import java.util.*;

/**
 * Configuration and metadata for all known static Properties,
 * 
 * This is the entire domain of Properties defined as static properties
 * (ie the property is defined as a static variable) and on the classpath
 * (or explicitly configured) to be part of AndHow in the current classloader.
 * 
 * This definition does not include the actual values configured for the
 * Properties or Loaders.  This interface is analogous to the list of declared
 * fields.  The values assigned to those fields is analogous to the ValueMap
 * interface.
 * 
 * @author eeverman
 */
public interface StaticPropertyConfiguration {

	public static final List<Property<?>> EMPTY_PROPERTY_LIST = Collections.unmodifiableList(new ArrayList());
	
	/**
	 * Returns all aliases (in and out) for a property.
	 *
	 * This may be different than the aliases requested for the Property, since
	 * the application-level configuration has the ability to add and remove
	 * aliases to mitigate name conflicts.
	 *
	 * @param property The property to fetch naming information for
	 * @return All aliases available for this property.
	 */
	List<EffectiveName> getAliases(Property<?> property);

	/**
	 * Returns the canonical name of a registered property.
	 *
	 * If the property is not registered, null is returned.
	 * @param prop The property to get the canonical name for
	 * @return The canonical name
	 */
	String getCanonicalName(Property<?> prop);

	/**
	 * Finds the Group containing the specified Property.
	 *
	 * @param prop The property to get the PropertyGroup for
	 * @return May return null if the Property is not in any group, or during
	 * construction, if the group has not finished registering all of its properties.
	 */
	GroupProxy getGroupForProperty(Property<?> prop);

	/**
	 * Returns a complete list of all registered properties.
	 *
	 * @return An unmodifiable list of registered properties.
	 */
	List<Property<?>> getProperties();
	
	/**
	 * Returns true if any of the registered groups are user groups.
	 * 
	 * False would indicate that no user groups were found or explicitly registered.
	 * @return 
	 */
	boolean containsUserGroups();

	/**
	 * Returns a list of Properties registered in the passed group.
	 * 
	 * If the group is unregistered or has no properties, an empty list is returned.
	 * 
	 * @param group The group to get Properties for
	 * @return An unmodifiable list of Properties
	 */
	List<Property<?>> getPropertiesForGroup(GroupProxy group);
	
	/**
	 * Returns a list of all registered groups.
	 *
	 * @return An unmodifiable list of Groups
	 */
	List<GroupProxy> getPropertyGroups();

	/**
	 * Finds a registered property by any recognized classpath style name,
	 * including the canonical name or 'in' aliases.
	 * 
	 * Note that this recognizes only classpath style names, not URI style names
	 * such as would be used in JNDI.
	 * 
	 * Some loaders, like the JndiLoader will read both the classpath name, like
	 * <code>my.property</code> and the URI style name, like <code>my/property</code>.
	 * The URI style name is just a means of reading properties in another system - 
	 * for AndHow the URI style name is not considered an actual name.
	 *
	 * @param classpathStyleName A path to a property in the classpath style.
	 * @return The Property or null if it is not found.
	 */
	Property<?> getProperty(String classpathStyleName);
	
	/**
	 * The list of ExportGroups, which handles exporting property values for use
	 * outside the AndHow framework.
	 * 
	 * An ExportGroup bundles an Exporter implementation with a Group
	 * to be exported.  After startup is complete, each Exporter will export its
	 * group as configured.
	 * 
	 * @return An unmodifiable list of export groups.
	 */
	List<ExportGroup> getExportGroups();
	
	/**
	 * Defines how names are created for Properties.
	 * 
	 * @return The NamingStrategy in use.
	 */
	NamingStrategy getNamingStrategy();

}