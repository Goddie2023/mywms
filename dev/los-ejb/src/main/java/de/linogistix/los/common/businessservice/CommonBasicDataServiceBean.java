/*
 * Copyright (c) 2012 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.los.common.businessservice;

import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.mywms.model.Client;

import de.linogistix.los.model.LOSCommonPropertyKey;
import de.linogistix.los.res.BundleResolver;
import de.linogistix.los.util.entityservice.LOSSystemPropertyService;
import de.wms2.mywms.client.ClientBusiness;

/**
 * @author krane
 *
 */
@Stateless
public class CommonBasicDataServiceBean implements CommonBasicDataService {

	private static final Logger log = Logger.getLogger(CommonBasicDataServiceBean.class);

	@Inject
	private ClientBusiness clientService;
	@EJB
	private LOSSystemPropertyService propertyService;

	@PersistenceContext(unitName = "myWMS")
	private EntityManager manager;

	public void createBasicData(Locale locale) {

		log.info("Create Common Basic Data...");

		Client sys = clientService.getSystemClient();

		log.info("Create Properties...");
		propertyService.createSystemProperty(sys, null, LOSCommonPropertyKey.NBCLIENT_SHOW_DETAIL_PROPERTIES, "true",
				LOSCommonPropertyKey.PROPERTY_GROUP_CLIENT,
				resolve("PropertyDescNBCLIENT_SHOW_DETAIL_PROPERTIES", locale), false);
		propertyService.createSystemProperty(sys, null, LOSCommonPropertyKey.NBCLIENT_RESTORE_TABS, "false",
				LOSCommonPropertyKey.PROPERTY_GROUP_CLIENT, resolve("PropertyDescNBCLIENT_RESTORE_TABS", locale),
				false);
		propertyService.createSystemProperty(sys, null, LOSCommonPropertyKey.NBCLIENT_SELECTION_UNLIMITED, "false",
				LOSCommonPropertyKey.PROPERTY_GROUP_CLIENT, resolve("PropertyDescNBCLIENT_SELECTION_UNLIMITED", locale),
				false);
		propertyService.createSystemProperty(sys, null, LOSCommonPropertyKey.NBCLIENT_SELECTION_ON_START, "true",
				LOSCommonPropertyKey.PROPERTY_GROUP_CLIENT, resolve("PropertyDescNBCLIENT_SELECTION_ON_START", locale),
				false);
		propertyService.createSystemProperty(sys, null, LOSCommonPropertyKey.NBCLIENT_VERSION_MATCHER, ".*",
				LOSCommonPropertyKey.PROPERTY_GROUP_CLIENT, resolve("PropertyDescNBCLIENT_VERSION_MATCHER", locale),
				false);

		log.info("Create Common Basic Data. done.");
	}

	private final String resolve(String key, Locale locale) {
		if (key == null) {
			return "";
		}
		if (locale == null) {
			locale = Locale.getDefault();
		}

		ResourceBundle bundle;
		try {
			bundle = ResourceBundle.getBundle("de.linogistix.los.res.Bundle", locale,
					BundleResolver.class.getClassLoader());
			String s = bundle.getString(key);
			return s;
		} catch (MissingResourceException ex) {
			log.error("Exception: " + ex.getMessage());
			return key;
		} catch (IllegalFormatException ife) {
			log.error("Exception: " + ife.getMessage());
			return key;
		}
	}
}
