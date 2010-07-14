package com.jklas.sample.petclinic.servlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.jklas.search.engine.VectorSearch;
import com.jklas.search.engine.dto.VectorRankedResult;
import com.jklas.search.engine.score.VectorRanker;
import com.jklas.search.index.IndexId;
import com.jklas.search.index.memory.MemoryIndex;
import com.jklas.search.index.memory.MemoryIndexReaderFactory;
import com.jklas.search.query.vectorial.VectorQuery;
import com.jklas.search.query.vectorial.VectorQueryParser;

public class SearchController extends MultiActionController implements InitializingBean {

	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
    // handlers
    /**
     * 
     * Custom handler for welcome
     * 
     * @param request
     *            current HTTP request
     * @param response
     *            current HTTP response
     * @return a ModelAndView to render the response
     */

    public ModelAndView searchStatsHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	List<MemoryIndex> allIndexes = MemoryIndex.getAllIndexes();

    	Map<String, Integer> model = new HashMap<String, Integer>();
    	
		if(allIndexes.size()>0) {
			for (MemoryIndex currentIndex : allIndexes) {
				if(IndexId.getDefaultIndexId().equals(currentIndex.getIndexName()))
					model.put("default", currentIndex.getObjectCount());					
				else
					model.put(currentIndex.getIndexName().toString(), currentIndex.getObjectCount());
			}	
		}		
        return new ModelAndView("searchStatsView","indexStats",model);
    }

    public ModelAndView searchHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	VectorQuery query = new VectorQueryParser(request.getParameter("q")).getQuery();

		long init = System.currentTimeMillis();
		VectorSearch vectorSearch = new VectorSearch(query, MemoryIndexReaderFactory.getInstance());
		long totalTime = System.currentTimeMillis() - init;

		List<VectorRankedResult> results = vectorSearch.search(new VectorRanker());

		Map<String, Object> model = new HashMap<String,Object>();
		
		if(results.size()==0) {
			model.put("results.start", 0);		
			model.put("results.end", 0);
		} else {
			model.put("results.start", (query.getPage()-1)*query.getPageSize()+1);		
			model.put("results.end", (query.getPage()-1)*query.getPageSize()+results.size());			
		}
		
		model.put("search.time", ((float)totalTime)/1000);

		Session session = sessionFactory.openSession();
		try {
			List<Object> resultList = new ArrayList<Object>();			
			model.put("results", resultList);
			
			for (VectorRankedResult result : results) {
				Class<?> resultClass = result.getKey().getClazz();
				Serializable resultId = result.getKey().getId();
				resultList.add(
							new ResultRow(session.load(resultClass, resultId),result.getScore())
						);
			}
			
		} finally {
			if(session!=null) session.close();
		}
		
        return new ModelAndView("searchResultsView","searchResults",model);
    }
    
	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
	
	public class ResultRow {
		public final Object result;
		public final Double score;
		
		public ResultRow(Object result, Double score) {
			this.result = result;
			this.score = score;
		}
		
		public Object getResult() {
			return result;
		}
		
		public Double getScore() {
			return score;
		}
	}
}
