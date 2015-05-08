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
	      isconfirmed:true,
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
        rtime:new Date(),
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


-- 门诊挂号申请表

db.applyquick.insert(
{
    applytime : new Date(),
    applyid :"551dfe4dcb4b40507ebc3ba7",
    doctorid:"551b4e1d31ad8b836c655377",
    ispay:false,
    isreply:false,
    addmoney:0
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
   isaccept:false,
   applytime:new Date(),
   isread:false
}

)










db.userslocation.find( { loc :
                   { $nearSphere :
                     { $geometry :
                        { type : "Point" ,
                          coordinates : [ 120, 30 ] } ,
                       $maxDistance : 100
             } } } )



--------------------------------------affilicatedhospital--------------------------------



db.illdata.insert(
  {
    name:"糖尿病",
    depts:["耳鼻咽喉科","神经精神科"],
    about:"小便呈云絮状",
    illreasion:"尿路感染95%以上是由单一细菌引起的。其中90%的门诊病人和50%左右的住院病人，其病原菌是大肠埃希杆菌，此菌血清分型可达140种，致尿感型大肠埃希杆菌与病人粪便中分离出来的大肠埃希杆菌属同一种菌型，多见于无症状菌尿或无并发症的尿感；变形杆菌、产气杆菌、克雷白肺炎杆菌、铜绿假单胞菌、粪链球菌等见于再感染、留置导尿管、有并发症之尿路感染者；白色念珠菌、新型隐球菌感染多见于糖尿病及使用糖皮质激素和免疫抑制药的病人及肾移植后；金黄色葡萄球菌多见于皮肤创伤及吸毒者引起的菌血症和败血症；病毒、支原体感染虽属少见，近年来有逐渐增多趋向。多种细菌感染见于留置导尿管、神经源性膀胱、结石、先天性畸形和阴道，肠道、尿道瘘等。",
    illdescription:"尿路感染的临床表现比较广泛。尿路感染根据感染部位不同，可分为肾盂肾炎、膀胱炎、尿道炎；根据有无尿路功能或器质上的异常，又有复杂性和非复杂性尿路感染之别；根据炎症的性质不同，又可分为急性和慢性尿路感染。但尿路感染仍有它的共同临床表现：尿路刺激征，即尿频、尿急、尿痛、排尿不适等症状。这些症状，不同的病人表现为轻重程度不一。急性期炎症患者往往有明显的尿路刺激征；但在老年人、小儿及慢性尿路感染患者，则通常尿路刺激症状较轻，如轻度的尿频，或尿急，或排尿不适等。全身中毒症状，如发热、寒战、头痛等。主要见于上尿路感染病人，特别是急性尿路感染及伴有尿路梗阻的病人尤为多见。尿常规检查可有白细胞、红细胞甚或蛋白。血常规可能有白细胞升高。尿细菌培养阳性。",
    checking:"尿路感染应该做哪些检查?当患者满足下列条件之一者，可确诊为尿感：①典型尿路感染症状+脓尿(离心后尿沉渣镜检白细胞>5个/HP)+尿亚硝酸盐实验阳性;②清洁离心中段尿沉渣白细胞数或有尿路感染症状者>10个/HP;③有尿路感染症状者+正规清晨清洁中段尿细菌定量培养，菌落数≥105 /ml，且连续两次尿细菌计数≥105/ml，两次的细菌及亚型相同者;④作膀胱穿刺尿培养，如细菌阳性﹝不论菌数多少﹞;⑤典型尿路感染症状，治疗前清晨清洁中段尿离心尿沉渣革兰染色找细菌，细菌>1个/油镜视野。慢性肾盂肾炎：X线静脉肾盂造影(IVP)见到局灶、粗糙的皮质癫痕，伴有附属的肾乳头收缩的扩张和变钝等征象可确诊。",
    diagnosis:"1.病史采集（1）临床表现尿路感染相关症状的特点、持续时间及伴随症状；（2）既往史、药物史及相关疾病史等寻找发病的可能原因、伴随疾病、曾经的药物治疗史及可能影响疾病发展、转归的因素等；2.体格检查包括泌尿外生殖器的检查；腹部和肾区的体检。盆腔和直肠指诊对鉴别是否合并其他疾病有意义。3.辅助检查（1）实验室检查包括血常规、尿常规、尿涂片镜检细菌、中段尿细菌培养+药敏、血液细菌培养+药敏、肾功能检查等；（2）影像学检查包括超声、腹部平片、静脉肾盂造影等，必要时可选择CT或MRI检查。",
    prevention:"尿路感染应该如何预防?尿感的再发可分为复发和重新感染。一般认为，在尿路感染痊愈后的2周之内再次出现同一种细菌的感染则为尿路感染复发;相反，在尿路感染痊愈后的2周之后再次出现的感染，则无论致病菌是否与前一次相同，则均诊断为重新感染，可采取如下预防措施：1.一般措施：①多饮水，每天入量最好在2000ml以上，每2～3小时排尿一次。②性生活相关的患者，与性交后及时排尿，必要时需向妇产科医生咨询并选择适宜的避孕方式。③尽量避免尿路器械的使用。④蔓越橘汁(cranberry juice),实验研究显示蔓越橘汁可以阻止大肠埃希菌粘附在尿路上皮细胞上，可有助于预防尿路感染。2.抗生素预防：抗生素预防可以明显减少女性尿路感染复发的机会。对于在半年内尿路感染复发2 次或2次以上，或者1年内复发3次或3次以上的女性患者，推荐使用抗生素治疗(A级)。预防方案包括持续性给药法和性交后服药法，疗程6～12个月。这些方案必须在原有尿路感染痊愈后(停药1～2周后复查尿培养阴性)方可采用，并可根据以往的药敏实验结果以及患者的药物过敏史选择抗生素。和持续性给药方法相比，性交后服药法更方便，更易于被性生活相关的患者接受，可于性生活后2小时内服用头孢氨苄或环丙沙星或呋喃妥因3.绝经女性患者的预防：阴道局部应用雌激素软膏可以恢复阴道局部环境，可减少尿路感染的复发机会(A级)。4.对于频繁尿感再发的患者应详细检查其泌尿系统有无解剖畸形、基础病变(如结石、多囊肾、髓质海绵肾等)及整体免疫系统异常。",
    complication:"尿路感染可以并发哪些疾病?1.感染性肾结石：感染性肾结石由感染而成，是一种特殊类型的结石，约占肾结石的15%～20%，其主要成分是磷酸镁铵和磷酸磷灰石。感染性肾结石治疗困难，复发率高，如不妥善处理，则会使肾盂肾炎变为慢性，甚至导致肾功能衰竭。临床表现除有通常肾结石的表现外，还有它自己的特点。感染性结石生长快，常呈大鹿角状，X线平片上显影，常伴有持续的或反复发生变形杆菌等致病菌的尿感病史。本病可根据病史、体格检查、血尿化验和X线检查等作出诊断。病人常有变形杆菌尿路感染病史，尿pH>7，尿细菌培养阳性。治疗包括内科治疗、手术治疗和其他治疗方法。肾结石在0.7～1cm以下，表面光滑，可用内科治疗。目前尚无满意的溶石药物，通常需使用对细菌敏感的药物。其次，酸化尿液可用氯化铵等。手术治疗是重要的治疗措施，应劝病人尽早手术。其他治疗包括大量饮水、酸化尿液、利尿解痉等。2.肾周围炎和肾周围脓肿： 　肾包膜与肾周围筋膜之间的脂肪组织发生感染性炎症称为肾周围炎，如果发生脓肿则称为肾周围脓肿。本病多由肾盂肾炎直接扩展而来(90%)，小部分(10%)是血源性感染。本病起病隐袭，数周后出现明显临床症状，病人除肾盂肾炎症状加重外，常出现单侧明显腰痛和压痛，个别病人可在腹部触到肿块。炎症波及横膈时，呼吸及膈肌运动受到限制，呼吸时常有牵引痛，X线胸部透视，可见局部横膈隆起。由肾内病变引起者，尿中可有多量脓细胞及致病菌;病变仅在肾周围者只有少量白细胞。本病的诊断主要依靠临床表现，X线检查、肾盂造影、超声及CT有助确诊，治疗应尽早使用抗菌药物，促使炎症消退，若脓肿形成则切开引流。3.肾乳头坏死：肾乳头坏死可波及整个锥体，由乳头尖端至肾皮质和髓质交界处，有大块坏死组织脱落，小块组织可从尿中排出，大块组织阻塞尿路。因此肾盂肾炎合并肾乳头坏死时，除肾盂肾炎症状加重外，还可出现肾绞痛、血尿、高热、肾功能迅速变坏，并可并发革兰氏阴性杆菌败血症。如双肾均发生急性肾乳头坏死，病人可出现少尿或无尿，发生急性肾功能衰竭。本病的诊断主要依靠发病诱因和临床表现。确诊条件有二：①尿中找到脱落的肾乳头坏死组织，病理检查证实;②静脉肾盂造影发现环形征，和/或肾小盏边缘有虫蚀样改变，均有助于诊断。治疗应选用有效的抗生素控制全身和尿路感染;使用各种支持疗法改善病人的状态，积极治疗糖尿病、尿路梗阻等原发病。4.革兰氏阴性杆菌败血症： 　革兰氏阴性杆菌败血症中，由尿路感染引起者占55%。主要表现，起病时大多数病人可有寒战、高热、全身出冷汗，另一些病人仅有轻度全身不适和中等度发热。稍后病势可变得凶险，病人血压很快下降，甚至可发生明显的休克，伴有心、脑、肾缺血的临床表现，如少尿、氮质血症、酸中毒及循环衰竭等。休克一般持续3～6天，严重者可因此而死亡。本病的确诊有赖于血细菌培养阳性，故在应用抗菌药之前宜抽血作细菌培养和药敏试验，并在病程中反复培养。革兰氏阴性杆菌败血症的病死率为20%～40%，除去感染源是处理败血症休克的重要措施，常用措施为抗感染，纠正水、电解质和酸碱平衡紊乱，使用大量皮质类固醇激素，以减轻毒血症状;试用肝素预防和治疗DIC，通畅尿路。",
    treatment :""
  }
)

