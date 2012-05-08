package com.rcs.liferay.service.hook;

import com.liferay.portlet.asset.service.persistence.AssetEntryQuery;

public class CustomAssetEntryQuery extends AssetEntryQuery{

	public CustomAssetEntryQuery(AssetEntryQuery aeq) {
		super(aeq);
	}
	
	private String customFieldOrder;
	private String customFieldOrderType;
	private String customFieldType;
	private String customFilterFieldName1;
	private String customFilterComparator1;
	private String customFilterValue1;
	private String customFilterFieldType1;
	private String customFilterFieldName2;
	private String customFilterComparator2;
	private String customFilterValue2;
	private String customFilterFieldType2;
	
	public String getCustomFieldType() {
		return customFieldType;
	}

	public void setCustomFieldType(String customFieldType) {
		this.customFieldType = customFieldType;
	}

	public String getCustomFilterFieldName1() {
		return customFilterFieldName1;
	}

	public void setCustomFilterFieldName1(String customFilterFieldName1) {
		this.customFilterFieldName1 = customFilterFieldName1;
	}

	public String getCustomFilterComparator1() {
		return customFilterComparator1;
	}

	public void setCustomFilterComparator1(String customFilterComparator1) {
		this.customFilterComparator1 = customFilterComparator1;
	}

	public String getCustomFilterValue1() {
		return customFilterValue1;
	}

	public void setCustomFilterValue1(String customFilterValue1) {
		this.customFilterValue1 = customFilterValue1;
	}

	public String getCustomFilterFieldType1() {
		return customFilterFieldType1;
	}

	public void setCustomFilterFieldType1(String customFilterFieldType1) {
		this.customFilterFieldType1 = customFilterFieldType1;
	}

	public String getCustomFilterFieldName2() {
		return customFilterFieldName2;
	}

	public void setCustomFilterFieldName2(String customFilterFieldName2) {
		this.customFilterFieldName2 = customFilterFieldName2;
	}

	public String getCustomFilterComparator2() {
		return customFilterComparator2;
	}

	public void setCustomFilterComparator2(String customFilterComparator2) {
		this.customFilterComparator2 = customFilterComparator2;
	}

	public String getCustomFilterValue2() {
		return customFilterValue2;
	}

	public void setCustomFilterValue2(String customFilterValue2) {
		this.customFilterValue2 = customFilterValue2;
	}

	public String getCustomFilterFieldType2() {
		return customFilterFieldType2;
	}

	public void setCustomFilterFieldType2(String customFilterFieldType2) {
		this.customFilterFieldType2 = customFilterFieldType2;
	}

	public String getCustomFieldOrder() {
		return customFieldOrder;
	}
	
	public void setCustomFieldOrder(String customFieldOrder) {
		this.customFieldOrder = customFieldOrder;
	}
	
	public String getCustomFieldOrderType() {
		return customFieldOrderType;
	}
	
	public void setCustomFieldOrderType(String customFieldOrderType) {
		this.customFieldOrderType = customFieldOrderType;
	}
	
	public boolean isCustomQuery() {
		return (((customFieldOrder!=null) && (!customFieldOrder.trim().isEmpty()))) || 
				(((customFilterFieldName1!=null) && (!customFilterFieldName1.trim().isEmpty()))) ||
				(((customFilterFieldName2!=null) && (!customFilterFieldName2.trim().isEmpty())));
	}
}
