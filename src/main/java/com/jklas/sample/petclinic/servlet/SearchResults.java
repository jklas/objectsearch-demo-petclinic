package com.jklas.sample.petclinic.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jklas.sample.petclinic.Owner;
import com.jklas.sample.petclinic.Pet;
import com.jklas.search.engine.VectorSearch;
import com.jklas.search.engine.dto.VectorRankedResult;
import com.jklas.search.engine.score.VectorRanker;
import com.jklas.search.index.memory.MemoryIndexReaderFactory;
import com.jklas.search.query.vectorial.VectorQuery;
import com.jklas.search.query.vectorial.VectorQueryParser;

public class SearchResults extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private PrintWriter writer;
	private SessionFactory sessionFactory;


	@Override
	public void init() throws ServletException {
		sessionFactory = (SessionFactory)new ClassPathXmlApplicationContext("applicationContextTesting-hibernate.xml").getBean("sessionFactory");		
		super.init();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		writer = resp.getWriter();

		VectorQuery query = new VectorQueryParser(req.getParameter("q")).getQuery();

		long init = System.currentTimeMillis();
		VectorSearch vectorSearch = new VectorSearch(query, MemoryIndexReaderFactory.getInstance());
		long totalTime = System.currentTimeMillis() - init;

		List<VectorRankedResult> results = vectorSearch.search(new VectorRanker());

		printHeader();
		printBody(query, results, totalTime);
		printFooter();
	}

	private void printFooter() {

	}

	private void printBody(VectorQuery query, List<VectorRankedResult> results, long totalTime) {
		writer.print("<h3>Results (");		
		writer.print((query.getPage()-1)*query.getPageSize()+1);
		writer.print(" to ");
		writer.print(query.getPage()*query.getPageSize());
		writer.print(") - ");		
		writer.println(((float)totalTime)/1000 + " s. </h3>");

		writer.println("<ul>");

		Session session = sessionFactory.openSession();
		try {
			for (VectorRankedResult result : results) {
				printCurrentResult(session, result);
			}
		} finally {
			if(session!=null) session.close();
		}

		writer.println("</ul>");

	}

	private void printCurrentResult(Session session, VectorRankedResult result) {		

		Class<?> resultClass = result.getKey().getClazz();
		Serializable resultId = result.getKey().getId();

		if(Owner.class.equals(resultClass)) {
			Owner owner = (Owner)session.load(resultClass, resultId);
			writer.println("<li>"+owner.getFirstName()+" "+owner.getLastName()+"</li>");
			writer.println(" <ul> ");
			writer.println( "<li>" + owner.getAddress() + "</li>");
			writer.println( "<li>" + owner.getCity() + "</li>");
			
			List<Pet> pets = (List<Pet>)owner.getPets();
			if(pets!= null && pets.size() > 0) {
				writer.println(" <ul>Pets: ");
				for (Pet pet : pets) {
					writer.println( "<li>" + pet.getName() + "</li>");
				}
				writer.println(" </ul>");
			}			
			writer.println("<li>Score: "+result.getScore()+"</li>");
			writer.println(" </ul> ");
		} else {
			writer.println("<li>Class: "+resultClass + " - ID: "+resultId + "</li>");
		}

	}

	private void printHeader() {
		writer.println("<h1>ModelSearch Results</h1>");
		writer.println("<hr>");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req,resp);
	}	
}
