/* 
Copyright 2019-2021 Matthias Krane

This file is part of the Warehouse Management System mywms

mywms is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.
 
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/
package de.wms2.mywms.project.module;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import de.linogistix.los.common.businessservice.CommonBasicDataService;
import de.linogistix.los.inventory.businessservice.InventoryBasicDataService;
import de.wms2.mywms.exception.BusinessException;
import de.wms2.mywms.module.ModuleSetup;
import de.wms2.mywms.property.SystemPropertyBusiness;
import de.wms2.mywms.util.Wms2Properties;

/**
 * Setup of the wms2 base module
 * 
 * @author krane
 *
 */
@Singleton
@DependsOn("Wms2SetupService")
@Startup
public class ProjectSetupService extends ModuleSetup {
	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Inject
	private SystemPropertyBusiness propertyBusiness;
	@EJB
	private InventoryBasicDataService inventoryBasicDataService;
	@EJB
	private CommonBasicDataService commonBasicDataService;

	@Override
	public String getModulePackage() {
		return "de.linogistix.los.reference";
	}

	@PostConstruct
	public void checkSetup() {
		String value = propertyBusiness.getString(getModulePropertyName(), null);
		if (!StringUtils.isBlank(value)) {
			return;
		}

		try {
			setup(SetupLevel.UNINITIALIZED, null);
		} catch (Throwable t) {
			logger.log(Level.SEVERE, "Setup failed", t);
			return;
		}
	}

	@Override
	public void setup(SetupLevel level, Locale locale) throws BusinessException {
		String value = propertyBusiness.getString(getModulePropertyName(), null);
		if (StringUtils.equals(value, level.name())) {
			return;
		}

		switch (level) {
		case UNINITIALIZED:
		case INITIALIZED:
		case DEMO_SMALL:
		case DEMO_MEDIUM:
		case DEMO_LARGE:
			commonBasicDataService.createBasicData(locale);
			inventoryBasicDataService.createBasicData(locale);

			createProperty(null, getModulePropertyName(), level.name(), Wms2Properties.GROUP_SETUP,
					Wms2Properties.GROUP_SETUP, locale);
			break;
		default:
			logger.log(Level.INFO, "Skip setup level=" + level);
			break;
		}
	}

}
