package com.hdekker.cryptocgt.data;

import java.time.LocalDateTime;

import com.hdekker.cryptocgt.data.transaction.TransactionType;

/**
 * @author HDekker
 *
 */
public class AccountOrderSnapshot{

	final LocalDateTime snapshotDate;
	final CoinOrderBalance cob1;
	final CoinOrderBalance cob2;
	final TransactionType type;

	public AccountOrderSnapshot(LocalDateTime snapshotDate, CoinOrderBalance cob1, CoinOrderBalance cob2,
			TransactionType type) {
		super();
		this.snapshotDate = snapshotDate;
		this.cob1 = cob1;
		this.cob2 = cob2;
		this.type = type;
	}
	public TransactionType getType() {
		return type;
	}
	public LocalDateTime getSnapshotDate() {
		return snapshotDate;
	}
	public CoinOrderBalance getCob1() {
		return cob1;
	}
	public CoinOrderBalance getCob2() {
		return cob2;
	}
	
}
