package com.dooioo.se.lorik.spi.view;

import java.io.Serializable;
import java.util.List;

/**
 *  分页对象
 * @author Jail Hu
 */
public class Pagination<T> implements Serializable {
  private static final long serialVersionUID = 1L;
  /**
	 * 实体对象列表
	 */
	private List<T> pageList;
	/**
	 * 每页记录数
	 */
	private int pageSize;
	/**
	 * 当前页号,pageNo从1开始
	 */
	private int pageNo;
	/**
	 * 总记录数
	 */
	private int totalCount = 0;

	public Pagination() {
		super();
	}

	public Pagination(int pageNo, int pageSize) {
		if (pageNo <= 0) {
			pageNo = 1;
		}
		this.pageNo = pageNo;
		this.pageSize = pageSize;
	}

	public List<T> getPageList() {
		return pageList;
	}

	public void setPageList(List<T> pageList) {
		this.pageList = pageList;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * 总页数
	 */
	public int getTotalPage() {
		if (getTotalCount() == 0) {
			return 1;
		} else {
			if (getTotalCount() % getPageSize() == 0) {
				return getTotalCount() / getPageSize();
			} else {
				return getTotalCount() / getPageSize() + 1;
			}
		}
	}

	/**
	 * 返回下一页页数
	 * 
	 * @return
	 */
	public int getNextPage() {
		if (isLastPage()) {
			return getTotalPage();
		} else {
			return getPageNo() + 1;
		}
	}

	/**
	 * 上一页页数
	 */
	public int getPrePage() {
		if (isFirstPage()) {
			return 1;
		} else {
			return getPageNo() - 1;
		}
	}

	/**
	 * 是否是最后一页
	 */
	public boolean isLastPage() {
		if (getTotalPage() <= 0) {
			return true;
		} else {
			return getPageNo() >= getTotalPage();
		}
	}

	/**
	 * 是否是第一页
	 */
	public boolean isFirstPage() {
		return getPageNo() <= 1;
	}
}
