package com.ncc.employee_management.projection.interfaceBased.openProjection;

import org.springframework.beans.factory.annotation.Value;

//Open projections do have a drawback though; Spring Data can’t optimize query execution,
// as it doesn’t know in advance which properties will be used.
// Thus, we should only use open projections when closed projections aren’t capable of handling our requirements.
public interface UserFullname {
    // ...

    @Value("#{target.firstName + ' ' + target.lastName}")
    String getFullName();
}