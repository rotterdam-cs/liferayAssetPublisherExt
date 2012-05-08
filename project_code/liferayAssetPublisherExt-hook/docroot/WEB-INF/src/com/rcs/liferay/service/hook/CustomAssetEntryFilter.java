package com.rcs.liferay.service.hook;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.expando.model.ExpandoBridge;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;

public class CustomAssetEntryFilter {
	private static final Logger log = Logger
			.getLogger(CustomAssetEntryFilter.class);

	// Constants because it handle the format done by other hook
	private static final String TIME_FORMAT = "H:m";
	private static final String DATE_FORMAT = "dd/MM/yyyy";

	private String field;
	private String operator;
	private String value;
	private CustomFieldTypeEnum format;
	private AssetEntry assetEntry;

	public CustomFieldTypeEnum getFormat() {
		return format;
	}

	public void setFormat(CustomFieldTypeEnum format) {
		this.format = format;
	}

	public AssetEntry getAssetEntry() {
		return assetEntry;
	}

	public void setAssetEntry(AssetEntry assetEntry) {
		this.assetEntry = assetEntry;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}



	public boolean validate() {

		String fieldValue = this.getFieldValue();

		if(this.getFormat() != CustomFieldTypeEnum.ALPHANUMERIC){
			
			if(!(this.getValue() != null && fieldValue != null && !this.getValue().trim().isEmpty() && !fieldValue.trim().isEmpty())){
				return false;
			}
			
			fieldValue = CustomAssetEntryFilter.getComparableDateFromFormattedString(fieldValue, this.getFormat());
			
			if("now".equalsIgnoreCase(value)){
				this.setValue(String.valueOf(new Date().getTime()));
			}else{
				this.setValue(CustomAssetEntryFilter.getComparableDateFromFormattedString(this.getValue(), this.getFormat()));
			}
		}else{
			if(this.getValue() == null || fieldValue == null){
				return false;
			}
		}
		
		if ("eq".equalsIgnoreCase(this.getOperator())) {
			return (fieldValue.compareToIgnoreCase(this.getValue()) == 0);
		} else if ("gt".equalsIgnoreCase(this.getOperator())) {
			return (fieldValue.compareToIgnoreCase(this.getValue()) > 0);
		} else if ("ge".equalsIgnoreCase(this.getOperator())) {
			return (fieldValue.compareToIgnoreCase(this.getValue()) >= 0);
		} else if ("lt".equalsIgnoreCase(this.getOperator())) {
			return (fieldValue.compareToIgnoreCase(this.getValue()) < 0);
		} else if ("le".equalsIgnoreCase(this.getOperator())) {
			return (fieldValue.compareToIgnoreCase(this.getValue()) <= 0);
		} else if ("ne".equalsIgnoreCase(this.getOperator())) {
			return (fieldValue.compareToIgnoreCase(this.getValue()) != 0);
		}

		return false;
	}

	private String getFieldValue() {
		try{
			JournalArticle ja = JournalArticleLocalServiceUtil.getLatestArticle(this.getAssetEntry().getClassPK());
			
			ExpandoBridge eb = ja.getExpandoBridge();
			
			if(eb.hasAttribute(this.getField())){
				return (String) eb.getAttribute(this.getField());
			}else if(ja.getStructureId() != null && !ja.getStructureId().isEmpty()) {
				try{
					Document doc = SAXReaderUtil.read(ja.getContent());
					Node node = doc.selectSingleNode("/root/dynamic-element[@name='" + this.getField() + "']/dynamic-content");
					return node.getStringValue();
				}catch (Exception e) {
					log.info("error parsing the content xml");
				}
			}else{
				log.info("this article doesn't have an structure associated");				
			}
		}catch (Exception e) {
			log.error("Error looking for the journal article!", e);
		}
		return new String();
	}

	public static String getComparableDateFromFormattedString(String str,
			CustomFieldTypeEnum fieldType) {

		try {
			DateFormat formatter = new SimpleDateFormat(
					CustomAssetEntryFilter.getFormatFromField(fieldType));
			Date date = formatter.parse(str);
			return String.valueOf(date.getTime());
		} catch (ParseException parseException) {
			log.error("Error Parsing  date or Time", parseException);
			return null;
		}
	}

	
	public static String getFormatFromField(CustomFieldTypeEnum fieldType) {
		String format = new String();

		switch (fieldType) {
		case ALPHANUMERIC:
			// this method only works with dates and/or times
			// No special format is applied to alphanumerics
			return null;
		case DATE:
			format = DATE_FORMAT;
			break;
		case TIME:
			format = TIME_FORMAT;
			break;
		case DATETIME:
			format = DATE_FORMAT + " " + TIME_FORMAT;
			break;
		}

		return format;
	}
	
	public static CustomFieldTypeEnum getFieldTypeByName(String name){
		return CustomFieldTypeEnum.valueOf(name.toUpperCase());
	}
}
