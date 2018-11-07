package com.haier.fintech.modules.report.utils;

import java.util.List;

import lombok.Data;
@Data
public class MongoPageUtils<T> {

    //总页数
    private Long totalPage;
    
    
    //每页记录数
  	private int pageSize;
  	
    //当前页数
  	private int currPage;

    //总记录数
    private Long totalCount;

    //每页显示集合
    private List<T> rows;
    
    public MongoPageUtils(List<T> rows, long totalCount, int currPage,int pageSize) {
		this.rows = rows;
		this.totalCount = totalCount;
		this.pageSize = pageSize;
		this.currPage = currPage;
		this.totalPage = (long)Math.ceil((double)totalCount/pageSize);
	}
   
}