/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.rcs.liferay.service.hook;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.asset.AssetRendererFactoryRegistryUtil;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.model.AssetRenderer;
import com.liferay.portlet.asset.model.AssetRendererFactory;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;
import com.liferay.portlet.asset.service.AssetEntryService;
import com.liferay.portlet.asset.service.AssetEntryServiceWrapper;
import com.liferay.portlet.asset.service.persistence.AssetEntryQuery;
import com.liferay.portlet.dynamicdatamapping.model.DDMStructure;
import com.liferay.portlet.dynamicdatamapping.service.DDMContentLocalServiceUtil;
import com.liferay.portlet.dynamicdatamapping.service.DDMStructureLocalServiceUtil;
import com.liferay.portlet.expando.model.ExpandoBridge;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalArticleResource;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portlet.journal.service.JournalArticleResourceLocalServiceUtil;


import org.apache.log4j.Logger;

/**
 * @author Brian Wing Shun Chan
 * @author Jorge Ferrer
 * @author Bruno Farache
 * @author Raymond Augé
 */
public class CustomAssetEntryServiceImpl extends AssetEntryServiceWrapper {

	public CustomAssetEntryServiceImpl(AssetEntryService assetEntryService) {
		super(assetEntryService);
	}

	private static final Logger log = Logger
			.getLogger(CustomAssetEntryServiceImpl.class);
	
	@Override
	public List<AssetEntry> getEntries(AssetEntryQuery entryQuery)
			throws PortalException, SystemException {
		List<AssetEntry> entriesList = new ArrayList<AssetEntry>();
		CustomAssetEntryQuery caeq = new CustomAssetEntryQuery(entryQuery);
		BeanPropertiesUtil.copyProperties(entryQuery, caeq);
		
		int end = entryQuery.getEnd();
		int start = entryQuery.getStart();
		
		if(caeq.isCustomQuery()){
			entryQuery.setStart(0);
			entryQuery.setEnd(QueryUtil.ALL_POS);
		}
		
		List<AssetEntry> entries = super.getEntries(entryQuery);
		
		if(caeq.isCustomQuery()){
			log.info("It is a custom query..");
			
			for (AssetEntry ae : entries) {
				entriesList.add(ae);
			}
			///////////
			// Start filtering
			///////////

			List<AssetEntry> toRemove = new ArrayList<AssetEntry>();
			
			for (AssetEntry assetEntry : entriesList) {

				if(caeq.getCustomFilterFieldName1() != null && !caeq.getCustomFilterFieldName1().trim().isEmpty()){

					CustomAssetEntryFilter assetFilter = new CustomAssetEntryFilter();
					assetFilter.setField(caeq.getCustomFilterFieldName1());
					assetFilter.setFormat(CustomAssetEntryFilter.getFieldTypeByName(caeq.getCustomFilterFieldType1()));
					assetFilter.setOperator(caeq.getCustomFilterComparator1());
					assetFilter.setValue(caeq.getCustomFilterValue1());
					assetFilter.setAssetEntry(assetEntry);
					
					if(!assetFilter.validate()){
						toRemove.add(assetEntry);
						continue;
					}
				}
				
				if(caeq.getCustomFilterFieldName2() != null && !caeq.getCustomFilterFieldName2().trim().isEmpty()){
					CustomAssetEntryFilter assetFilter = new CustomAssetEntryFilter();
					assetFilter.setField(caeq.getCustomFilterFieldName2());
					assetFilter.setFormat(CustomAssetEntryFilter.getFieldTypeByName(caeq.getCustomFilterFieldType2()));
					assetFilter.setOperator(caeq.getCustomFilterComparator2());
					assetFilter.setValue(caeq.getCustomFilterValue2());
					assetFilter.setAssetEntry(assetEntry);
					
					if(!assetFilter.validate()){
						toRemove.add(assetEntry);
					}
				}
			}
			
			entriesList.removeAll(toRemove);

			// Will check the first element to know if we need to sort by custom field or by structure value
			if(entriesList != null && entriesList.size() > 0 ){
				///////////
				// Start sorting
				///////////
				
				AssetEntry assetEntry = entriesList.get(0);
				long classPK = assetEntry.getClassPK();
				
				// get the 
				JournalArticle ja = JournalArticleLocalServiceUtil.getLatestArticle(classPK);
				
				// check for custom field
				ExpandoBridge eb = ja.getExpandoBridge();
				
				if(eb.hasAttribute(caeq.getCustomFieldOrder())){
					// Sort by custom field
					CustomFieldAssetEntryComparator comparator = new CustomFieldAssetEntryComparator(caeq.getCustomFieldOrder(), CustomAssetEntryFilter.getFieldTypeByName(caeq.getCustomFieldType()));
					
					// sorting asc
					Collections.sort(entriesList, comparator);
				}else{
					// Sort by structure
					StructureFieldAssetEntryComparator comparator = new StructureFieldAssetEntryComparator(caeq.getCustomFieldOrder(), CustomAssetEntryFilter.getFieldTypeByName(caeq.getCustomFieldType()));
					
					//sorting asc
					Collections.sort(entriesList, comparator);

				}

				if("DESC".equalsIgnoreCase(caeq.getCustomFieldOrderType())){
					Collections.reverse(entriesList);
				}
				return entriesList.subList(start, (entriesList.size() < end)? entriesList.size() : end);
				
			}
		}else{
			log.info("It isn't a custom query..");
			return entries;
		}
		
		return entriesList;
	}
	
	@Override
	public int getEntriesCount(AssetEntryQuery entryQuery)
			throws PortalException, SystemException {

		return super.getEntriesCount(entryQuery);
	}

}