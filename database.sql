--  医生表
CREATE TABLE IF NOT EXISTS doctors
(

  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,                   --  自增主键
  username VARCHAR(50),                                         --  用户名字
  password VARCHAR(500),                                        --  用户密码
  realname VARCHAR(50),                                         --  真实姓名
  sex  VARCHAR(5),                                              --  性别
  sectionid  int,                                     		--  科室id
  time TIMESTAMP DEFAULT CURRENT_TIMESTAMP  	     		--  注册时间

);


-- 医生地理信息表

CREATE TABLE IF NOT EXISTS userslocation
(

  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,                   --  自增主键
  userid int,                                         		--  用户id
  usertype int,						--  用户类型
  x     float,            --  x
  y     float,            --y
  time TIMESTAMP DEFAULT CURRENT_TIMESTAMP  	     		--  注册时间

);

--消息表
CREATE TABLE IF NOT EXISTS messages
(

  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,                   --  自增主键
  fromid 	int,                                              --  发起人id
  toid   	int,						--  接受人id
  fromtype    int,							--  发起人类型
  totype      int,							--  接受人类型
  content     VARCHAR(500),                                     --  消息
  time TIMESTAMP DEFAULT CURRENT_TIMESTAMP  	     		--  注册时间

);

--患者表

CREATE TABLE IF NOT EXISTS patients
(

  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,                   --  自增主键
  userid 	int,                                              --  患者id
  realname   	VARCHAR(50)					       --  患者姓名
  
);

--医生患者关联表

CREATE TABLE IF NOT EXISTS patientswithdoctors
(

  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,                   --  自增主键
  patientid 	int,                                              --  患者id
  doctorid   	int				       		--  医生id
  
);

--看病申请表

CREATE TABLE IF NOT EXISTS patientswithdoctors
(

  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,                    --  自增主键
  patientid 		int,                                        --  患者id
  doctorid   		int,				                                 --  医生id
  isreceived   	int,				       	                        --  是否接受
  bgtime             TIMESTAMP,					                    --开始时间
  edtime            TIMESTAMP					                      --结束时间
  
);

--科室维护

CREATE TABLE IF NOT EXISTS section
(

  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,                    --  自增主键
  sectionname 	VARCHAR(50)                                      --  科室名（疾病名）
  
);


db.createUser(
  {
    user: "jack",
    pwd: "1313",
    roles:
    [
      {
        role: "userAdminAnyDatabase",
        db: "admin"
      }
    ]
  }
)


db.runCommand(
  {
    usersInfo:"jack",
    showPrivileges:true
  }
)






use doctorapp

db.createUser( { user: "jack", pwd: "1313"} )
mongo  127.0.0.1/doctorapp -u jack -p 1313


db.createUser(
  {
    user:"jack",
    pwd:"1313",
    roles:[
      {role:"userAdminAnyDatabase",db:"doctorapp"}
    ]
  }
)


show dbs
show collections





db.userslocation.insert(
   {
      loc : { type: "Point", coordinates: [120, 30 ] },
      userid: 1,
      usertype : 1
   }
)
db.userslocation.insert(
   {
      loc : { type: "Point", coordinates: [120.003, 30.005 ] },
      userid: 2,
      usertype : 1
   }
)

db.userslocation.insert(
   {
      loc : { type: "Point", coordinates: [120.003, 30.005 ] },
      userid: 2,
      usertype : 1
   }
)


db.userslocation.ensureIndex( { loc : "2dsphere" } )


--医生表
db.doctors.insert(
   {

	      loc : { type: "Point", coordinates: [120.003, 30.005 ] },
	      userinfo:{
	          username:"jack",
	          password : "1",
	          realname : "赵医生",
	          sex:"男",
	          sectionname:"内科"

	      },
	      logintime:new Date()
   	}
)



db.doctors.ensureIndex( { loc : "2dsphere" } )

--患者表

db.patients.insert({
username : "jane",
realname : "王小明",
password:"1"

})

--枚举表维护

db.enumerate.insert(
   {
	      enumeratename: "骨科",
        enumeratevalue:"骨科",
        enumeratetype:"section"
   }
)

--消息表维护 (1 doctor,0 patient)

db.messages.insert(
   {
	      fromid: "551b4cb83b83719a9aba9c01",
        toid:"551b4e1d31ad8b836c655377",
        fromtype:1,
        totype:1,
        msgtime:new Date(),
        content:"hello jack",
        isread:false
   }
)


--医生vs患者关联表
db.doctorsvspatients.insert(
  {
        doctorid:"551b4cb83b83719a9aba9c01",
        patientid:"551dfe4dcb4b40507ebc3ba7"
  }
)

--医生vs医生关联表
db.doctorsvsdoctors.insert(
  {
        doctorid:"551b4cb83b83719a9aba9c01",
        rid:"551b4e1d31ad8b836c655377"
  }
)

--添加申请表 (1,doctor ;0 patient)
db.applyfor.insert(
  {
        fromid:"551b4cb83b83719a9aba9c01",
        toid:"551b4e1d31ad8b836c655377",
        applytype:1
  }
)

--医生患者推荐表(1,doctor ;0 patient)

db.recommend.insert(
  {
      doctorid:"551b4e1d31ad8b836c655377",
      patientid:"551dfe4dcb4b40507ebc3ba7",
      fromid:"551b4cb83b83719a9aba9c01",
      rectype:1,
      isdoctoraccepted:false,
      ispatientaccepted:false,
      isreadbydoctor:false,
      isreadbypatient:false
  }
)

--黑名单(3)

db.blacklist.insert(
  {
        patientid:"551dfe4dcb4b40507ebc3ba7",
        doctorid:"551b4e1d31ad8b836c655377",
        times:0
  }

)

--定制推送

db.custompush.insert(
  {
        content:"",
        sendtime:new Date(),

        frequency:"once",
        doctorid:"551b4e1d31ad8b836c655377"
  }

)


-- 急救申请表

db.applyquick.insert(
{
    applytime : new Date(),
    applyid :"551dfe4dcb4b40507ebc3ba7",
    doctorid:"551b4e1d31ad8b836c655377",
    ispay:false
}
)

--用户资金表
db.money.insert(
 {
     totalmoney:1,
     userid:"551dfe4dcb4b40507ebc3ba7"
 }
)

--急救申请医生
db.applydoctors.insert(
{
   patientid:"551dfe4dcb4b40507ebc3ba7",
   doctorid:"551b4e1d31ad8b836c655377",
   isaccept:""
}

)










db.userslocation.find( { loc :
                   { $nearSphere :
                     { $geometry :
                        { type : "Point" ,
                          coordinates : [ 120, 30 ] } ,
                       $maxDistance : 100
             } } } )












