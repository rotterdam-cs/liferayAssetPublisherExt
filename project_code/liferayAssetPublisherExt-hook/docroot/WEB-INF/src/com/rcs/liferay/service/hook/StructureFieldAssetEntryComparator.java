package com.rcs.liferay.service.hook;

import java.util.Comparator;

import org.apache.log4j.Logger;

import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Node;
import com.liferay.portal.kernel.xml.SAXReaderUtil;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.expando.model.ExpandoBridge;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;

public class StructureFieldAssetEntryComparator implements Comparator {

	private final static Logger log = Logger
			.getLogger(StructureFieldAssetEntryComparator.class);

	private String field;
	private CustomFieldTypeEnum format;

	public StructureFieldAssetEntryComparator(String field,
			CustomFieldTypeEnum format) {
		this.field = field;
		this.format = format;
	}

	public String getField() {
		return field;
	}

	public CustomFieldTypeEnum getFormat() {
		return format;
	}

	public int compare(AssetEntry o1, AssetEntry o2) {
		try {
			JournalArticle ja1 = JournalArticleLocalServiceUtil
					.getLatestArticle(o1.getClassPK());
			JournalArticle ja2 = JournalArticleLocalServiceUtil
					.getLatestArticle(o2.getClassPK());

			String value1 = new String();
			String value2 = new String();

			if (ja1.getStructureId() != null && !ja1.getStructureId().isEmpty()) {
				try {
					Document doc = SAXReaderUtil.read(ja1.getContent());
					if (doc != null) {
						Node node = doc.selectSingleNode("/root/dynamic-element[@name='"+ this.getField()+ "']/dynamic-content");
						if (node != null) {
							value1 = node.getStringValue();
						}
					}
				} catch (Exception e) {
					log.info("error parsing the content xml", e);
				}
			} else {
				log.info("this article doesn't have an structure associated");
			}

			if (ja2.getStructureId() != null && !ja2.getStructureId().isEmpty()) {
				try {
					Document doc = SAXReaderUtil.read(ja2.getContent());
					if (doc != null) {
						Node node = doc.selectSingleNode("/root/dynamic-element[@name='"+ this.getField()+ "']/dynamic-content");
						if (node != null) {
							value2 = node.getStringValue();
						}
					}
				} catch (Exception e) {
					log.info("error parsing the content xml");
				}
			} else {
				log.info("this article doesn't have an structure associated");
			}

			if (!(value1 != null && value2 != null && !value1.trim().isEmpty() && !value2
					.trim().isEmpty())) {
				return -1;
			}

			if (this.getFormat() != CustomFieldTypeEnum.ALPHANUMERIC) {
				value1 = CustomAssetEntryFilter
						.getComparableDateFromFormattedString(value1,
								this.getFormat());
				value2 = CustomAssetEntryFilter
						.getComparableDateFromFormattedString(value2,
								this.getFormat());

				if (value1 == null || value2 == null) {
					return -1;
				}
			}

			return value1.compareToIgnoreCase(value2);
		} catch (Exception e) {
			log.info("Error found comparing structure fields:", e);
			return 0;
		}
	}

	public int compare(Object o1, Object o2) {
		AssetEntry assetEntry1 = (AssetEntry) o1;
		AssetEntry assetEntry2 = (AssetEntry) o2;

		return this.compare(assetEntry1, assetEntry2);
	}

}
