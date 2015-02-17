package service.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import model.TUser;

public class JpaTest {

	public static void main(String[] args) {
		System.out.println("Begin of main.");
		
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("SMARTCAMPUS");
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		TypedQuery<TUser> query = entityManager.createNamedQuery("TUser.findAll", TUser.class);
		List<TUser> users = query.getResultList();

		for (TUser tUser : users) {
			System.out.println(tUser.getLoginName());
		}

		entityManager.close();
		entityManagerFactory.close();

		System.out.println("End of main");
	}

}
