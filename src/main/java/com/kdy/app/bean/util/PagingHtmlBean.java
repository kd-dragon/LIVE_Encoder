package com.kdy.app.bean.util;

import org.springframework.stereotype.Component;

@Component
public class PagingHtmlBean {
	
	public String Paging(int currentPage, int totalCount, int blockCount) throws Exception {

		StringBuffer pagingHtml;
		int          pageCount = 10;
		// 전체 페이지 수
		int totalPage = (int) Math.ceil((double) totalCount / blockCount);
		if (totalPage == 0) {
			totalPage = 1;
		}

		// 현재 페이지가 전체 페이지 수보다 크면 전체 페이지 수로 설정
		if (currentPage > totalPage) {
			currentPage = totalPage;
		}

		// 현재 페이지의 처음과 마지막 글의 번호 가져오기.
		int startCount = (currentPage - 1) * blockCount;
		int endCount   = startCount + blockCount - 1;

		// 시작 페이지와 마지막 페이지 값 구하기.

		int startPage = (int) ((currentPage - 1) / pageCount) * pageCount + 1;
		int endPage   = startPage + pageCount - 1;

		// int endPage = (int) (Math.ceil(currentPage / (double)blockCount ) *
		// blockCount);
		// int startPage = (endPage - blockCount) + 1;

		// 마지막 페이지가 전체 페이지 수보다 크면 전체 페이지 수로 설정
		if (endPage > totalPage) {
			endPage = totalPage;
		}

		// 이전 block 페이지
		pagingHtml = new StringBuffer();
		pagingHtml.append("<div class=\"col-lg-12 mt-Half\">").append("	<nav aria-label=\"Page navigation\">").append("		<ul class=\"pagination\">");
		if (currentPage > pageCount) {
			pagingHtml.append("<li class=\"page-item\">");
			pagingHtml.append("<a class=\"page-link\" href=\"javascript:goList(1)\">« Prev</a>");
			pagingHtml.append("</li>");
			pagingHtml.append("<li class=\"page-item\">");
			pagingHtml.append("<a class=\"page-link\" href=\"javascript:goList(" + (startPage - 1) + ")\">Previous</a>");
			pagingHtml.append("</li>");
		}

		// 페이지 번호.현재 페이지는 빨간색으로 강조하고 링크를 제거.
		for (int i = startPage; i <= endPage; i++) {
			if (i > totalPage) {
				break;
			}
			if (i == currentPage) {
				pagingHtml.append("<li class=\"page-item active\"><a class=\"page-link\" href=\"javascript:goList(" + i + ")\">" + i + "</a></li>");
			} else {
				pagingHtml.append("<li class=\"page-item\"><a class=\"page-link\" href=\"javascript:goList(" + i + ")\">" + i + "</a></li>");
			}
		}

		// 다음 block 페이지
		if (totalPage - startPage >= pageCount) {
			pagingHtml.append("<li class=\"page-item\">");
			pagingHtml.append("<a class=\"page-link\" href=javascript:goList(" + (endPage + 1) + ")>Next</a>");
			pagingHtml.append("</li>");
			pagingHtml.append("<li class=\"page-item\">");
			pagingHtml.append("<a class=\"page-link\" href=javascript:goList(" + totalPage + ") class='end'>Next »</a>");
			pagingHtml.append("</li>");
		}
		pagingHtml.append("		</ul>").append("	</nav>").append("</div>");

		return pagingHtml.toString();
	}
	
	public String popupPaging(int currentPage, int totalCount, int blockCount) throws Exception {
		
		StringBuffer pagingHtml;
		int          pageCount = 5;
		// 전체 페이지 수
		int totalPage = (int) Math.ceil((double) totalCount / blockCount);
		if (totalPage == 0) {
			totalPage = 1;
		}
		
		// 현재 페이지가 전체 페이지 수보다 크면 전체 페이지 수로 설정
		if (currentPage > totalPage) {
			currentPage = totalPage;
		}
		
		// 현재 페이지의 처음과 마지막 글의 번호 가져오기.
		int startCount = (currentPage - 1) * blockCount;
		int endCount   = startCount + blockCount - 1;
		
		// 시작 페이지와 마지막 페이지 값 구하기.
		
		int startPage = (int) ((currentPage - 1) / pageCount) * pageCount + 1;
		int endPage   = startPage + pageCount - 1;
		
		// int endPage = (int) (Math.ceil(currentPage / (double)blockCount ) *
		// blockCount);
		// int startPage = (endPage - blockCount) + 1;
		
		// 마지막 페이지가 전체 페이지 수보다 크면 전체 페이지 수로 설정
		if (endPage > totalPage) {
			endPage = totalPage;
		}
		
		// 이전 block 페이지
		pagingHtml = new StringBuffer();
		pagingHtml.append("<div class=\"col-lg-12 mt-Half\">").append("	<nav aria-label=\"Page navigation\">").append("		<ul class=\"pagination\">");
		if (currentPage > pageCount) {
			pagingHtml.append("<li class=\"page-item\">");
			pagingHtml.append("<a class=\"page-link\" href=\"javascript:goList(1)\">« Prev</a>");
			pagingHtml.append("</li>");
			pagingHtml.append("<li class=\"page-item\">");
			pagingHtml.append("<a class=\"page-link\" href=\"javascript:goList(" + (startPage - 1) + ")\">Previous</a>");
			pagingHtml.append("</li>");
		}
		
		// 페이지 번호.현재 페이지는 빨간색으로 강조하고 링크를 제거.
		for (int i = startPage; i <= endPage; i++) {
			if (i > totalPage) {
				break;
			}
			if (i == currentPage) {
				pagingHtml.append("<li class=\"page-item active\"><a class=\"page-link\" href=\"javascript:goList(" + i + ")\">" + i + "</a></li>");
			} else {
				pagingHtml.append("<li class=\"page-item\"><a class=\"page-link\" href=\"javascript:goList(" + i + ")\">" + i + "</a></li>");
			}
		}
		
		// 다음 block 페이지
		if (totalPage - startPage >= pageCount) {
			pagingHtml.append("<li class=\"page-item\">");
			pagingHtml.append("<a class=\"page-link\" href=javascript:goList(" + (endPage + 1) + ")>Next</a>");
			pagingHtml.append("</li>");
			pagingHtml.append("<li class=\"page-item\">");
			pagingHtml.append("<a class=\"page-link\" href=javascript:goList(" + totalPage + ") class='end'>Next »</a>");
			pagingHtml.append("</li>");
		}
		pagingHtml.append("		</ul>").append("	</nav>").append("</div>");
		
		return pagingHtml.toString();
	}
}