db.commondrugs.insert(
  {
     name:"感冒"

  }

)

db.drugsclassify.insert(
  {
     name:"诊断用药物",
     parentid:"root"

  }

)
db.drugsclassify.insert(
  {
     name:"影像诊断用药",
     parentid:"554c2c5384697c30fbb8ff89"

  }

)
db.drugsclassify.insert(
  {
     name:"其他诊断用药",
     parentid:"554c2c5384697c30fbb8ff89"

  }

)

db.drugdetail.insert(
  {
     name:"芬必得",
     drugtype:"甲类非处方药;医保药品",
     drugelement:"布洛芬",
     drugdose:"成人;一粒(300 mg)/次，1日2次(早晚各一次)。",
     parentids:["554af02984697c30fbb8ff86","554af11b84697c30fbb8ff87"]

  }
)

db.aidclassify.insert({
     name:"疾病急救",
     parentid:"root"

  })

db.aidclassify.insert({
     name:"日常急救",
     parentid:"root"

  })


db.aiddetail.insert({
     name:"咽部异物急救",
     content:"咽部异物常见的是鱼刺、骨片、果核、小针和假牙或牙托等引起。急救方法：咽分鼻咽、口咽、喉咽三部分，鼻咽和喉咽部异物必须请医生诊治。口咽部异物，如鱼刺、骨刺、缝针等很容易刺在口咽部扁桃体或其他附近组织上。处理时，一定要对着充足日光或灯光，光线能直射在口咽部，令病人张口，安静地呼吸，最好用压舌板或用两根筷子代替轻轻将舌头压下，使咽峡部露出十分清楚，如果是鱼刺，往往一端刺入组织，另一端暴露在外，呈白色，用镊子钳出，否则送医院处理。",
     parentids:["554c58f584697c30fbb8ff8d"]
  })
