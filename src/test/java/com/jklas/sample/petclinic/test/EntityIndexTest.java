package com.jklas.sample.petclinic.test;

import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jklas.sample.petclinic.Owner;
import com.jklas.sample.petclinic.Person;
import com.jklas.sample.petclinic.Pet;
import com.jklas.sample.petclinic.PetType;
import com.jklas.sample.petclinic.Vet;
import com.jklas.sample.petclinic.Visit;
import com.jklas.sample.petclinic.util.SearchMapServlet;
import com.jklas.search.SearchEngine;
import com.jklas.search.engine.VectorSearch;
import com.jklas.search.engine.dto.VectorRankedResult;
import com.jklas.search.exception.IndexObjectException;
import com.jklas.search.exception.SearchEngineMappingException;
import com.jklas.search.index.memory.MemoryIndex;
import com.jklas.search.index.memory.MemoryIndexReader;
import com.jklas.search.query.vectorial.VectorQuery;
import com.jklas.search.query.vectorial.VectorQueryParser;
import com.jklas.search.util.Utils;

public class EntityIndexTest {

	@Before
	public void before() {
		SearchEngine.getInstance().reset();
		MemoryIndex.renewAllIndexes();
	}
	
	@Test
	public void ServletMapsOneClass() throws ServletException {	
		SearchMapServlet.mapFromProperty("com.jklas.sample.petclinic.Owner");
		Assert.assertNotNull(SearchEngine.getInstance().getConfiguration().getMapping(Owner.class));
	}

	@Test
	public void ServletMapsAllClasses() throws ServletException {
		SearchMapServlet.mapFromProperty(
				"com.jklas.sample.petclinic.Owner;" +
				"com.jklas.sample.petclinic.Person;" +
				"com.jklas.sample.petclinic.Vet;" +
				"com.jklas.sample.petclinic.Pet;" +
				"com.jklas.sample.petclinic.PetType;");
		Assert.assertNotNull(SearchEngine.getInstance().getConfiguration().getMapping(Owner.class));
		Assert.assertNotNull(SearchEngine.getInstance().getConfiguration().getMapping(Person.class));
		Assert.assertNotNull(SearchEngine.getInstance().getConfiguration().getMapping(Vet.class));
		Assert.assertNotNull(SearchEngine.getInstance().getConfiguration().getMapping(Pet.class));
		Assert.assertNotNull(SearchEngine.getInstance().getConfiguration().getMapping(PetType.class));

	}

	@Test
	public void IndexedOwnerIsRetrieved() throws ServletException, SearchEngineMappingException, IndexObjectException {
		Owner owner = new Owner();
		owner.setAddress("Paseo Colon 850");
		owner.setCity("Buenos Aires");
		owner.setFirstName("Julian");
		owner.setLastName("Klas");
		owner.setTelephone("+54 11 XXXX-XXXX");
		owner.setId(0);
		
		Utils.setupSampleMemoryIndex(owner);

		VectorQuery query = new VectorQueryParser("Julián").getQuery();		
		
		List<VectorRankedResult> result = new VectorSearch(query, new MemoryIndexReader()).search();
		
		Assert.assertTrue( result.size() == 1 );
		Assert.assertTrue( result.get(0).getKey().getClazz().equals(owner.getClass()) );		
		Assert.assertTrue( result.get(0).getKey().getId().equals(owner.getId()) );
	}

	@Test
	public void IndexedPetRetrievesAllIndexedFields() throws ServletException, SearchEngineMappingException, IndexObjectException {
		Owner owner = new Owner();
		owner.setAddress("Paseo Colon 850");
		owner.setCity("Buenos Aires");
		owner.setFirstName("Julian");
		owner.setLastName("Klas");
		owner.setTelephone("+54 11 XXXX-XXXX");
		owner.setId(0);
		
		PetType type = new PetType();
		type.setId(2);
		type.setName("Dog");
		
		Pet pet = new Pet();
		pet.setBirthDate(new Date());
		pet.setId(1);
		pet.setName("Fido");
		pet.setType(type);
		
		owner.addPet(pet);
		
		Utils.setupSampleMemoryIndex(pet);

		// when searching for the dog name we get dog
		VectorQuery query = new VectorQueryParser("fido").getQuery();		
		List<VectorRankedResult> result = new VectorSearch(query, new MemoryIndexReader()).search();
		
		Assert.assertTrue( result.size() == 1 );
		Assert.assertTrue( result.get(0).getKey().getClazz().equals(pet.getClass()) );		
		Assert.assertTrue( result.get(0).getKey().getId().equals(pet.getId()) );
		
		// when searching for the owner, we also get the dog
		query = new VectorQueryParser("klas").getQuery();		
		result = new VectorSearch(query, new MemoryIndexReader()).search();

		Assert.assertTrue( result.size() == 1 );
		Assert.assertTrue( result.get(0).getKey().getClazz().equals(pet.getClass()) );		
		Assert.assertTrue( result.get(0).getKey().getId().equals(pet.getId()) );
		
		// finally, we got a pet type index
		query = new VectorQueryParser("dog").getQuery();		
		result = new VectorSearch(query, new MemoryIndexReader()).search();
		
		Assert.assertTrue( result.size() == 1 );
		Assert.assertTrue( result.get(0).getKey().getClazz().equals(pet.getClass()) );		
		Assert.assertTrue( result.get(0).getKey().getId().equals(pet.getId()) );
		
	}
	
	@Test
	public void IndexedVisitIsRetrieved() throws ServletException, SearchEngineMappingException, IndexObjectException {
		Visit visit = new Visit();
		visit.setDate(new Date());
		visit.setId(0);
		visit.setDescription("Vacuna");
		
		Utils.setupSampleMemoryIndex(visit);

		VectorQuery query = new VectorQueryParser("Vacuna").getQuery();		
		
		List<VectorRankedResult> result = new VectorSearch(query, new MemoryIndexReader()).search();
		
		Assert.assertTrue( result.size() == 1 );
		Assert.assertTrue( result.get(0).getKey().getClazz().equals(visit.getClass()) );		
		Assert.assertTrue( result.get(0).getKey().getId().equals(visit.getId()) );
	}
}
