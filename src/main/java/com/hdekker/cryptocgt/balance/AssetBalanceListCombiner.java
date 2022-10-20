package com.hdekker.cryptocgt.balance;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hdekker.cryptocgt.data.AssetBalance;

@Component
public class AssetBalanceListCombiner {

	public List<AssetBalance> combine(List<AssetBalance> l1, List<AssetBalance> l2){
		
		List<AssetBalance> comb = new ArrayList<>(l1);
		comb.addAll(l2);
		return comb;
		
	}
	
}