db.aiddetail.insert({
     name:"小儿惊厥急救",
     content:"惊厥俗称“抽风”，小儿因高热而抽风是常见的急症之一，通常有两种情况：一种是中枢神经系统的急性传染病，如脑膜炎、脑炎等，小儿发烧抽风后，神志不清或昏迷；另一种是上呼吸道感染时，因高热引起小儿惊厥，它不是脑子发炎，而是由于小儿大脑皮层下中枢神经的兴奋性比较高，而皮层的发育还不成熟，当遇到很强的刺激如体温骤然升高，大脑皮层对皮层下就不能很好控制，引起神经细胞暂时性功能紊乱，出现惊厥。小儿脑子本身没病，医学上称“高热惊厥”，多见于6个月～3岁的小儿。惊厥持续几秒钟到几分钟，多不超过10分钟，发作过后，神志清楚。婴幼儿高热惊厥发病率较高，因此，小儿抽风时，在准备送医院的同时，应进行家庭急救：1.家长和保育员首先要保持镇静，切勿惊慌失措。应迅速将小儿抱到床上，使之平卧，解开衣扣、衣领、裤带，采用物理方法降温。2.用手指甲掐人中穴人中穴位于鼻唇沟上1/3与下2/3交界处，将患儿头偏向一侧，以免痰液吸入气管引起窒息，用裹布的筷子或小木片塞在患儿的上、下牙之间，以免咬伤舌头并保障通气。3.小儿抽风时，不能喂水、进食，以免误入气管发生窒息与引起肺炎。家庭处理的同时最好先就近求治，在注射镇静及退烧针后，一般抽风就能停止。切忌长途奔跑去大医院，使抽风不能在短期内控制住，会引起小儿脑缺氧，造成脑水肿甚至脑损害，最终影响小儿智力，个别患儿甚至死亡。4.止抽后，应及时去医院就诊，以便明确诊断，避免延误治疗",
     parentids:["554c58ed84697c30fbb8ff8c"]
  })



