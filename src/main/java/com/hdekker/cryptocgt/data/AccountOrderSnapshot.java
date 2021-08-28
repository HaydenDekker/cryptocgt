package com.hdekker.cryptocgt.data;

import java.time.LocalDateTime;

import com.hdekker.cryptocgt.TransactionType;

/**
 * @author HDekker
 *
 */
public class AccountOrderSnapshot{

	LocalDateTime snapshotDate;
	CoinOrderBalance cob1;
	CoinOrderBalance cob2;
	TransactionType type;

	public TransactionType getType() {
		return type;
	}
	public void setType(TransactionType type) {
		this.type = type;
	}
	public LocalDateTime getSnapshotDate() {
		return snapshotDate;
	}
	public void setSnapshotDate(LocalDateTime snapshotDate) {
		this.snapshotDate = snapshotDate;
	}
	public CoinOrderBalance getCob1() {
		return cob1;
	}
	public void setCob1(CoinOrderBalance cob1) {
		this.cob1 = cob1;
	}
	public CoinOrderBalance getCob2() {
		return cob2;
	}
	public void setCob2(CoinOrderBalance cob2) {
		this.cob2 = cob2;
	}
	
	
	
}
