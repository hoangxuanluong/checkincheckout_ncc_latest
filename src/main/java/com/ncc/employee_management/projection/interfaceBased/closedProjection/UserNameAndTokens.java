package com.ncc.employee_management.projection.interfaceBased.closedProjection;


//closed projection
//A projection interface whose accessor methods all match properties of the target aggregate
// is considered to be a closed projection.
// The following example (which we used earlier in this chapter, too) is a closed projection
//If you use a closed projection, Spring Data can optimize the query execution,
// because we know about all the attributes that are needed to back the projection proxy
public interface UserNameAndTokens {//owning side

    String getFirstname();

    String getLastname();

    interface TokenSummary {//inverse side

        String getToken();
    }
}