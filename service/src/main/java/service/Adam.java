package service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import model.TUser;

public class Adam {

	private static final String PERSISTENCE_UNIT_NAME = "SMARTCAMPUS";
	private static EntityManagerFactory entityManagerFactory;
	private static EntityManager entityManager;
	
	static {
		entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		entityManager = entityManagerFactory.createEntityManager();
	}
	
	public static void main(String[] args) {
		System.out.println("Hello!");
		
		TypedQuery<TUser> query = entityManager.createNamedQuery("TUser.findAll", TUser.class);
		List<TUser> users = query.getResultList();
		
		for (TUser tUser : users) {
			System.out.println(tUser.getLoginName());
		}
		
		entityManager.close();
		entityManagerFactory.close();
	}

}
