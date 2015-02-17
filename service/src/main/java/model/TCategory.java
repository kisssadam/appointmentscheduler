package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the T_CATEGORY database table.
 * 
 */
@Entity
@Table(name="T_CATEGORY")
@NamedQuery(name="TCategory.findAll", query="SELECT t FROM TCategory t")
public class TCategory implements Serializable {
	private static final long serialVersionUID = 1L;
	private long categoryId;
	private String description;
	private String title;
	private List<TEvent> TEvents;
	private List<TCategory> TCategories1;
	private List<TCategory> TCategories2;

	public TCategory() {
	}


	@Id
	@Column(name="CATEGORY_ID", unique=true, nullable=false, precision=10)
	public long getCategoryId() {
		return this.categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}


	@Column(nullable=false, length=1000)
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	@Column(nullable=false, length=100)
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	//bi-directional many-to-many association to TEvent
	@ManyToMany(mappedBy="TCategories")
	public List<TEvent> getTEvents() {
		return this.TEvents;
	}

	public void setTEvents(List<TEvent> TEvents) {
		this.TEvents = TEvents;
	}


	//bi-directional many-to-many association to TCategory
	@ManyToMany
	@JoinTable(
		name="T_HIERARCHY"
		, joinColumns={
			@JoinColumn(name="CHILD_ID", nullable=false)
			}
		, inverseJoinColumns={
			@JoinColumn(name="PARENT_ID", nullable=false)
			}
		)
	public List<TCategory> getTCategories1() {
		return this.TCategories1;
	}

	public void setTCategories1(List<TCategory> TCategories1) {
		this.TCategories1 = TCategories1;
	}


	//bi-directional many-to-many association to TCategory
	@ManyToMany(mappedBy="TCategories1")
	public List<TCategory> getTCategories2() {
		return this.TCategories2;
	}

	public void setTCategories2(List<TCategory> TCategories2) {
		this.TCategories2 = TCategories2;
	}

}