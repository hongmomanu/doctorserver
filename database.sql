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

CREATE TABLE IF NOT EXISTS doctorslocation
(

  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,                   --  自增主键
  userid int,                                         		--  用户id
  usertype int，							--  用户类型
  time TIMESTAMP DEFAULT CURRENT_TIMESTAMP  	     		--  注册时间

);

--消息表
CREATE TABLE IF NOT EXISTS messages
(

  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,                   --  自增主键
  fromid 	int,                                              --  发起人id
  toid   	int，							--  接受人id
  fromtype    int，							--  发起人类型
  totype      int，							--  接受人类型
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












