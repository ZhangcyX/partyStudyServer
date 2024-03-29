package com.zcy.party.server;

import com.zcy.party.domain.ObjectProblem;

import java.util.List;

public interface ObjectProblemServer {
    List<ObjectProblem> getAllObjProblem();
    List<ObjectProblem> getTestProblem();
    int updateObjProblem(ObjectProblem objectProblem);
    int deleteProblem(int id);
    int insertProblem(ObjectProblem objectProblem);
}
