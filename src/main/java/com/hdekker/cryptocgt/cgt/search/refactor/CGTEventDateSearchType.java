package com.hdekker.cryptocgt.cgt.search.refactor;

public enum CGTEventDateSearchType {

	/**
	 *  These assets are applicable to
	 *  CGT discounts of 50%.
	 *  Want to maximise these events.
	 * 
	 */
	ANY_ASSET_PURSHED_MORER_THAN_12_MONTHS_AGO,
	
	/**
	 *  If no events older than 12 months,
	 *  dip into the most recent purchase
	 *  of the asset type so that other purchases
	 *  could potentially reach the 12 month
	 *  marker.
	 * 
	 */
	MOST_RECENT_PURCHASE_OF_ASSET
	
}
