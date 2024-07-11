package com.ncc.employee_management.user;

import com.ncc.employee_management.projection.classBased.UserDto;
import com.ncc.employee_management.projection.interfaceBased.closedProjection.UserNameAndTokens;
import com.ncc.employee_management.projection.interfaceBased.openProjection.UserFullname;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
//    Iterable

    //    Iterator
//    Seq
    //--------------------------------------------------
//    List<User> findByFullnameContainingIgnoreCaseOrderByFullname(String name);

    //Using Named Parameters
    @Query("SELECT u FROM User u where u.firstname= :firstname AND u.lastname = :lastname")
    List<User> findByFullname(@Param("firstname") String firstname,
                              @Param("lastname") String lastname);

    User findByCheckinCode(String checkinCode);

    //
    @Modifying //use for delete or update dont need for method name, or custom method --> method declare in custom repo
    @Query("update User u set u.firstname = ?1 where u.lastname = ?2")
    int setFixedFirstnameFor(String firstname, String lastname);

    //closed projection
    Optional<UserNameAndTokens> findByLastnameAndFirstnameContainingIgnoreCase(String firstname, String lastname);

    //open projection
    List<UserFullname> findByLastname(String lastname);

    List<User> findByFirstnameContaining(String firstname);

    //class-based projection
    List<UserDto> findByFirstnameContainingIgnoreCase(String firstname);

    //dynamic projection
    //User user = personRepository.findByUsername("Doe", User.class);
    <T> T findByUsername(String username, Class<T> type);

}
