package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static ru.javawebinar.topjava.util.DateTimeUtil.getEndExclusive;
import static ru.javawebinar.topjava.util.DateTimeUtil.getStartInclusive;

@Repository
public class DataJpaMealRepository implements MealRepository {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private CrudMealRepository crudRepository;

    @Override
    @Transactional
    public Meal save(Meal meal, int userId) {

        if (!meal.isNew() && get(meal.getId(), userId) == null) {
            return null;
        }
        meal.setUser(em.getReference(User.class, userId));
        Meal meal1 = crudRepository.save(meal);

        return meal1.getUser().getId() == userId ? meal : null;
    }

    @Override
    public boolean delete(int id, int userId) {
        return crudRepository.delete(id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {

        Meal meal = crudRepository.findById(id).orElse(null);
        if (meal != null) {
            return meal.getUser().getId() == userId ? meal : null;
        }
        return null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return crudRepository.findAll(userId);
    }

    @Override
    public List<Meal> getBetweenInclusive(LocalDate startDate, LocalDate endDate, int userId) {
        return crudRepository.findAll(userId, getStartInclusive(startDate), getEndExclusive(endDate));
    }
}
