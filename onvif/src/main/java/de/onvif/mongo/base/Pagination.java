package de.onvif.mongo.base;

import java.util.List;

//import com.fasterxml.jackson.databind.annotation.JsonSerialize;
//
//@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Pagination<T> {

	private int pageSize;
	private int pageNum;
	private long totalAmount;

	private String orderBy;
	private String ordering;

	/**
	 * 下一页第一个元素排序方式的索引
	 * 1. users/search : next指的是subscribeTime
	 * 2. interactMessageHistories : next指的是createTime
	 * 3. interactMessageHistories/user/{userId} : next指的是createTime
	 * */
	private String next;
	private List<T> results;

	public Pagination() {
	}

	public Pagination(int pageNum, int pageSize) {
		this.pageNum = pageNum;
		this.pageSize = pageSize;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public long getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(long totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getOrdering() {
		return ordering;
	}

	public void setOrdering(String ordering) {
		this.ordering = ordering;
	}

	public int offset() {
		return (pageNum - 1) * pageSize;
	}

	public List<T> getResults() {
		return results;
	}

	public void setResults(List<T> results) {
		this.results = results;
	}

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}
}
