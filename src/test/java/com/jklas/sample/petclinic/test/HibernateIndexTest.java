package com.jklas.sample.petclinic.test;


import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.PreDeleteEventListener;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreUpdateEventListener;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.jklas.sample.petclinic.Owner;
import com.jklas.sample.petclinic.Pet;
import com.jklas.sample.petclinic.PetType;
import com.jklas.search.engine.VectorSearch;
import com.jklas.search.engine.dto.VectorRankedResult;
import com.jklas.search.exception.SearchEngineMappingException;
import com.jklas.search.index.memory.MemoryIndex;
import com.jklas.search.index.memory.MemoryIndexReader;
import com.jklas.search.index.memory.MemoryIndexWriterFactory;
import com.jklas.search.indexer.DefaultIndexerService;
import com.jklas.search.indexer.pipeline.DefaultIndexingPipeline;
import com.jklas.search.interceptors.SearchInterceptor;
import com.jklas.search.interceptors.hibernate.HibernateEventInterceptor;
import com.jklas.search.query.vectorial.VectorQuery;
import com.jklas.search.query.vectorial.VectorQueryParser;
import com.jklas.search.util.Utils;

public class HibernateIndexTest {

	private static SessionFactory sessionFactory;

	private Session session = null;

	@BeforeClass
	public static void setupSessionFactory() throws SearchEngineMappingException{
		Utils.configureAndMap(Owner.class);
		Utils.configureAndMap(Pet.class);

		HibernateEventInterceptor listener= 
			new HibernateEventInterceptor(
					new SearchInterceptor(
							new DefaultIndexerService(
									new DefaultIndexingPipeline(),
									MemoryIndexWriterFactory.getInstance())));

		
		Configuration configuration = new Configuration().configure();
		configuration.addResource("petclinic.hbm.xml");
		configuration.getEventListeners().setPreInsertEventListeners(new PreInsertEventListener[]{listener});
		configuration.getEventListeners().setPreUpdateEventListeners(new PreUpdateEventListener[]{listener});
		configuration.getEventListeners().setPreDeleteEventListeners(new PreDeleteEventListener[]{listener});

		sessionFactory = configuration.buildSessionFactory();

	}

	@Before
	public void Setup(){
		session = sessionFactory.openSession();		
		session.beginTransaction();
		session.setFlushMode(FlushMode.ALWAYS);
	}

	@After
	public void Cleanup(){
		MemoryIndex.renewAllIndexes();
		session.getTransaction().rollback();
		session.getTransaction().begin();
	}

	@Test
	public void AddOwner() {	
		Owner owner = new Owner();
		
		owner.setAddress("Paseo Colon 850");
		owner.setCity("Buenos Aires");
		owner.setId(0);
		owner.setLastName("Klas");
		owner.setTelephone("12345678");
		
		session.save(owner);
	}
	
	@Test
	public void AddPet() {
		Pet pet = new Pet();
		
		PetType type = new PetType();
		type.setId(0);
		type.setName("Cat");
		session.save(type);
		
		pet.setBirthDate(new Date());
		pet.setId(0);
		pet.setName("Cuky");
		pet.setType(type);
		
		session.save(pet);
		
		Pet retrieved = (Pet) session.createQuery("from Pet pet").uniqueResult();
				
		Assert.assertEquals(pet, retrieved);
	}

	 
	@Test
	public void HibernateInterceptorWithSpringIndexObjects() {
		ApplicationContext context;
		BeanFactory factory;	
		context = new ClassPathXmlApplicationContext("applicationContextTesting-hibernate.xml");
		factory = (BeanFactory) context;

		SessionFactory localSessionFactory = (SessionFactory)factory.getBean("sessionFactory");
		
		Session localSession = localSessionFactory.openSession();		
		localSession.beginTransaction();
		localSession.setFlushMode(FlushMode.ALWAYS);
		
		Owner owner = new Owner();
		
		owner.setAddress("Paseo Colon 850");
		owner.setCity("Buenos Aires");
		owner.setId(0);
		owner.setLastName("Klas");
		owner.setTelephone("12345678");
		
		session.save(owner);
		
		// when searching for the dog name we get dog
		VectorQuery query = new VectorQueryParser("Klas").getQuery();		
		List<VectorRankedResult> result = new VectorSearch(query, new MemoryIndexReader()).search();
		
		Assert.assertTrue( result.size() == 1 );
		Assert.assertEquals( owner.getClass(), result.get(0).getKey().getClazz() );			
		
	}
	
}
