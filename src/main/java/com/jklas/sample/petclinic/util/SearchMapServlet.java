package com.jklas.sample.petclinic.util;

import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.jklas.sample.petclinic.PetType;
import com.jklas.sample.petclinic.Vet;
import com.jklas.search.SearchEngine;
import com.jklas.search.configuration.AnnotationConfigurationMapper;
import com.jklas.search.exception.SearchEngineMappingException;

public class SearchMapServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public final static String SERVLET_CONTEXT_MAPPING_PROPERTY = "modelsearch-init-mappings";
	public final static String SERVLET_CONTEXT_PETTYPE_PROPERTY = "petclinic-init-pettype";
	public final static String SERVLET_CONTEXT_VETS_PROPERTY = "petclinic-init-vets";

	@Override
	public void init() throws ServletException {
		mapFromProperty(getServletContext().getInitParameter(SERVLET_CONTEXT_MAPPING_PROPERTY));
		createPetTypes(getServletContext().getInitParameter(SERVLET_CONTEXT_PETTYPE_PROPERTY));
//		createVets(getServletContext().getInitParameter(SERVLET_CONTEXT_VETS_PROPERTY));
	}

//	private void createVets(String initParameter) {
//		StringTokenizer outer = new StringTokenizer(initParameter," ,;\t\n");
//
//		BeanFactory factory = WebApplicationContextUtils.getWebApplicationContext( this.getServletContext() );
//		SessionFactory sessionFactory = (SessionFactory)factory.getBean("sessionFactory");
//
//		Session session = sessionFactory.openSession();
//
//		session.beginTransaction();
//
//		int i=0;
//		while(outer.hasMoreElements()) {
//			StringTokenizer inner = new StringTokenizer(initParameter," ,;\t\n");
//			while(inner.hasMoreElements()) {
//				Vet vet = new Vet();
//				vet.getSpecialties().add();
//				PetType petType = new PetType();
//				petType.setId(i++);
//				petType.setName(tok.nextToken());
//				session.save(petType);							
//			}
//		}
//
//		session.getTransaction().commit();
//	}

	private void createPetTypes(String initParameter) {		
		StringTokenizer tok = new StringTokenizer(initParameter," ,;\t\n");

		BeanFactory factory = WebApplicationContextUtils.getWebApplicationContext( this.getServletContext() );
		SessionFactory sessionFactory = (SessionFactory)factory.getBean("sessionFactory");

		Session session = sessionFactory.openSession();

		session.beginTransaction();

		int i=0;
		while(tok.hasMoreElements()) {
			PetType petType = new PetType();
			petType.setId(i++);
			petType.setName(tok.nextToken());
			session.save(petType);			
		}

		session.getTransaction().commit();

	}

	public static void mapFromProperty(String classes) {

		StringTokenizer tok = new StringTokenizer(classes," ,;\t\n");

		SearchEngine.getInstance().newConfiguration();
		AnnotationConfigurationMapper acm = new AnnotationConfigurationMapper();

		while(tok.hasMoreElements()) {
			String currentClass = tok.nextToken();

			try {
				Class<?> classToMap = Class.forName(currentClass);
				acm.map(classToMap);
			} catch (ClassNotFoundException e) {
				Logger.getLogger(SearchMapServlet.class).error("Couldn't load class: "+currentClass,e);
			} catch (SearchEngineMappingException e) {
				Logger.getLogger(SearchMapServlet.class).error("Couldn't map class: "+currentClass,e);
			}
		}
	}

}