db.assayclassify.insert({
      name:"血液及骨髓检查",
      parentid:"root"
})
db.assayclassify.insert({
      name:"免疫学检查",
      parentid:"554c740b84697c30fbb8ff90"
})

db.assaydetail.insert({
      name:"B因子溶血活性",
      info:"补体激活的旁路途径(alternativepathway，AP)激活时，补体前段成分(C1,4,2)不活化。参与AP激活的除C3～C9外，尚有P、D、B等因子。",
      normalvalue:"83%～121%。",
      meaning:"补体旁路途径活化，参与的成分为补体C3、C5～C9、P因子、D因子、B因子等，其中任何成分的异常都可引起旁路途径溶血活性的改变。AP-CH50溶血活性显著增高见于某些自身免疫病、肾病综合征、慢性肾炎、肿瘤、感染等，降低则见于肝硬化、慢活肝、急性肾炎等病症。",
      parentids:["554c745584697c30fbb8ff91"]
})







db.enumerate.insert({
 "enumeratename" : "肾内科",
 "enumeratevalue" : "肾内科",
 "enumeratetype" : "section"
})

儿　科
儿童智力开发中心
耳鼻咽喉科
妇　科
骨　科
急诊科
神经精神科
口腔科
泌尿外科
皮肤性病科
神经外科
消化内科(肾内科)
心血管内科
眼　科
中医科
肿瘤外科
老年内科（呼吸、内分泌科）
医疗美容科
普外、微创、肝胆外科
糖尿病手术治疗中心
血液内科、中西医结合科
肛肠外科
重症医学科（ICU）











