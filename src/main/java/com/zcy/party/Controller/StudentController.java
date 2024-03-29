package com.zcy.party.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zcy.party.dao.GradeMapper;
import com.zcy.party.dao.SchoolMapper;
import com.zcy.party.domain.*;
import com.zcy.party.server.GradeServer;
import com.zcy.party.server.StuAnswerServer;
import com.zcy.party.server.StudentServer;
import com.zcy.party.server.impl.GradeServerImpl;
import com.zcy.party.server.impl.StuAnswerServerImpl;
import com.zcy.party.server.impl.StudentServerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.swing.plaf.synth.SynthTextAreaUI;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@RestController
@Controller
public class StudentController {
    @Autowired
    StudentServerImpl studentServer;
    @Autowired
    GradeServerImpl gradeServer;
    @Autowired
    SchoolMapper schoolMapper;
    @Autowired
    StuAnswerServerImpl stuAnswerServer;

    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "api/insertStuAnswer",method = RequestMethod.POST)
    public Object insertStuAnswer(HttpServletRequest request) throws IOException {
        String s = StreamUtils.copyToString(request.getInputStream(),Charset.forName("UTF-8"));
        JSONObject jsonObject = JSON.parseObject(s);
        String stuId = jsonObject.get("stuId").toString();
        int paperId =Integer.parseInt( jsonObject.get("paperId").toString());
        float objGrade = Float.parseFloat(jsonObject.get("objGrade").toString());
        JSONArray jsonArray = JSONArray.parseArray(jsonObject.get("subAnswer").toString());
        int i = stuAnswerServer.insertStuAnswer(stuId,paperId,objGrade,jsonArray.get(0).toString(),
                jsonArray.get(1).toString(),jsonArray.get(2).toString(),jsonArray.get(3).toString(),
                jsonArray.get(4).toString());
        JSONObject jsonObject1 = new JSONObject();
         if(i>0) jsonObject1.put("code",200);
         else jsonObject1.put("code",400);
         return jsonObject1;
    }
    @ResponseBody
    @CrossOrigin
    @RequestMapping(value = "/api/insertGrade",method = RequestMethod.POST)
    public void insertStuGrade(HttpServletRequest request) throws IOException {
        String s = StreamUtils.copyToString(request.getInputStream(), Charset.forName("UTF-8"));
        JSONObject jsonObject = JSONObject.parseObject(s);
        Grade accept_grade = new Grade(Integer.parseInt(jsonObject.get("CorrectedNum").toString()),
                Integer.parseInt(jsonObject.get("DisCorrectedNum").toString()),
               jsonObject.get("questionMaster").toString(),
                jsonObject.get("id").toString());
        gradeServer.insertStuGrade(accept_grade);
    }
    @CrossOrigin
    @RequestMapping(value = "api/getHistory",method = RequestMethod.GET)
    public Object getHistory(HttpServletRequest request){
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        String s = request.getParameter("id");
        Student student = studentServer.getStuByStuID(s);
        @JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
        class Data{
            String name;
            String id;
            int CorrectedNum;
            int DisCorrectedNum;
            int grade;
            String questionMaster;
            String createTime;
            public Data(String name, String id, int correctedNum, int disCorrectedNum, int grade, String createTime,String questionMaster) {
                this.name = name;
                this.id = id;
                this.CorrectedNum = correctedNum;
                this.DisCorrectedNum = disCorrectedNum;
                this.grade = grade;
                this.createTime = createTime;
                this.questionMaster = questionMaster;
            }
            public String getQuestionMaster() {
                return questionMaster;
            }
            public String getName() {
                return name;
            }
            public void setName(String name) {
                this.name = name;
            }
            public String getId() {
                return id;
            }
            public void setId(String id) {
                this.id = id;
            }
            public int getCorrectedNum() {
                return CorrectedNum;
            }
            public void setCorrectedNum(int correctedNum) {
                CorrectedNum = correctedNum;
            }
            public int getDisCorrectedNum() {
                return DisCorrectedNum;
            }
            public void setDisCorrectedNum(int disCorrectedNum) {
                DisCorrectedNum = disCorrectedNum;
            }
            public int getGrade() {
                return grade;
            }
            public void setGrade(int grade) {
                this.grade = grade;
            }
            public String getCreateTime() {
                return createTime;
            }
            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }
        }
        for(int i = 0;i<student.getGrades().size();i++){
            Data data = new Data(student.getStu_name(),
                    student.getStu_id(),student.getGrades().get(0).getCorrectedNum(),
                    student.getGrades().get(i).getDisCorrectedNum(),
                    student.getGrades().get(i).getCorrectedNum()*5,
                    student.getGrades().get(i).getCreateTime().toString(),
                    student.getGrades().get(i).getQuestionMaster());
            jsonArray.add(data);
        }
         jsonObject.put("history",jsonArray);
         return jsonObject;
    }
    @CrossOrigin
    @RequestMapping(value = "api/getStuInfo",method = RequestMethod.GET)
    public Object getStuInfo(HttpServletRequest request){
        JSONObject jsonObject =new JSONObject();
        String id = request.getParameter("id");
        Student student = studentServer.getStuByStuID(id);
        jsonObject.put("grade",student.getStu_grade());
        jsonObject.put("major",student.getMajor());
        jsonObject.put("school",student.getStu_school());
        return jsonObject;
    }
    @CrossOrigin
    @RequestMapping(value = "api/getAllStuInfo",method = RequestMethod.GET)
    public Object getAllStuInfo(HttpServletRequest request){
        List<Student> students = studentServer.getAllStudentInfo();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray =new JSONArray();
        for(int i=0;i<students.size();i++){
            jsonArray.add(students.get(i));
        }
        jsonObject.put("students",jsonArray);
        return jsonObject;
    }

    @CrossOrigin
    @RequestMapping(value = "api/getMypaper",method = RequestMethod.GET)
    public Object getMapaper(HttpServletRequest request){
         String s = request.getParameter("id");
         List<StuPaper> list = studentServer.getMyPaper(s);
         JSONArray jsonArray = new JSONArray();
         class ResultData{
             String name;
             String creator;
             String createTime;
             int id;

             public int getId() {
                 return id;
             }

             public void setId(int id) {
                 this.id = id;
             }

             public String getName() {
                 return name;
             }

             public void setName(String name) {
                 this.name = name;
             }

             public String getCreator() {
                 return creator;
             }

             public void setCreator(String creator) {
                 this.creator = creator;
             }

             public String getCreateTime() {
                 return createTime;
             }

             public void setCreateTime(String createTime) {
                 this.createTime = createTime;
             }
         }
         for(int i=0;i<list.size();i++){
             ResultData resultData = new ResultData();
             resultData.setId(list.get(i).getPaper().getId());
             resultData.setName(list.get(i).getPaper().getName());
             resultData.setCreator(list.get(i).getPaper().getCreator());
             resultData.setCreateTime(list.get(i).getPaper().getCreateTime());
             jsonArray.add(resultData);
         }
         JSONObject jsonObject = new JSONObject();
         jsonObject.put("paperInfo",jsonArray);
         return jsonObject;
    }

    @CrossOrigin
    @RequestMapping(value = "api/getMeigeSchoolRenshu",method = RequestMethod.GET)
    public Object  getMeigeSchoolRenshu(){
        List<ResultData> list = studentServer.getMeigeSchoolRenshu();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        class Datatype{
            String name;
            int value;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getValue() {
                return value;
            }

            public void setValue(int data) {
                this.value = data;
            }
        }
        for(int i=0;i<list.size();i++){
            String s = schoolMapper.getNameById(list.get(i).getType());
            Datatype data = new Datatype();
            data.setName(s);
            data.setValue(list.get(i).getData());
            jsonArray.add(data);
        }
        jsonObject.put("data",jsonArray);
        return jsonObject;
    }
}
