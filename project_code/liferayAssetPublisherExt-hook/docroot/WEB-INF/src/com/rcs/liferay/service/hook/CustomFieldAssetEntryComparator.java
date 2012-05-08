package com.rcs.liferay.service.hook;

import java.util.Comparator;
import java.util.Date;

import org.apache.log4j.Logger;

import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.expando.model.ExpandoBridge;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;

public class CustomFieldAssetEntryComparator implements Comparator {

	private final static Logger log = Logger.getLogger(CustomFieldAssetEntryComparator.class);
	
	private String field;
	private CustomFieldTypeEnum fieldType;
		
	public CustomFieldAssetEntryComparator(String field, CustomFieldTypeEnum fieldType){
		this.field = field;
		this.fieldType = fieldType;
	}
	
	public String getField() {
		return field;
	}

	public CustomFieldTypeEnum getFieldType() {
		return fieldType;
	}
	
	public int compare(AssetEntry o1, AssetEntry o2) {
		try{
			JournalArticle ja1 = JournalArticleLocalServiceUtil.getLatestArticle(o1.getClassPK());
			JournalArticle ja2 = JournalArticleLocalServiceUtil.getLatestArticle(o2.getClassPK());
			
			ExpandoBridge eb1 = ja1.getExpandoBridge();
			ExpandoBridge eb2 = ja2.getExpandoBridge();
			
			String value1 = (String) eb1.getAttribute(this.getField());
			String value2 = (String) eb2.getAttribute(this.getField());
			
			if(!(value1 != null && value2 != null && !value1.trim().isEmpty() && !value2.trim().isEmpty())){
				return -1;
			}
			
			if(this.fieldType != CustomFieldTypeEnum.ALPHANUMERIC){
				value1 = CustomAssetEntryFilter.getComparableDateFromFormattedString(value1, this.getFieldType());
				value2 = CustomAssetEntryFilter.getComparableDateFromFormattedString(value2, this.getFieldType());
				
				if(value1 == null || value2 == null){
					return -1;
				}
			}
			
			return value1.compareToIgnoreCase(value2);
		}catch (Exception e) {
			log.error("Error comparing objects:", e);
			return 0;
		}
	}

	public int compare(Object o1, Object o2) {
		AssetEntry assetEntry1 = (AssetEntry) o1;
		AssetEntry assetEntry2 = (AssetEntry) o2;
		
		return this.compare(assetEntry1, assetEntry2);
	}

	
}
