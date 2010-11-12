package com.jklas.sample.petclinic.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jklas.search.index.IndexId;
import com.jklas.search.index.memory.MemoryIndex;

public class SearchSummary extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private PrintWriter writer;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		writer = resp.getWriter();
		
		printHeader();
		printBody();
		printFooter();
	}

	private void printFooter() {
		
	}

	private void printBody() {
		writer.println("<h3>Indexes</h3>");
		
		List<MemoryIndex> allIndexes = MemoryIndex.getAllIndexes();

		if(allIndexes.size()>0) {
			writer.println("<ul>");
			for (MemoryIndex currentIndex : allIndexes) {
				writer.println("<li>");
				
				if(IndexId.getDefaultIndexId().equals(currentIndex.getIndexName()))
					writer.println("(default index) - Size: "+currentIndex.getObjectCount());
				else
					writer.println(currentIndex.getIndexName()+" - Size: "+currentIndex.getObjectCount());
					
				writer.println("</li>");
			}	
			writer.println("</ul>");
		}		
	}

	private void printHeader() {
		writer.println("<h1>ModelSearch Summary</h1>");
		writer.println("<hr>");
	}
		
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req,resp);
	}
	
	
}
