
package com.jklas.sample.petclinic.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.jklas.sample.petclinic.Owner;

public class OwnersHome extends MultiActionController  {
	
	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
    public OwnersHome() {  }
    
    public ModelAndView findOwnersHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	return new ModelAndView("ownersHomeView");    	
    }

    public ModelAndView deleteOwnerHandler(HttpServletRequest request, HttpServletResponse response) throws ServletException {	
    	String reqId = request.getParameter("ownerId");
    	    	
    	Session session = sessionFactory.openSession();
    	Transaction transaction = session.getTransaction();
    	int id = Integer.parseInt(reqId);
    	
		try {
			transaction.begin();
			Owner owner = (Owner)session.load(Owner.class, id);
			session.delete(owner);			
    		transaction.commit();	
    	} catch(HibernateException ex) {
    		if(transaction!=null && transaction.isActive()) transaction.rollback();    		
    	} finally {
    		if(session!=null) session.close();
    	}
		
    	return new ModelAndView("ownersHomeView");    	
    }

    
}
